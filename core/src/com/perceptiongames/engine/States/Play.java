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
import com.perceptiongames.engine.Handlers.Terrain.*;

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

    private boolean showDeathPoints;
    private boolean restarted;

    public Play(GameStateManager gsm) {
        super(gsm);

        levelNumber = 1;
        loadContent();
        generateEntities();

        debug = new ShapeRenderer();
        debug.setColor(1, 0, 0, 1);
        debugFont = content.getFont("Ubuntu");
        camera.zoom =0.5f;
        bg = content.getTexture("Background");
        bg.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        deathPoints = new ArrayList<Vector2>();
        showDeathPoints = true;
        restarted = false;
    }

    @Override
    public void update(float dt) {

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
            restarted = false;
            player.reset(generator.getStartPosition());
        }
        else if(Gdx.input.isKeyJustPressed(Input.Keys.P)){
            showDeathPoints = !showDeathPoints;
        }
        else if(Gdx.input.isKeyJustPressed(Input.Keys.V)) {
            terrain[3][0].setActive(true);
        }

        player.update(dt);

        for(Enemy e : enemies) { e.update(dt); }


        for (int i = 0; i < terrain.length; i++) {
            for(int j = 0; j < terrain[0].length; j++) {
                Tile current = terrain[i][j];
                if(current == null) continue;

                if(current instanceof SpearBlock) current.update(dt);
                if(current instanceof FallingBlock) {
                    current.update(dt);
                    if(!((FallingBlock)current).isAlive()) {
                        terrain[current.getColumn()][current.getRow()] = null;
                    }

                    if(current.isActive() && player.getPosition().y > current.getAABB().getMaximum().y) {
                        if(Math.abs(player.getPosition().x - current.getAABB().getPosition().x) < Tile.SIZE) {
                            ((FallingBlock) current).setVelocity(450f);
                            ((FallingBlock) current).setPlayerColliding(true);
                        }
                    }
                }

                if(player.getAABB().overlaps(current.getAABB())) {
                    player.getAnimation(player.getAnimationKey()).setPosition(player.getPosition());

                    if(current instanceof StandardTile) { standardTileCollision((StandardTile) current); }
                    else if(current instanceof SpearBlock) { spearBlockCollision((SpearBlock) current); }
                    else if(current instanceof Sensor) { sensorCollision((Sensor) current); }
                    else if(current instanceof FallingBlock) { fallingBlockCollision((FallingBlock) current); }
                }
            }
        }

        if(player.isLive()) {
            camera.position.set(
                    Math.max(Math.min(player.getAABB().getCentre().x, Game.WORLD_WIDTH - 320), 320),
                    Math.max(Math.min(player.getAABB().getCentre().y, Game.WORLD_HEIGHT - 180), 180),
                    0);
        }

        camera.update();
    }

    @Override
    public void render() {

        batch.setProjectionMatrix(camera.combined);
        debug.setProjectionMatrix(camera.combined);

        batch.begin();

        batch.draw(bg, -320, -180,
                Game.WORLD_WIDTH + 640, Game.WORLD_HEIGHT + 360, 0, 0,
                Game.WORLD_WIDTH / bg.getWidth(), Game.WORLD_HEIGHT / bg.getHeight());

        for(Tile[] column : terrain) {
            for(Tile tile : column) {
                if(tile != null) { tile.render(batch); }
            }
        }

        for(Enemy e : enemies) { e.render(batch); }

        player.render(batch);

        batch.end();

        debug.begin(ShapeRenderer.ShapeType.Line);
        player.getAABB().debugRender(debug);
        for (int i = 0; i < terrain.length; i++) {
            for (int j = 0; j < terrain[0].length; j++) {
                if(terrain[i][j] == null) continue;
                if(terrain[i][j] instanceof SpearBlock) {
                    terrain[i][j].getAABB().debugRender(debug);
                    debug.circle(((SpearBlock)terrain[i][j]).getAnimation().getPosition().x,
                            ((SpearBlock)terrain[i][j]).getAnimation().getPosition().y, 5);
                }
                else if(terrain[i][j] instanceof Sensor) {
                    terrain[i][j].getAABB().debugRender(debug);
                }
                else if(terrain[i][j] instanceof FallingBlock && terrain[i][j].isActive()) {
                    terrain[i][j].getAABB().debugRender(debug);
                }
            }
        }
        debug.end();

        if(showDeathPoints) {
            debug.begin(ShapeRenderer.ShapeType.Filled);
            for(Vector2 p : deathPoints) {
                debug.circle(p.x, p.y, 5f);
            }
            debug.end();
        }

    }

    @Override
    public void dispose() {}

    private void spearBlockCollision(SpearBlock tile) {
        float x = Math.abs(player.getPosition().x - tile.getAnimation().getPosition().x);
        if(tile.isFacingLeft()) {
            if (x < 80 && player.getAABB().hasCollisionBit(AABB.LEFT_BITS))
                player.hit();
        }
        else {
            if (x > 80 && player.getAABB().hasCollisionBit(AABB.RIGHT_BITS))
                player.hit();
        }
    }

    private void sensorCollision(Sensor tile) {
        int row = tile.getRow();
        int col = tile.getColumn();

        Tile[] s = new Tile[] { null, null, null, null, null, null, null, null };

        int xMax = TerrainGenerator.ROOM_WIDTH * TerrainGenerator.GRID_SIZE;
        int yMax = TerrainGenerator.ROOM_HEIGHT * TerrainGenerator.GRID_SIZE;


        if(row + 1 < yMax && col + 1 < xMax) { s[0] = terrain[col + 1][row + 1]; }
        if(row - 1 > 0 && col - 1 > 0) { s[1] = terrain[col - 1][row - 1]; }
        if(row + 1 < yMax) { s[2] = terrain[col][row + 1]; }
        if(row - 1 > 0) { s[3] = terrain[col][row - 1]; }
        if(col + 1 < xMax && row - 1 > 0) { s[4] = terrain[col + 1][row - 1]; }
        if(col - 1 > 0) { s[5] = terrain[col - 1][row]; }
        if(col + 1 < xMax) { s[6] = terrain[col + 1][row]; }
        if(col - 1 > 0 && row + 1 < yMax) { s[7] = terrain[col - 1][row + 1]; }

        for(Tile t : s) { if(t != null) t.setActive(true); }
    }

    private void fallingBlockCollision(FallingBlock tile) {
        if(tile.isActive()) {
            tile.setPlayerColliding(true);
            tile.setVelocity(450f);
            if(player.getAABB().hasCollisionBit(AABB.BOTTOM_BITS)) {
                player.hit();
            }
        }

    }

    private void standardTileCollision(StandardTile tile) {
        if(tile.getDamage() > 0) { player.hit(); }
        else if(tile.isLadder()) { player.isOnLadder(); }

        if (!player.isLive() && !restarted) {
            deathPoints.add(new Vector2(player.getAABB().getCentre()));
            player.incrementDeaths();
            restarted = true;
        }
    }


    private void loadContent() {
        content.loadTexture("PlayerIdle", "PlayerIdle.png");
        content.loadTexture("PlayerMove", "PlayerRun.png");
        content.loadTexture("PlayerPush", "PlayerPush.png");
        content.loadTexture("PlayerAttackLeft", "PlayerAttackLeft.png");
        content.loadTexture("PlayerAttackRight", "PlayerAttackRight.png");
        content.loadTexture("Background", "Background.png");
        content.loadTexture("Badlogic", "badlogic.jpg");
        content.loadTexture("Block", "testBlock.png");

        content.loadTexture("Ladder", "Terrain/Ladder.png");
        content.loadTexture("SpearBlock", "Terrain/SpearBlock.png");
        content.loadTexture("Wall", "Terrain/Wall.png");
        content.loadTexture("BrokenWall", "Terrain/BrokenWall1.png");
        content.loadTexture("BrokenWall1", "Terrain/BrokenWall2.png");
        content.loadTexture("BrokenWall2", "Terrain/BrokenWall3.png");
        content.loadTexture("Spikes", "Terrain/Spikes.png");
        content.loadTexture("Ground", "Terrain/Ground.png");

        content.loadFont("Ubuntu", "UbuntuBold.ttf", 20);

        content.loadMusic("Music", "testMusic.mp3");
        content.loadSound("Attack", "attack1.mp3");
    }

    private void generateEntities() {
        Animation playerStill = new Animation(content.getTexture("PlayerIdle"), 1, 24, 0.2f);

        Animation playerLeft = new Animation(content.getTexture("PlayerMove"), 1, 5, 0.1f);
        Animation playerRight = new Animation(content.getTexture("PlayerMove"), 1, 5, 0.1f);
        Animation playerPushLeft = new Animation(content.getTexture("PlayerPush"), 1, 5, 0.1f);
        Animation playerPushRight = new Animation(content.getTexture("PlayerPush"), 1, 5, 0.1f);
        Animation playerAttackLeft = new Animation(content.getTexture("PlayerAttackLeft"), 1, 4, 0.05f);
        Animation playerAttackRight = new Animation(content.getTexture("PlayerAttackRight"), 1, 4, 0.05f);
        playerAttackLeft.setOffset(-32, 0);

        playerLeft.setFlipX(true);
        playerPushLeft.setFlipX(true);

        AABB aabb = new AABB(new Vector2(100, 100), new Vector2(16, 32));
        player = new Player(playerStill, "idle", aabb);

        player.addAnimation("moveLeft", playerLeft);
        player.addAnimation("moveRight", playerRight);

        player.addAnimation("pushLeft", playerPushLeft);
        player.addAnimation("pushRight", playerPushRight);

        player.addAnimation("attackRight", playerAttackRight);
        player.addAnimation("attackLeft", playerAttackLeft);

        enemies = new ArrayList<Enemy>();

        generator =  new TerrainGenerator(content);

        terrain = generator.getTerrain();
    }
}