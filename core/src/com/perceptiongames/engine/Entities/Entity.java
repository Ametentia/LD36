package com.perceptiongames.engine.Entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.perceptiongames.engine.Handlers.Animation;

import java.util.HashMap;

/**
 * Represents an object which can be added to the world.
 * Cannot be instantiated
 */
public abstract class Entity {

    protected HashMap<String, Animation> animations;
    private String currentAnimation;
    private String prevAnimation;
    protected boolean live; //whether or not the entity is alive or not

    protected AABB aabb;

    // Constructors
    public Entity(Animation animation, String animationName, AABB aabb) {
        this.live = true;
        this.animations = new HashMap<String, Animation>();
        this.aabb = aabb;

        animations.put(animationName, animation);
        currentAnimation = prevAnimation = animationName;
        this.animations.get(currentAnimation).setPosition(getPosition());
    }

    // Methods
    public void update(float dt) {
        animations.get(currentAnimation).update(dt);
        animations.get(currentAnimation).setPosition(aabb.getPosition());
        if(animations.get(currentAnimation).isFinished()) {
            animations.get(currentAnimation).reset();
            currentAnimation = prevAnimation;
        }
    }

    public void render(SpriteBatch batch) { if(live) { animations.get(currentAnimation).render(batch); } }

    // Getters
    public AABB getAABB() { return aabb; }
    public Animation getAnimation(String key) { return animations.get(key); }
    public Vector2 getPosition() { return aabb.getPosition(); }
    public String getAnimationKey() { return currentAnimation; }
    public void addAnimation(String key, Animation animation) { animations.put(key,animation); }
    public boolean isLive() { return live; }

    // Setters
    public void setPosition(float x, float y) { setPosition(new Vector2(x, y)); }
    public void setPosition(Vector2 position) {
        aabb.setCentre(position.add(aabb.getHalfSize()));
    }
    public void setCurrentAnimation(String animation) {
        if(!animations.containsKey(animation))
            throw new IllegalArgumentException("Error: Animation " + animation + " does not exist in this entity");

        prevAnimation = currentAnimation.equals(animation) ? prevAnimation : currentAnimation;
        currentAnimation = animation;
    }

    public void setCurrentAnimation(String animation, int plays) {
        if(!animations.containsKey(animation))
            throw new IllegalArgumentException("Error: Animation " + animation + " does not exist in this entity");

        prevAnimation = "idle";
        currentAnimation = animation;
        animations.get(currentAnimation).setMaxPlays(plays);
    }
    public void setLive(boolean live) { this.live = live; }
}
