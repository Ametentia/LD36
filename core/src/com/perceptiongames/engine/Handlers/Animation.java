package com.perceptiongames.engine.Handlers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Animation {

    private Vector2 position;

    private TextureRegion texture;
    private int rows;
    private int columns;

    private float timePerFrame;
    private float sinceLastFrame;
    private int currentFrame;

    private final int totalFrames;

    private boolean flipX;
    private boolean flipY;

    /**
     * Creates an animation from a texture
     * @param texture The texture to represent the animation
     * @param rows How many rows the animation has
     * @param columns How many columns the animation has
     */
    public Animation(Texture texture, int rows, int columns) {
        this(texture, rows, columns, 0.1f);
    }

    /**
     * Creates an animation from a texture
     * @param texture The texture to represent the animation
     * @param rows How many rows the animation has
     * @param columns How many columns the animation has
     * @param timePerFrame How much time (in seconds) a frame lasts for
     */
    public Animation(Texture texture, int rows, int columns, float timePerFrame) {
        this.texture = new TextureRegion(texture);
        this.rows = rows;
        this.columns = columns;

        totalFrames = rows * columns;

        this.timePerFrame = timePerFrame;

        position = new Vector2();

        flipX = flipY = false;
    }

    /**
     * Moves the animation along to the next frame <br>
     *     Only if the total time since last frame update is greater than the time per frame
     * @param dt The time since the last frame
     */
    public void update(float dt) {
        sinceLastFrame += dt;
        if(sinceLastFrame >= timePerFrame) {
            sinceLastFrame -= timePerFrame;

            currentFrame++;
            if (currentFrame == totalFrames)
                currentFrame = 0;
        }
    }

    /**
     * Draws the animation on the screen on its current frame
     * @param batch The Sprite Batch used to render the texture on screen
     */
    public void render(SpriteBatch batch) {
        int width = texture.getTexture().getWidth() / columns;
        int height = texture.getTexture().getHeight() / rows;

        int row = (int)(((float) currentFrame) / ((float) columns));
        int col = currentFrame % columns;

        texture.setRegion(col * width, row * height, width, height);

        texture.flip(flipX, !flipY);
        batch.draw(texture, position.x, position.y);
    }

    // Getters
    public TextureRegion getTexture() { return texture; }
    public Vector2 getPosition() { return position; }
    public int getRows() { return rows; }
    public int getColumns() { return columns; }
    public boolean isFlipX() { return flipX; }
    public boolean isFlipY() { return flipY; }

    // Setters
    public void setPosition(Vector2 position) {
        this.position.set(position);
    }
    public void setTimePerFrame(float time) {
        timePerFrame = time;
    }
    public void setFlipX(boolean flip) { flipX = flip; }
    public void setFlipY(boolean flip) { flipY = flip; }
}
