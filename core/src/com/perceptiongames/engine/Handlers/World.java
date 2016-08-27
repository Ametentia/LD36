package com.perceptiongames.engine.Handlers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.perceptiongames.engine.Entities.AABB;
import com.perceptiongames.engine.Entities.Entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class World {

    private List<Entity> staticBodies;
    private List<Entity> dynamicBodies;

    public World() {
        staticBodies = new ArrayList<Entity>();
        dynamicBodies = new ArrayList<Entity>();
    }

    public void update(float dt) {

        for(Entity e : staticBodies)
            e.update(dt);

        for(Entity e : dynamicBodies)
            e.update(dt);
        resetCollision();
        for(Entity d : dynamicBodies) {
            for(Entity s : staticBodies) {
                if(d.getAABB().overlaps(s.getAABB())) {
                    d.getAnimation(d.getAnimationKey()).setPosition(d.getPosition());
                }
            }
        }

        for (int i = 0; i < dynamicBodies.size(); i++) {
            for (int j = i + 1; j < dynamicBodies.size(); j++) {
                dynamicBodies.get(i).getAABB().overlaps(
                        dynamicBodies.get(j).getAABB()
                );
            }
        }
    }

    public void render(SpriteBatch batch) {
        for (Entity e : staticBodies)
            e.render(batch);

        for(Entity e : dynamicBodies)
            e.render(batch);
    }

    private void resetCollision() {
        for(Entity e : dynamicBodies) {
            e.getAABB().setCollisionState(AABB.CollisionState.NONE);
        }
    }

    public void addStatic(Entity s) { staticBodies.add(s); }
    public void addStatic(Collection<Entity> s) { staticBodies.addAll(s); }

    public void addDynamic(Entity d) { dynamicBodies.add(d); }
    public void addDynamic(Collection<Entity> d) { dynamicBodies.addAll(d); }

    public void removeDynamic(Entity d) {
        if(dynamicBodies.contains(d))
            dynamicBodies.remove(d);
    }
    public void removeDynamic(Collection<Entity> d) {
        if(dynamicBodies.containsAll(d))
            dynamicBodies.removeAll(d);
    }

    public void removeStatic(Entity s) {
        if(staticBodies.contains(s))
            staticBodies.remove(s);
    }
    public void removeStatic(Collection<Entity> s) {
        if(staticBodies.containsAll(s))
            staticBodies.removeAll(s);
    }
}
