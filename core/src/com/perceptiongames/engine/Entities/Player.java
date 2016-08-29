package com.perceptiongames.engine.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.perceptiongames.engine.Game;
import com.perceptiongames.engine.Handlers.Animation;
import com.perceptiongames.engine.States.Play;

import java.util.ArrayList;
import java.util.List;

public class Player extends Entity {

    private Vector2 velocity;
    private boolean onGround;

    private AABB weapon;
    private float weaponOffset;

    private int health = 1;
    private int numberDeaths = 0;
    private List<Sound> sounds;
    private float lastAttack=0;

    private int enemiesKilled;

    private boolean attacking;

    private int totalPoints;

    /**
     * Sets up the local variables for the player
     * @param animation The animation for the player
     * @param animationKey The name for the initial animation
     * @param aabb The collision box for the player
     */
    public Player(Animation animation,String animationKey, AABB aabb) {
        super(animation, animationKey,  aabb);
        velocity = new Vector2(); //Sets up a vector with values 0,0

        onGround = false; //Sets the player to off the ground by default

        attacking = false;
        sounds = new ArrayList<Sound>();

        enemiesKilled = 0;
        totalPoints = 0;
    }

    public void reset(Vector2 position) {
        setCurrentAnimation("idle");
        health = 1;
        live = true;
        setPosition(new Vector2(position));
        velocity = new Vector2(0, 0);
    }

