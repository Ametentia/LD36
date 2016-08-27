package com.perceptiongames.engine.Entities;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.perceptiongames.engine.Game;
import com.perceptiongames.engine.Handlers.Animation;

public class Player extends Entity {

    private Vector2 velocity;
    private boolean onGround;

    private int health = 1;

    private ShapeRenderer renderer;
    private OrthographicCamera camera;

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

        renderer = new ShapeRenderer();
    }

    public void reset(Vector2 position) {
        setPosition(position);
        health = 1;
        live = true;
    }

    /**
     * Handles the user input to effect the player movement
     */
    private void handleInput() {
        boolean moving = false;  // Sets the player to not moving

        if(onGround && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) { //Checks if the player is on the ground and if they want to jump
            setVelocity(velocity.x, -940f); //Sets their velocity to the escape jump speed
            onGround = false;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.A)) {
            setVelocity(-500f, velocity.y); //Sets the velocity to the left at a 500 units/s speed
            moving = true;
            this.setCurrentAnimation("moveLeft");
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.D)) {
            setVelocity(500f, velocity.y); //Sets the velocity to the right at a 500 units/s speed
            moving = true;
            this.setCurrentAnimation("moveRight");
        }
        else {
            setVelocity(0, velocity.y); //If not pressing a direction key set the x velocity to 0
            this.setCurrentAnimation("idle");
        }
        if(Gdx.app.getType() == Application.ApplicationType.Android && Gdx.input.isTouched(0)) //very basic movement on android
        {
            if(Gdx.input.getY(0)<Game.HEIGHT/2 && onGround) {
                setVelocity(velocity.x, -940f);
                onGround = false;
            }
            if(Gdx.input.getY(0)>Game.HEIGHT/2)
            {
                if (Gdx.input.getX(0) > Game.WIDTH / 2)
                    setVelocity(500f, velocity.y);
                else if (Gdx.input.getX(0) < Game.WIDTH / 2)
                    setVelocity(-500f, velocity.y);
                else
                    setVelocity(0, velocity.y);
            }
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

        if(flags == AABB.NONE_BITS) {
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

        renderer.setProjectionMatrix(camera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(1, 0, 0, 1);
        aabb.debugRender(renderer);
        setPosition(newPos);
        renderer.setColor(0, 1, 0, 1);
        aabb.debugRender(renderer);
        super.update(dt);
        renderer.setColor(0, 0, 1, 1);
        aabb.debugRender(renderer);
        renderer.end();
        aabb.setCollisionFlags(AABB.NONE_BITS);
    }

    public Vector2 getVelocity() { return velocity; }
    public boolean isOnGround() { return onGround; }

    public void setVelocity(float x, float y) {
        setVelocity(new Vector2(x, y));
    }
    public void setVelocity(Vector2 v) {
        velocity.set(v);
    }

    public void setCamera(OrthographicCamera camera) { this.camera = camera; }

    public void hit() {
        health--;
        if(health == 0)
            live = false;
    }
}
