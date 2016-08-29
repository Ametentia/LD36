package com.perceptiongames.engine.Entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

// A class to represent an Axis-aligned Bounding Box
// Cannot be extended from
public final class AABB {

    public static final int NONE_BITS = 0x1;
    public static final int TOP_BITS = 0x10;
    public static final int BOTTOM_BITS = 0x100;
    public static final int LEFT_BITS = 0x1000;
    public static final int RIGHT_BITS = 0x10000;

    public static final int SENSOR_BITS = 0x100000;

    // AABB represented by a centre point and its half width and half height
    private Vector2 centre;
    private Vector2 halfSize;

    // The Current collisionFlags state of the AABB
    private int collisionFlags;

    private boolean isSensor;

    // Constructors

    /**
     * Creates an AABB from and exsiting one
     * @param aabb The exsiting AABB to create this from
     */
    public AABB(AABB aabb) {
        this(aabb.getCentre(), aabb.getHalfSize());
    }

    /**
     * Creates an AABB from the given parameters
     * @param centreX The X coordinate of the centre
     * @param centreY The Y coordinate of the centre
     * @param halfWidth The half width of the AABB
     * @param halfHeight The half height of the AABB
     */
    public AABB(float centreX, float centreY, float halfWidth, float halfHeight) {
        this(new Vector2(centreX, centreY), new Vector2(halfWidth, halfHeight));
    }

    /**
     * Creates an AABB from the given parameters
     * @param centre A Vector2 representation of the centre
     * @param halfSize A Vector2 representation of the halfSize
     *                 , where X is half width and Y is half height
     */
    public AABB(Vector2 centre, Vector2 halfSize) {
        this.centre = new Vector2(centre);
        this.halfSize = new Vector2(halfSize);
        collisionFlags = NONE_BITS;

        isSensor = false;
    }

    /**
     * A method to check if another AABB overlaps with this one
     * @param other The secondary AABB to check overlap with
     * @return True if the two AABBs overlap, otherwise false
     */
    public boolean overlaps(AABB other) {
        if(Math.abs(this.centre.x - other.centre.x) > this.halfSize.x + other.halfSize.x) {
            return false;
        }
        if(Math.abs(this.centre.y - other.centre.y) > this.halfSize.y + other.halfSize.y) {
            return false;
        }


        if(!isSensor && !other.isSensor)
            collide(other);
        else
            collisionFlags |= SENSOR_BITS;

        return true;
    }

    public boolean contains(Vector2 pos)
    {
        if(this.centre.x+halfSize.x > pos.x && this.centre.x-this.halfSize.y < pos.x)
            if(this.centre.y+this.getHeight()/2 > pos.y && this.centre.y-this.getHeight()/2 < pos.y)
                return true;
        return false;
    }


    /**
     * This is called if two AABBs overlap <br>
     *     it will separate the two AABBs so they are collisionFlags and not intersecting
     * @param other The second AABB which is overlapping with this
     */
    private void collide(AABB other) {
        float bottom = other.getMaximum().y - this.getMinimum().y;
        float top = this.getMaximum().y - other.getMinimum().y;

        float left = this.getMaximum().x - other.getMinimum().x;
        float right = other.getMaximum().x - this.getMinimum().x;

        if(top <= left && top <= right && top <= bottom) {
            this.setPosition(this.getPosition().x, other.getMinimum().y - this.getHeight());
            collisionFlags |= TOP_BITS;
        }
        else if(bottom <= left && bottom <= right && bottom <= top) {
            this.setPosition(this.getPosition().x, other.getMaximum().y);
            collisionFlags |= BOTTOM_BITS;
        }
        else if(left <= right && left <= top && left <= bottom) {
            this.setPosition(other.getMinimum().x - this.getWidth(), this.getPosition().y);
            collisionFlags |= LEFT_BITS;
        }
        else {
            this.setPosition(other.getMaximum().x, this.getPosition().y);
            collisionFlags |= RIGHT_BITS;
        }
    }

    // Getters
    public Vector2 getCentre() { return centre; }
    public Vector2 getHalfSize() { return halfSize; }

    // Position and Minimum are the same, more for code readability
    public Vector2 getPosition() { return  new Vector2(centre).sub(halfSize); }
    public Vector2 getMinimum() { return getPosition(); }
    public Vector2 getMaximum() { return new Vector2(centre).add(halfSize); }

    // Returns the current collision state
    public int getCollisionFlags() { return collisionFlags; }

    public float getWidth() { return halfSize.x * 2; }
    public float getHeight() { return halfSize.y * 2; }

    public boolean hasCollisionBit(int bits) { return (collisionFlags & bits) == bits; }
    public boolean isSensor() { return isSensor; }

    // Setters
    public void setCentre(Vector2 centre) { this.centre.set(centre); }
    public void setPosition(float x, float y) { setPosition(new Vector2(x, y)); }
    public void setPosition(Vector2 position) { setCentre(new Vector2(position).add(halfSize)); }
    public void setCollisionFlags(int flags) { collisionFlags = flags; }
    public void setSensor(boolean sensor) { this.isSensor = sensor; }

    public void setHalfSize(float w, float h) { halfSize.set(w, h); }

    // DEBUG Stuff
    // This will draw the AABB as an outline on screen
    public void debugRender(ShapeRenderer sr) {
        sr.box(getPosition().x, getPosition().y, 0, getHalfSize().x * 2, getHalfSize().y * 2, 0);
    }
}
