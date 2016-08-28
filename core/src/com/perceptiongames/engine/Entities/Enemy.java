package com.perceptiongames.engine.Entities;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.perceptiongames.engine.Game;
import com.perceptiongames.engine.Handlers.Animation;

import static com.badlogic.gdx.math.MathUtils.floor;
import static java.lang.Math.sin;

public class Enemy extends Entity {

    float ticker;
    Vector2 pos = getPosition();
    Vector2 velocity = new Vector2();
    boolean onGround;

    public Enemy(Animation animation, String animationName, AABB aabb) {
        super(animation, animationName, aabb);
        onGround=false;
    }

    @Override
    public void update(float dt) {

        float X = floor(pos.x/80);
        float Y = floor(pos.y/80);

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
        if(!onGround) {
            velocity.y = Math.min(velocity.y + 2300f*dt, 2500f);
        }
        else
        {
            velocity.y=0;
        }
        System.out.println("On ground" + onGround);
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
        this.setPosition(newPos);
        ticker += dt;
        super.update(dt);
        aabb.setCollisionFlags(AABB.NONE_BITS);
    }
}
