package com.perceptiongames.engine.Handlers.Terrain;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.perceptiongames.engine.Entities.AABB;
import com.perceptiongames.engine.Entities.Entity;
import com.perceptiongames.engine.Handlers.Animation;

public class Tile {

    public static final int SIZE = 80;

    private AABB aabb;
    private Texture texture;

    private int damage;

    public Tile(Texture texture, AABB aabb) {
        this.aabb = aabb;
        this.texture = texture;

        damage = 0;
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, aabb.getPosition().x, aabb.getPosition().y);
    }

    public AABB getAABB() { return aabb; }
    public Texture getTexture() { return texture; }
    public int getDamage() { return damage; }

    public void setDamage(int dmg) { damage = dmg; }
}
