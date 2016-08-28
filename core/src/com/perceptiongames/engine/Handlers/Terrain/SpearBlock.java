package com.perceptiongames.engine.Handlers.Terrain;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.perceptiongames.engine.Entities.AABB;
import com.perceptiongames.engine.Handlers.Animation;

public class SpearBlock extends Tile {

    private Animation animation;

    private boolean facingLeft;

    public SpearBlock(Animation a, AABB aabb, boolean facingLeft) {
        super(aabb);
        active = false;
        this.facingLeft = facingLeft;
        a.setFlipX(facingLeft);

        animation = a;
        if(facingLeft)
            animation.setPosition(new Vector2(aabb.getPosition().x - aabb.getWidth(), aabb.getPosition().y));
        else
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
                if(facingLeft) aabb.setCentre(new Vector2(animation.getPosition().x + 120
                        - (5 * animation.getCurrentFrame()), aabb.getCentre().y));
            } else {

                aabb.setHalfSize(40 + (5 * (animation.getTotalFrames() - animation.getCurrentFrame())), aabb.getHalfSize().y);
                if(facingLeft) aabb.setCentre(new Vector2(animation.getPosition().x + 120
                        - (5 * (animation.getTotalFrames() - animation.getCurrentFrame())), aabb.getCentre().y));
            }
            if(!facingLeft) {
                aabb.setPosition(animation.getPosition());
            }

            if(animation.isFinished()) {
                active = false;
                animation.reset();
                animation.setMaxPlays(1);
                aabb.setHalfSize(40, 40);
                if(!facingLeft)
                    aabb.setPosition(animation.getPosition());
                else
                    aabb.setPosition(animation.getPosition().x + 80, animation.getPosition().y);
            }
        }
    }

    public boolean isFacingLeft() { return facingLeft; }

    public void setFacingLeft(boolean facing) { facingLeft = facing; }

    public Animation getAnimation() { return animation; }
}
