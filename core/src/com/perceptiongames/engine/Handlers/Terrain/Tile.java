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
    private boolean active;

    public Tile(Texture texture, AABB aabb) {
        this.aabb = aabb;
        this.texture = texture;

        damage = 0;
        active = false;
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, aabb.getPosition().x, aabb.getPosition().y, texture.getWidth(), texture.getHeight(),
                0, 0, texture.getWidth(), texture.getHeight(), false, true);
    }

    public AABB getAABB() { return aabb; }
    public Texture getTexture() { return texture; }
    public int getDamage() { return damage; }

    public void setDamage(int dmg) { damage = dmg; }
    public void setActive(boolean a) { active = a; }
}
