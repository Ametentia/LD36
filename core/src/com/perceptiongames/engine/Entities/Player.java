package com.perceptiongames.engine.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.perceptiongames.engine.Game;
import com.perceptiongames.engine.Handlers.Animation;

import java.util.HashMap;

public class Player extends Entity {

    private Vector2 velocity;
    private boolean onGround;
    private boolean onLadder;

    private int health = 1;
    private int numberDeaths = 0;

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
    }

    public void reset(Vector2 position) {
        setPosition(new Vector2(position));
        health = 1;
        live = true;
    }

    /**
     * Handles the user input to effect the player movement
     */
    private void handleInput() {

        if(onGround && getAnimationKey().contains("attack"))
            velocity.x = 0;

        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) { //Checks if the player is on the ground and if they want to jump
            setVelocity(velocity.x, -940f); //Sets their velocity to the escape jump speed
            onGround = false;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.A)) {
            if(Gdx.input.isTouched()) {
                setCurrentAnimation("attackLeft", 1);
                if(onGround) velocity.x = 0;
            }
            else if(!getAnimationKey().contains("attack")) {
                setVelocity(-500f, velocity.y); //Sets the velocity to the left at a 500 units/s speed
                if ((aabb.getCollisionFlags() & AABB.RIGHT_BITS) == AABB.RIGHT_BITS) {
                    if(onLadder) {
                        velocity.y = -350f;
                    }
                    setCurrentAnimation("pushLeft");
                } else {
                    this.setCurrentAnimation("moveLeft");
                }
            }
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.D)) {
            if(Gdx.input.isTouched()) {
                setCurrentAnimation("attackRight", 1);

                if(onGround) velocity.x = 0;
            }
            else if(!getAnimationKey().contains("attack")) {
                if ((aabb.getCollisionFlags() & AABB.LEFT_BITS) == AABB.LEFT_BITS) {
                    velocity.x = 0;
                    if(onLadder) {
                        velocity.y = -350f;
                    }
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

        int flags = aabb.getCollisionFlags();
        if((flags & AABB.TOP_BITS) == AABB.TOP_BITS) {
            onGround = true;
            if(velocity.y > 0 && !onLadder) {
                velocity.y = 0;
            }
        }

        if((flags & AABB.BOTTOM_BITS) == AABB.BOTTOM_BITS) {
            onGround = false;
            if(velocity.y < 0 && !onLadder) {
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

        if(!onGround && !onLadder) { //If not on ground apply gravity to the player, unless terminal is reached
           velocity.y = Math.min(velocity.y + 2300f*dt, 2500f);
        }
        else if(!onLadder) {
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
        aabb.setCollisionFlags(AABB.NONE_BITS);
        onLadder = false;
    }

    public Vector2 getVelocity() { return velocity; }
    public boolean isOnGround() { return onGround; }
    public boolean isOnLadder() { return onLadder; }
    public int getNumberDeaths() { return numberDeaths; }
    public int getHealth() { return health; }

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

    public void incrementDeaths() { numberDeaths++; }

    public void setOnLadder() { onLadder = true; }
}
