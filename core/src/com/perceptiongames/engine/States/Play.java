package com.perceptiongames.engine.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
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

    private float timeTaken;

    private float cameraXOffset;
    private float cameraYOffset;
    private OrthographicCamera hudCamera;

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
        showDeathPoints = false;
        restarted = false;
        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(true);
        timeTaken = 0;
    }
    public void madeHud(ShapeRenderer sr)
    {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.setProjectionMatrix(hudCamera.combined);
        batch.setProjectionMatrix(hudCamera.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        debugFont.setColor(0,0,0,1);
        mouse.set(0,0,0);
        hudCamera.unproject(mouse);
        Color a = new Color(253f/255,0,0,0.6f);
        Color b = new Color(253f/255,0,0,0.75f);
        sr.rect(mouse.x,mouse.y,200,50,a,a,a,a);
        sr.triangle(mouse.x+200,mouse.y,mouse.x+200,mouse.y+50,mouse.x+300,mouse.y,a,a,b);
        sr.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        batch.begin();
        debugFont.draw(batch, "Time: "+Math.round(timeTaken),mouse.x+20,mouse.y+15);
        debugFont.draw(batch, "Death Count: "+player.getNumberDeaths(),mouse.x+20,mouse.y+30);
        batch.end();
        sr.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);
    }
    @Override
    public void update(float dt) {
        boolean movingCamera=false;
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            movingCamera=true;
            if(cameraXOffset-player.getAABB().getHeight()>-Game.WIDTH/4)
                cameraXOffset-=8;
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            movingCamera=true;
            if(cameraXOffset+player.getAABB().getWidth()<Game.WIDTH/4)
                cameraXOffset+=8;
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
            movingCamera=true;
            if(cameraYOffset-player.getAABB().getHeight()/2>-Game.HEIGHT/4)
                cameraYOffset-=8;
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            movingCamera=true;
            if(cameraYOffset+player.getAABB().getHeight()/2<Game.HEIGHT/4)
                cameraYOffset+=8;
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.Q)) {
            camera.zoom = Math.max(camera.zoom - 0.02f, 0);
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.E)) {
            camera.zoom = Math.min(camera.zoom + 0.02f, 4f);
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
        if(!movingCamera)
        {
            if(Math.abs(cameraXOffset)>10)
                cameraXOffset-= (cameraXOffset/Math.abs(cameraXOffset))*3f;
            if(Math.abs(cameraYOffset)>10)
                cameraYOffset-=(cameraYOffset/Math.abs(cameraYOffset))*3f;
            if(Math.abs(cameraXOffset)<10)
            {
                cameraXOffset=0;
            }
            if(Math.abs(cameraYOffset)<10)
            {
                cameraYOffset=0;
            }
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
                    else if(current instanceof Sensor) { sensorCollision((Sensor) current, -1); }
                    else if(current instanceof FallingBlock) { fallingBlockCollision((FallingBlock) current); }
                }
                if(player.getWeapon().overlaps(current.getAABB()) && player.isAttacking() && !current.getAABB().isSensor()) {
                    System.out.println("Stabbing: [" + current.getRow() + ":" + current.getColumn() + "]");
                }

                for(int e = 0; e < enemies.size(); e++) {

                    if(enemies.get(e).getAABB().overlaps(current.getAABB())) {
                        if(current instanceof Sensor) { sensorCollision((Sensor) current, e); }
                    }

                    if(enemies.get(e).isAttacking() && enemies.get(e).getWeapon().overlaps(player.getAABB())) {
                        player.hit();
                    }
                    if(player.getWeapon().overlaps(enemies.get(e).getAABB()) && player.isAttacking()) {
                        enemies.get(e).hit();
                    }
                }
            }
        }

        if(player.isLive()) {
            camera.position.set(
                    Math.max(Math.min(player.getAABB().getPosition().x + 16+cameraXOffset, Game.WORLD_WIDTH - 320), 320),
                    Math.max(Math.min(player.getAABB().getPosition().y + 32+cameraYOffset, Game.WORLD_HEIGHT - 180), 180),
                    0);
        }


        camera.update();
        hudCamera.update();

        timeTaken += dt;
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



        player.render(batch);

        mouse.set(100, 100, 0);
        camera.unproject(mouse);

        debugFont.draw(batch, "Attacking: " + player.isAttacking(), mouse.x, mouse.y);

        for(Enemy e : enemies) {
            e.render(batch);
        }
        mouse.set(100, 150, 0);
        camera.unproject(mouse);
        batch.end();

        madeHud(debug);

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

    private void sensorCollision(Sensor tile, int index) {
        if(tile.getData().contains("Enemy") && index >= 0) {
            if(tile.getData().equals("EnemyLeft")) {
                enemies.get(index).setCurrent(2);
            }
            else if(tile.getData().equals("EnemyRight")) {
                enemies.get(index).setCurrent(1);
            }
        }
        else if(!tile.getData().contains("Enemy")) {
            int row = tile.getRow();
            int col = tile.getColumn();

            Tile[] s = new Tile[]{null, null, null, null, null, null, null, null};

            int xMax = TerrainGenerator.ROOM_WIDTH * TerrainGenerator.GRID_SIZE;
            int yMax = TerrainGenerator.ROOM_HEIGHT * TerrainGenerator.GRID_SIZE;


            if (row + 1 < yMax && col + 1 < xMax) { s[0] = terrain[col + 1][row + 1]; }
            if (row - 1 > 0 && col - 1 > 0) { s[1] = terrain[col - 1][row - 1]; }
            if (row + 1 < yMax) { s[2] = terrain[col][row + 1]; }
            if (row - 1 > 0) { s[3] = terrain[col][row - 1]; }
            if (col + 1 < xMax && row - 1 > 0) { s[4] = terrain[col + 1][row - 1]; }
            if (col - 1 > 0) { s[5] = terrain[col - 1][row]; }
            if (col + 1 < xMax) { s[6] = terrain[col + 1][row]; }
            if (col - 1 > 0 && row + 1 < yMax) { s[7] = terrain[col - 1][row + 1]; }

            for (Tile t : s) { if (t != null) t.setActive(true); }
        }
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

        if (!player.isLive() && !restarted) {
            deathPoints.add(new Vector2(player.getAABB().getCentre()));
            player.incrementDeaths();
            restarted = true;
            player.reset(generator.getStartPosition());
        }

        if(tile.getDamage() > 0) { player.hit(); }
        else if(tile.isLadder()) { player.isOnLadder(); }
        else if(tile.getDamage() == -4) {
            player.setLive(false);
            restarted = false;
            gsm.pushState(GameStateManager.END_LEVEL);
        }
    }


    public void reset() {
        generator.generate();
        player.reset(generator.getStartPosition());
        camera.position.set(
                Math.max(Math.min(player.getAABB().getPosition().x + 16, Game.WORLD_WIDTH - 320), 320),
                Math.max(Math.min(player.getAABB().getPosition().y + 32, Game.WORLD_HEIGHT - 180), 180),
                0);

        enemies.clear();
        enemies.addAll(generator.getEnemies());

        deathPoints.clear();

        timeTaken = 0;

        terrain = generator.getTerrain();
        levelNumber++;
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
        content.loadTexture("Enemy", "Soldier.png");
        content.loadTexture("EnemyMove", "SoldierMove.png");
        content.loadTexture("EnemyAttack", "SoldierAttack.png");


        content.loadTexture("Ladder", "Terrain/Ladder.png");
        content.loadTexture("EndDoor", "Terrain/EndDoor.png");
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

        generator =  new TerrainGenerator(content);

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
        player.getSounds().add(content.getSound("Attack"));

        player.setWeapon(new AABB(player.getAABB().getCentre().x, player.getAABB().getCentre().y + 4, 6, 3));

        enemies = new ArrayList<Enemy>();
        enemies.addAll(generator.getEnemies());


        terrain = generator.getTerrain();
        player.reset(generator.getStartPosition());

    }

    public float getTime() { return timeTaken; }
}