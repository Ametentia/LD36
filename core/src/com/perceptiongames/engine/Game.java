package com.perceptiongames.engine;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.perceptiongames.engine.Handlers.Content;
import com.perceptiongames.engine.Handlers.GameStateManager;
import com.perceptiongames.engine.Handlers.Terrain.TerrainGenerator;
import com.perceptiongames.engine.Handlers.Terrain.Tile;

public class Game extends ApplicationAdapter {

    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;

    public static final String TITLE = "Perception Engine";

    public static final int WORLD_WIDTH;
    public static final int WORLD_HEIGHT;

    static {
        WORLD_WIDTH = TerrainGenerator.ROOM_WIDTH * TerrainGenerator.GRID_SIZE * Tile.SIZE;
        WORLD_HEIGHT = TerrainGenerator.ROOM_HEIGHT * TerrainGenerator.GRID_SIZE * Tile.SIZE;
    }

    private GameStateManager gsm;
	private SpriteBatch batch;
    private OrthographicCamera camera;

    private Content content;

    private Viewport viewport;

    private float time;
    private final float DELTA = 1/60f;

	@Override
	public void create () {
	    batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(true);

        // Extended Viewport to deal with all aspect ratios
        // Works with 16:9, 16:10, 21:9 and 4:3
        viewport = new ExtendViewport(WIDTH, HEIGHT, camera);
        viewport.apply(true);

        content = new Content();

        gsm = new GameStateManager(this);
    }

	@Override
	public void render () {
	    Gdx.graphics.setTitle("FPS: " + Gdx.graphics.getFramesPerSecond());
		Gdx.gl.glClearColor((100/255f), (149/255f), (237/255f), 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        time += Gdx.graphics.getDeltaTime();
        while (time >= DELTA) {
            time -= DELTA;

            gsm.update(DELTA);

            if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
                Gdx.app.exit();
        }
        gsm.render();
	}
	
	@Override
	public void dispose () {
        batch.dispose();
    }

    @Override
    public void resize(int width, int height) { viewport.update(width, height, true); }

    public SpriteBatch getSpriteBatch() { return batch; }
	public OrthographicCamera getCamera() { return camera; }
	public Viewport getViewport() { return viewport; }

	public Content getContent() { return content; }
}
