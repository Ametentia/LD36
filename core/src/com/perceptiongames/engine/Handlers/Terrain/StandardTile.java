package com.perceptiongames.engine.Handlers.Terrain;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.perceptiongames.engine.Entities.AABB;


public class StandardTile extends Tile {

    private Texture texture;
    private boolean isLadder;

    public StandardTile(Texture texture, AABB aabb, boolean ladder) {
        super(aabb);

        this.texture = texture;
        isLadder = ladder;
        aabb.setSensor(ladder);
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(texture, aabb.getPosition().x, aabb.getPosition().y, texture.getWidth(), texture.getHeight(),
                0, 0, texture.getWidth(), texture.getHeight(), false, true);
    }

    public boolean isLadder() { return isLadder; }
    public void setLadder(boolean l) { isLadder = l; }
}