package com.perceptiongames.engine.Handlers.Terrain;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.perceptiongames.engine.Entities.AABB;

public class FallingBlock extends Tile {

    private Texture texture;

    private float velocity;
    private float yBound;

    private boolean isPlayerColliding;

    private boolean alive;

    public FallingBlock(Texture texture, AABB aabb) {
        super(aabb);
        this.texture = texture;
        active = false;

        isPlayerColliding = false;
        alive = true;

        yBound = aabb.getPosition().y + (Tile.SIZE * 3);
    }

    @Override
    public void render(SpriteBatch batch) {
        if(!alive) return;
        batch.draw(texture, aabb.getPosition().x, aabb.getPosition().y, texture.getWidth(), texture.getHeight(),
                0, 0, texture.getWidth(), texture.getHeight(), false, true);
    }

    @Override
    public void update(float dt) {
        if(active && isPlayerColliding && alive) {
            float newPos = aabb.getPosition().y + (velocity * dt);
            aabb.setPosition(aabb.getPosition().x, newPos);

            if(aabb.getPosition().y >= yBound) alive = false;
        }
    }

    public float getVelocity() { return velocity; }
    public boolean isAlive() { return alive; }
    public boolean isPlayerColliding() { return isPlayerColliding; }

    public void setPlayerColliding(boolean c) { isPlayerColliding = c; }
    public void setVelocity(float y) { velocity = y; }
}
