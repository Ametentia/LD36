package com.perceptiongames.engine.Handlers.Terrain;

import com.perceptiongames.engine.Entities.AABB;
import com.perceptiongames.engine.Entities.Entity;
import com.perceptiongames.engine.Handlers.Animation;

public class Tile extends Entity {

    public static final int SIZE = 80;

    public Tile(Animation animation, AABB aabb) {
        super(animation, "Main", aabb);
    }
}
