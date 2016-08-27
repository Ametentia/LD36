package com.perceptiongames.engine.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Json;
import com.perceptiongames.engine.Entities.AABB;
import com.perceptiongames.engine.Entities.Player;
import com.perceptiongames.engine.Game;
import com.perceptiongames.engine.Handlers.Animation;
import com.perceptiongames.engine.Handlers.GameStateManager;
import com.perceptiongames.engine.Handlers.Terrain.TerrainGenerator;
import com.perceptiongames.engine.Handlers.World;

public class Play extends State {

    private ShapeRenderer sr;
    private BitmapFont font;

    private Player player;

    private Texture bg;

    private float uRight, vTop;

    private int frames, totalFrames;
    private float frameTime;

    private World world;
    private String worldName;

    private TerrainGenerator generator;

    public Play(GameStateManager gsm) {
        super(gsm);

        Animation a = new Animation(content.getTexture("PlayerIdle"), 1, 8, 0.1f);

        bg = content.getTexture("Background");
        bg.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        uRight = Game.WORLD_WIDTH / bg.getWidth();
        vTop = Game.WORLD_HEIGHT / bg.getHeight();

        world = new World();
        world.addDynamic(player = new Player(a,"idle", new AABB(150, 150, 16, 32)));
        a = new Animation(content.getTexture("PlayerMove"), 1, 5, 0.1f);
        player.addAnimation("moveRight",a);
        a = new Animation(content.getTexture("PlayerMove"), 1, 5, 0.1f);
        a.setFlipX(true);
        player.addAnimation("moveLeft",a);

        sr = new ShapeRenderer();
        sr.setColor(1, 0, 0, 1);

        font = content.getFont("Ubuntu");
        font.setColor(0, 1, 0, 1);

        worldName = "World 1.json";

        content.getMusic("Music").play();// Found this from when my friend made me music for an old game
        content.getMusic("Music").setLooping(true); // Thought I should test music :P

        generator = new TerrainGenerator(content);
        world.addStatic(generator.getTerrain());
    }

    public void update(float dt) {

        if(Gdx.input.isKeyJustPressed(Input.Keys.F5))
            saveWorld(worldName);



        if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
            camera.zoom = Math.max(0.5f, camera.zoom - 0.1f);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            camera.zoom = Math.min(3f, camera.zoom + 0.1f);
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.Y)) {
            world.removeStatic(generator.getTerrain());
            generator.generate();
            world.addStatic(generator.getTerrain());
        }

        mouse.set(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        camera.unproject(mouse);

        if(Gdx.input.isKeyJustPressed(Input.Keys.F11)){
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode(Gdx.graphics.getPrimaryMonitor()));
        }

        frames++;
        frameTime += Gdx.graphics.getRawDeltaTime();
        if(frameTime >= 1) {
            totalFrames = frames;
            frames = 0;
            frameTime = 0;
        }

        world.update(dt);

        camera.position.set(
                Math.max(Math.min(player.getAABB().getCentre().x, Game.WORLD_WIDTH - 320), 320),
                Math.max(Math.min(player.getAABB().getCentre().y, Game.WORLD_HEIGHT - 180), 180),
                0);

        camera.update();
    }

    public void render() {
        batch.setProjectionMatrix(camera.combined);
        sr.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(bg, -320, -180, Game.WORLD_WIDTH + 640, Game.WORLD_HEIGHT + 360, 0, 0, uRight, vTop);
        world.render(batch);
        batch.end();

        sr.begin(ShapeRenderer.ShapeType.Line);
        player.getAABB().debugRender(sr);
        sr.box(0, 0, 0, Game.WORLD_WIDTH, Game.WORLD_HEIGHT, 0);
        sr.end();

    }

    public void dispose() {
        sr.dispose();
    }

    private void saveWorld(String worldDirectory) {
        Gdx.files.local(worldDirectory).writeString(new Json().toJson(world), false);
    }
}
