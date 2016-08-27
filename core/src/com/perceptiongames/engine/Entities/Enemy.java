package com.perceptiongames.engine.Entities;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.perceptiongames.engine.Handlers.Animation;

import static com.badlogic.gdx.math.MathUtils.floor;
import static java.lang.Math.sin;

public class Enemy extends Entity {

    @Override

    public void update(float dt) {
        super.update(dt);

        float ticker = 0;

        Vector2 Pos = getPosition();
        float PosX = Pos.x;
        float PosY = Pos.y;

        float X = floor(PosX/80);
        float Y = floor(PosY/80);

        PosX += 60*MathUtils.sin(ticker);

        ticker += 1/60;

    }

    public Enemy(Animation animation, String animationName, AABB aabb) {
        super(animation, animationName, aabb);


    }
}
