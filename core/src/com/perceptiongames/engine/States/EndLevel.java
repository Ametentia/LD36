package com.perceptiongames.engine.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.perceptiongames.engine.Entities.Player;
import com.perceptiongames.engine.Game;
import com.perceptiongames.engine.Handlers.GameStateManager;

public class EndLevel extends State {

    private BitmapFont font;

    private Play play;
    private Player player;

    private float totalPoints;

    private ShapeRenderer sr;

    private Texture bg;

    public EndLevel(GameStateManager gsm) {
        super(gsm);
        font = content.getFont("Ubuntu");
        camera.zoom = 1;
        camera.rotate(0);
        bg = content.getTexture("Background");
        bg.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        play = (Play) gsm.get(0);
        player = play.getPlayer();

        totalPoints = 500f / (float) Math.floor(play.getTime());
        totalPoints += 40f * player.getEnemiesKilled();
        totalPoints -= 50f * player.getNumberDeaths();

        sr = new ShapeRenderer();
        sr.setColor(Color.BLACK);
    }


    public void update(float dt) {
        if(Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            play.resetLevel();
            gsm.popState();
        }
    }

    public void render() {

        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.rect(mouse.x, mouse.y + 110, 150, 20);
        sr.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.draw(bg, -320, -180,
                Game.WORLD_WIDTH + 640, Game.WORLD_HEIGHT + 360, 0, 0,
                Game.WORLD_WIDTH / bg.getWidth(), Game.WORLD_HEIGHT / bg.getHeight());

        mouse.set(100, 100, 0);
        camera.unproject(mouse);
        font.draw(batch, "Level Completed!", mouse.x, mouse.y);
        font.draw(batch, "Time Taken: 500pts / " + Math.floor(play.getTime()), mouse.x, mouse.y + 30);

        font.draw(batch, "Enemies Killed: 40pts x " + player.getEnemiesKilled(), mouse.x, mouse.y + 60);
        font.draw(batch, "Deaths: -50pts x " + player.getNumberDeaths(), mouse.x, mouse.y + 90);

        font.draw(batch, "Total Points: " + totalPoints, mouse.x, mouse.y + 150);
        batch.end();
    }

    public void dispose() {}
}
