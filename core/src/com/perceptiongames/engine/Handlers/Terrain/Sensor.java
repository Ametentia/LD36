package com.perceptiongames.engine.Handlers.Terrain;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.perceptiongames.engine.Entities.AABB;

public class Sensor extends Tile {

    private boolean playerColliding;

    private String data;

    public Sensor(int row, int col, AABB aabb) {
        super(aabb);
        setRow(row);
        setColumn(col);

        aabb.setSensor(true);

        playerColliding = false;
    }

    public boolean isPlayerColliding() { return playerColliding; }
    public String getData() { return data; }

    public void setPlayerColliding(boolean b) { playerColliding = b; }
    public void setData(String s) { data = s; }
}
