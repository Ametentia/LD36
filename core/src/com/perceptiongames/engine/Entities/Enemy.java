package com.perceptiongames.engine.Entities;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.perceptiongames.engine.Handlers.Animation;

import static com.badlogic.gdx.math.MathUtils.floor;
import static java.lang.Math.sin;

public class Enemy extends Entity {

    float ticker;
    Vector2 pos = getPosition();

    @Override
    public void update(float dt) {
        super.update(dt);

        float X = floor(pos.x/80);
        float Y = floor(pos.y/80);

        pos.add(60*MathUtils.sin(ticker),0);

        ticker += dt;

    }

    public Enemy(Animation animation, String animationName, AABB aabb) {
        super(animation, animationName, aabb);


    }
}
