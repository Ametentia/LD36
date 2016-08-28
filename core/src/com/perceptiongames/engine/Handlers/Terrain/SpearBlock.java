package com.perceptiongames.engine.Handlers.Terrain;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.perceptiongames.engine.Entities.AABB;
import com.perceptiongames.engine.Handlers.Animation;

public class SpearBlock extends Tile {
    private Animation animation;

    public SpearBlock(Animation a, AABB aabb) {
        super(aabb);
        active = false;
        animation = a;
        animation.setPosition(aabb.getPosition());
        animation.setMaxPlays(1);
    }

    @Override
    public void render(SpriteBatch batch) {
        animation.render(batch);
    }

    public void update(float dt) {
        if(active) {
            animation.update(dt);
            if (animation.getCurrentFrame() < 9) {
                aabb.setHalfSize(40 + (5 * animation.getCurrentFrame()), aabb.getHalfSize().y);
            } else {
                aabb.setHalfSize(40 + (5 * (animation.getTotalFrames() - animation.getCurrentFrame())), aabb.getHalfSize().y);
            }
            aabb.setPosition(animation.getPosition());
            if(animation.isFinished()) {
                active = false;
                animation.reset();
                animation.setMaxPlays(1);
                aabb.setHalfSize(40, 40);
            }
        }
    }
}
