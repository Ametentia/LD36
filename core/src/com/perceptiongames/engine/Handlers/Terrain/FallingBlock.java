package com.perceptiongames.engine.Handlers.Terrain;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.perceptiongames.engine.Entities.AABB;

public class FallingBlock extends Tile {

    private Texture texture;

    public FallingBlock(Texture texture, AABB aabb) {
        super(aabb);
        this.texture = texture;
        active = false;
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(texture, aabb.getPosition().x, aabb.getPosition().y, texture.getWidth(), texture.getHeight(),
                0, 0, texture.getWidth(), texture.getHeight(), false, true);
    }
}