    /**
     * Handles the user input to effect the player movement
     */
    private void handleInput() {

        if(getAnimationKey().contains("attack")) {
            velocity.x = 0;
            Animation current = getAnimation(getAnimationKey());
            switch (current.getCurrentFrame() + 1) {
                case 1:
                    weaponOffset = 17;
                    break;
                case 2:
                    weaponOffset = 23;
                    break;
                case 3:
                    weaponOffset = 32;
                    break;
                case 4:
                    weaponOffset = 26;
                    weaponOffset = 26;
                    break;
            }
            if(getAnimationKey().equals("attackLeft")) weaponOffset = -weaponOffset;
        }
        else {
            if(attacking) {
                attacking = false;
                weaponOffset = 0;
            }
        }


        if(Gdx.input.isKeyPressed(Input.Keys.SPACE) &&onGround) { //Checks if the player is on the ground and if they want to jump
            setVelocity(velocity.x, -940f); //Sets their velocity to the escape jump speed
            onGround = false;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.A)) {
            if(Gdx.input.isTouched()&& lastAttack>1) {
                lastAttack=0;
                sounds.get(0).play(Play.AUDIO_VOLUME);
                setCurrentAnimation("attackLeft", 1);
                attacking = true;
                if(onGround) velocity.x = 0;
            }
            else if(!getAnimationKey().contains("attack")) {
                setVelocity(-500f, velocity.y); //Sets the velocity to the left at a 500 units/s speed
                if ((aabb.getCollisionFlags() & AABB.RIGHT_BITS) == AABB.RIGHT_BITS) {
                    setCurrentAnimation("pushLeft");
                } else {
                    this.setCurrentAnimation("moveLeft");
                }
            }
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.D)) {
            if(Gdx.input.isTouched() &&lastAttack>1) {
                lastAttack=0;
                sounds.get(0).play(Play.AUDIO_VOLUME);
                setCurrentAnimation("attackRight", 1);
                attacking = true;

                if(onGround) velocity.x = 0;
            }
            else if(!getAnimationKey().contains("attack")) {
                if ((aabb.getCollisionFlags() & AABB.LEFT_BITS) == AABB.LEFT_BITS) {
                    velocity.x = 0;
                    setCurrentAnimation("pushRight");
                }
                else {
                    setVelocity(500f, velocity.y); //Sets the velocity to the right at a 500 units/s speed
                    this.setCurrentAnimation("moveRight");
                }
            }
        }
        else {
            setVelocity(0, velocity.y); //If not pressing a direction key set the x velocity to 0
            if(!getAnimationKey().contains("attack"))
                this.setCurrentAnimation("idle");
        }
    }

    /**
     * Runs every frame to update the player
     * @param dt The time since the last update
     */
    @Override
    public void update(float dt) {
        if(!live) return;
        lastAttack+=dt;

        int flags = aabb.getCollisionFlags();
        if((flags & AABB.TOP_BITS) == AABB.TOP_BITS) {
            onGround = true;
            if(velocity.y > 0) {
                velocity.y = 0;
            }
        }

        if((flags & AABB.BOTTOM_BITS) == AABB.BOTTOM_BITS) {
            onGround = false;
            if(velocity.y < 0) {
                velocity.y = 0;
            }
        }

        if((flags & AABB.LEFT_BITS) == AABB.LEFT_BITS) {
            velocity.x = 0;
        }

        if((flags & AABB.LEFT_BITS) == AABB.LEFT_BITS) {
            velocity.x = 0;
        }

        if(flags == AABB.NONE_BITS || flags == (AABB.SENSOR_BITS | AABB.NONE_BITS)) {
            if(getPosition().y + aabb.getHeight() == Game.WORLD_HEIGHT) { //If no collision, check if its on the world floor
                onGround = true;
            }
            else {
                onGround = false;
            }
        }

        handleInput();

        if(!onGround) { //If not on ground apply gravity to the player, unless terminal is reached
           velocity.y = Math.min(velocity.y + 2300f*dt, 2500f);
        }
        else {
            velocity.y = 0; //Set the y velocity to 0 if the player is on the ground
        }


        Vector2 newPos = new Vector2();
        newPos.x = getPosition().x + (velocity.x * dt); // Speed = distance / time, simple physics
        newPos.y = getPosition().y + (velocity.y * dt);
        if(newPos.x < 0) { //Because speed never hit 0, we make it 0 if its under 1
            newPos.x = 0;
            velocity.x = 0;
        }
        else if(newPos.x + aabb.getWidth() > Game.WORLD_WIDTH) {
            newPos.x = Game.WORLD_WIDTH - aabb.getWidth();
            velocity.x = 0;
        }

        if(newPos.y < 0) {
            newPos.y = 0;
            velocity.y = 0;
        }
        else if(newPos.y + aabb.getHeight() > Game.WORLD_HEIGHT) {
            newPos.y = Game.WORLD_HEIGHT - aabb.getHeight();
            velocity.y = 0;
            onGround = true;
        }

        setPosition(newPos);
        super.update(dt);

        weapon.setCentre(new Vector2(aabb.getCentre().x + weaponOffset, aabb.getCentre().y + 7));
        aabb.setCollisionFlags(AABB.NONE_BITS);
    }

    public Vector2 getVelocity() { return velocity; }
    public boolean isOnGround() { return onGround; }
    public int getNumberDeaths() { return numberDeaths; }
    public int getHealth() { return health; }
    public int getTotalPoints() { return totalPoints; }

    public void setVelocity(float x, float y) {
        setVelocity(new Vector2(x, y));
    }
    public void setVelocity(Vector2 v) {
        velocity.set(v);
    }

    public void hit() {
        health--;
        if(health == 0)
            live = false;
    }

    public AABB getWeapon() { return weapon; }
    public boolean isAttacking() { return attacking; }
    public int getEnemiesKilled() { return enemiesKilled; }
    public List<Sound> getSounds() { return sounds; }

    public void incrementEnemiesKillled() { enemiesKilled++; }
    public void setEnemiesKilled(int k) { enemiesKilled = k; }
    public void incrementDeaths() { numberDeaths++; }
    public void setSounds(List<Sound> sounds) { this.sounds = sounds; }
    public void setWeapon(AABB weapon) {
        this.weapon = weapon;
        weapon.setSensor(true);
    }
    public void addPoints(int points) { totalPoints += points; }
}
