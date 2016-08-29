package com.perceptiongames.engine.Entities;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.perceptiongames.engine.Game;
import com.perceptiongames.engine.Handlers.Animation;

import static com.badlogic.gdx.math.MathUtils.floor;
import static com.badlogic.gdx.math.MathUtils.random;
import static java.lang.Math.sin;

public class Enemy extends Entity {

    private float ticker;
    private Vector2 pos = getPosition();
    private Vector2 velocity = new Vector2();
    private boolean onGround;
    private int[] actions;
    private int current;

    private boolean attacking;
    private Vector2 weaponOffset;
    private AABB weapon;

    public Enemy(Animation animation, String animationName, AABB aabb) {
        super(animation, animationName, aabb);
        onGround=false;

        actions = new int[] { 0, 1, 2, 3 };
        current=3;

        attacking = false;
        weaponOffset = new Vector2();
    }

    @Override
    public void update(float dt) {

        if(ticker > 6) {
            ticker = 0;
            current = random.nextInt(4)+1;
        }


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
        flags = aabb.getCollisionFlags();
        switch(current) {
            case 1:
                velocity.x=75;
                this.setCurrentAnimation("Right");
                if((flags&AABB.RIGHT_BITS) == AABB.RIGHT_BITS) {
                    velocity.add(0, -400);
                }
                attacking = false;
                break;
            case 2:
                velocity.x=-75;
                this.setCurrentAnimation("Left");
                if((flags&AABB.LEFT_BITS) == AABB.LEFT_BITS) {
                    velocity.add(0,-400);
                }
                attacking = false;
                break;
            case 3:
                this.setCurrentAnimation("idle");
                attacking = false;
                break;
            case 4:
                this.setCurrentAnimation("attack");
                attacking = true;
                break;
        }
        Vector2 newPos = new Vector2();
        newPos.x = getPosition().x + (velocity.x * dt); // Speed = distance / time, simple physics
        newPos.y = getPosition().y + (velocity.y * dt);

        if(attacking) {
            Animation a = getAnimation(getAnimationKey());
            switch (a.getCurrentFrame()) {
                case 0:
                    weaponOffset.x = 29;
                    weaponOffset.y = -13;
                    break;
                case 1:
                    weaponOffset.x = 30;
                    weaponOffset.y = 4;
                    break;
                case 2:
                    weaponOffset.x = 15;
                    weaponOffset.y = 14;
                    break;
                case 3:
                    weaponOffset.x = -13;
                    weaponOffset.y = 22;
                    break;
                case 4:
                    weaponOffset.x = -24;
                    weaponOffset.y = 12;
                    break;
                case 5:
                    weaponOffset.x = -31;
                    weaponOffset.y = -12;
                    break;
                case 6:
                    weaponOffset.x = 16;
                    weaponOffset.y = -24;
                    break;
            }
        }
        else {
            weaponOffset.x = weaponOffset.y = 0;
        }

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
        weapon.setCentre(new Vector2(aabb.getCentre().x + weaponOffset.x, aabb.getCentre().y + weaponOffset.y));
        super.update(dt);
        aabb.setCollisionFlags(AABB.NONE_BITS);
    }

    public void hit()
    {
        this.live=false;
    }
    public int getCurrent() { return current; }
    public AABB getWeapon() { return weapon; }
    public boolean isAttacking() { return attacking; }

    public void setWeapon(AABB weapon) {
        this.weapon = weapon;
        this.weapon.setSensor(true);
    }
}
