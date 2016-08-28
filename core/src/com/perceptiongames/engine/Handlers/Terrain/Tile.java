package com.perceptiongames.engine.Handlers.Terrain;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.perceptiongames.engine.Entities.AABB;
import com.perceptiongames.engine.Entities.Entity;
import com.perceptiongames.engine.Handlers.Animation;

public abstract class Tile {

    public static final int SIZE = 80;

    protected AABB aabb;

    protected int damage;
    protected boolean active;

    protected int row;
    protected int column;

    public Tile(AABB aabb) {
        this.aabb = aabb;

        damage = 0;
        active = false;
    }

    public void update(float dt) {}
    public void render(SpriteBatch batch) {}

    public AABB getAABB() { return aabb; }
    public int getDamage() { return damage; }

    public int getRow() { return row; }
    public int getColumn() { return column; }

    public void setDamage(int dmg) { damage = dmg; }
    public void setActive(boolean a) { active = a; }

    public void setRow(int row) { this.row = row; }
    public void setColumn(int col) { column = col; }
}
