package com.perceptiongames.engine.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.perceptiongames.engine.Entities.AABB;
import com.perceptiongames.engine.Entities.Enemy;
import com.perceptiongames.engine.Entities.Player;
import com.perceptiongames.engine.Game;
import com.perceptiongames.engine.Handlers.Animation;
import com.perceptiongames.engine.Handlers.GameStateManager;
import com.perceptiongames.engine.Handlers.Terrain.TerrainGenerator;
import com.perceptiongames.engine.Handlers.Terrain.Tile;

import java.util.ArrayList;
import java.util.List;

public class Play extends State {

    private ShapeRenderer debug;
    private BitmapFont debugFont;

    private int levelNumber;

    private Player player;
    private List<Enemy> enemies;

    private List<Vector2> deathPoints;

    private TerrainGenerator generator;
    private Tile[][] terrain;
    private Texture bg;

    public Play(GameStateManager gsm) {
        super(gsm);

        levelNumber = 1;
        loadContent();
        generateEntities();

        debug = new ShapeRenderer();
        debugFont = content.getFont("Ubuntu");
        camera.zoom =0.5f;
        bg = content.getTexture("Background");
        bg.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
    }

    @Override
    public void update(float dt) {
        player.update(dt);
        for(Enemy e : enemies) {
            e.update(dt);
        }
        for(Tile[] t : terrain) {
            for(Tile tt : t) {
                if(tt != null) {
                    if(player.getAABB().overlaps(tt.getAABB())) {
                        player.getAnimation(player.getAnimationKey()).setPosition(player.getPosition());
                    }
                }
            }
        }

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera.translate(-10, 0);
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.translate(10, 0);
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
            camera.translate(0, -10);
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            camera.translate(0, 10);
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.Q)) {
            camera.zoom = Math.max(camera.zoom - 0.2f, 0);
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.E)) {
            camera.zoom = Math.min(camera.zoom + 0.2f, 4f);
        }
        else if(Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            player.reset(generator.getStartPosition());
        }
        camera.position.set(
                Math.max(Math.min(player.getAABB().getCentre().x, Game.WORLD_WIDTH - 320), 320),
                Math.max(Math.min(player.getAABB().getCentre().y, Game.WORLD_HEIGHT - 180), 180),
                0);
        camera.update();

    }

    @Override
    public void render() {
        batch.setProjectionMatrix(camera.combined);
        debug.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(bg, -320, -180, Game.WORLD_WIDTH + 640, Game.WORLD_HEIGHT + 360, 0, 0, Game.WORLD_WIDTH/bg.getWidth(), Game.WORLD_HEIGHT/bg.getHeight());
        for(Tile[] t : terrain) {
            for(Tile tt : t) {
                if(tt != null) {
                    tt.render(batch);
                }
            }
        }
        for(Enemy e : enemies) {
            e.render(batch);
        }
        player.render(batch);
        debugFont.draw(batch, "Flags: " + String.format("0x%08X", player.getAABB().getCollisionFlags()),
                player.getPosition().x + 45, player.getPosition().y);
        batch.end();
        debug.begin(ShapeRenderer.ShapeType.Line);
        debug.box(0, 0, 0, Game.WORLD_WIDTH, Game.WORLD_HEIGHT, 0);
        for(Tile[] t : terrain) {
            for(Tile tt : t) {
                if(tt != null)
                    tt.getAABB().debugRender(debug);
            }
        }
        player.getAABB().debugRender(debug);
        debug.end();
    }

    @Override
    public void dispose() {}

    private void loadContent() {
        content.loadTexture("PlayerIdle", "PlayerIdle.png");
        content.loadTexture("PlayerMove", "PlayerRun.png");
        content.loadTexture("PlayerPush", "PlayerPush.png");
        content.loadTexture("Background", "Background.png");
        content.loadTexture("Badlogic", "badlogic.jpg");
        content.loadTexture("Block", "testBlock.png");

        content.loadTexture("Wall", "Terrain/Wall.png");
        content.loadTexture("BrokenWall", "Terrain/BrokenWall1.png");
        content.loadTexture("Ladder", "Terrain/Ladder.png");
        content.loadTexture("Ground", "Terrain/Ground.png");

        content.loadFont("Ubuntu", "UbuntuBold.ttf", 20);

        content.loadMusic("Music", "testMusic.mp3");
    }

    private void generateEntities() {
        Animation playerStill = new Animation(content.getTexture("PlayerIdle"), 1, 24, 0.2f);

        Animation playerLeft = new Animation(content.getTexture("PlayerMove"), 1, 5, 0.1f);
        Animation playerRight = new Animation(content.getTexture("PlayerMove"), 1, 5, 0.1f);
        Animation playerPushLeft = new Animation(content.getTexture("PlayerPush"), 1, 5, 0.1f);
        Animation playerPushRight = new Animation(content.getTexture("PlayerPush"), 1, 5, 0.1f);

        playerLeft.setFlipX(true);
        playerPushLeft.setFlipX(true);
        AABB aabb = new AABB(new Vector2(100, 100), new Vector2(16, 32));
        player = new Player(playerStill, "idle", aabb);
        player.setCamera(camera);

        player.addAnimation("moveLeft", playerLeft);
        player.addAnimation("moveRight", playerRight);

        player.addAnimation("pushLeft", playerPushLeft);
        player.addAnimation("pushRight", playerPushRight);

        enemies = new ArrayList<Enemy>();

        generator =  new TerrainGenerator(content);

        terrain = generator.getTerrain();
    }
}