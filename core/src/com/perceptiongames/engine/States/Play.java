package com.perceptiongames.engine.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

    private int levelNumber;

    private Player player;
    private List<Enemy> enemies;

    private List<Vector2> deathPoints;

    private TerrainGenerator generator;
    private Tile[][] terrain;

    public Play(GameStateManager gsm) {
        super(gsm);

        levelNumber = 1;
        loadContent();
        generateEntities();

        debug = new ShapeRenderer();
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
                    player.getAABB().overlaps(tt.getAABB());
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
            generator.generate();
        }
        camera.update();
    }

    @Override
    public void render() {
        batch.setProjectionMatrix(camera.combined);
        debug.setProjectionMatrix(camera.combined);
        batch.begin();
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
        batch.end();
        debug.begin(ShapeRenderer.ShapeType.Line);
        debug.box(0, 0, 0, Game.WORLD_WIDTH, Game.WORLD_HEIGHT, 0);
        debug.end();
    }

    @Override
    public void dispose() {

    }

    private void loadContent() {
        content.loadTexture("PlayerIdle", "PlayerStill.png");
        content.loadTexture("PlayerMove", "PlayerMove.png");
        content.loadTexture("Background", "Background.png");
        content.loadTexture("Badlogic", "badlogic.jpg");
        content.loadTexture("Block", "testBlock.png");

        content.loadTexture("Wall", "Terrain/Wall.png");
        content.loadTexture("Ladder", "Terrain/Ladder.png");
        content.loadTexture("Ground", "Terrain/Ground.png");

        content.loadFont("Ubuntu", "UbuntuBold.ttf", 20);

        content.loadMusic("Music", "testMusic.mp3");
    }

    private void generateEntities() {
        Animation playerStill = new Animation(content.getTexture("PlayerIdle"), 1, 8, 0.2f);
        Animation playerLeft = new Animation(content.getTexture("PlayerMove"), 1, 5, 0.2f);
        Animation playerRight = new Animation(content.getTexture("PlayerMove"), 1, 5, 0.2f);
        playerLeft.setFlipX(true);
        AABB aabb = new AABB(new Vector2(100, 100), new Vector2(16, 32));
        player = new Player(playerStill, "idle", aabb);

        player.addAnimation("moveLeft", playerLeft);
        player.addAnimation("moveRight", playerRight);

        enemies = new ArrayList<Enemy>();

        generator =  new TerrainGenerator(content);

        terrain = generator.getTerrain();
    }
}