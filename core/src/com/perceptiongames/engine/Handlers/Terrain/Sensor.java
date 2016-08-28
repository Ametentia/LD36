package com.perceptiongames.engine.Handlers.Terrain;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.perceptiongames.engine.Entities.AABB;

public class Sensor extends Tile {

    private boolean playerColliding;

    public Sensor(int row, int col, AABB aabb) {
        super(aabb);
        setRow(row);
        setColumn(col);

        aabb.setSensor(true);

        playerColliding = false;
    }

    public boolean isPlayerColliding() { return playerColliding; }

    public void setPlayerColliding(boolean b) { playerColliding = b; }
}
