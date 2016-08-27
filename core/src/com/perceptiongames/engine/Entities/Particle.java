package com.perceptiongames.engine.Entities;

import com.badlogic.gdx.math.Vector2;
import com.perceptiongames.engine.Handlers.Animation;

/**
 * Created by matt on 20/08/16. A class that hopefully should cover a diverse set of particles
 * Very much a work in progress, currently wondering how to get these to spawn constantly
 */

public class Particle extends Entity{

    private Vector2 velocity;
    private Vector2 acceleration;


    public Particle(Animation animation, String animationKey,AABB AABB) {
        super(animation, animationKey,AABB);
        velocity = new Vector2();
        acceleration = new Vector2();
    }
    @Override
    public void update(float dt)
    {
        setPosition(getPosition().x + velocity.x*dt + 0.5f*acceleration.x*acceleration.x, getPosition().y + velocity.y*dt + 0.5f*acceleration.y*acceleration.y);
        if(getAABB().getCollisionState()!= AABB.CollisionState.NONE)
        {
            setLive(false);
        }
    }

    //setters
    public void setVelocity(Vector2 velocity) {this.velocity = velocity;}
    public void setAcceleration(Vector2 acceleration) {this.acceleration = acceleration;}
    public void setAcceleration(float x,float y) {setAcceleration(new Vector2(x,y));}
    //getters
    public Vector2 getVelocity() {return velocity;}
    public Vector2 getAcceleration() {return acceleration;}


}
