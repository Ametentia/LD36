package com.perceptiongames.engine.States;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.perceptiongames.engine.Entities.Player;
import com.perceptiongames.engine.Game;
import com.perceptiongames.engine.Handlers.GameStateManager;

/**
 * Created by matt on 30/08/16.
 */
public class Finish extends State {

    private Texture bg;
    private SpriteBatch batch;
    private Texture back;
    private BitmapFont font;
    private BitmapFont fonts;
    private Play play;
    private OrthographicCamera cam;
    private float totalPoints;
    private Player player;
    public Finish(GameStateManager gsm)
    {
        super(gsm);
        cam = new OrthographicCamera();
        batch = new SpriteBatch();
        bg = content.getTexture("Background");
        bg.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        font = content.getFont("Menu");
        fonts = content.getFont("Menus");
        content.loadTexture("asdfasdfasdf","finishBoard.png");
        back = content.getTexture("asdfasdfasdf");
        play = (Play) gsm.get(1);

        player = play.getPlayer();

        totalPoints = 500f / (float) Math.floor(play.getTime());
        totalPoints += 100f * player.getEnemiesKilled();
        totalPoints -= 50f * player.getNumberDeaths();

        if(totalPoints<0)
        {
            totalPoints=0;
        }

        totalPoints = round(totalPoints);

        player.addPoints((int)totalPoints);
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        mouse.set(-320, -180, 0);
        camera.unproject(mouse);
        batch.draw(bg, mouse.x, mouse.y,
                Game.WORLD_WIDTH + 640, Game.WORLD_HEIGHT + 360, 0, 0,
                Game.WORLD_WIDTH / bg.getWidth(), Game.WORLD_HEIGHT / bg.getHeight());
        mouse.set(game.WIDTH/2-back.getWidth()/2, 20, 0);
        camera.unproject(mouse);
        batch.draw(back,mouse.x,mouse.y,600,800);
        mouse.set(game.WIDTH/2-100, 120, 0);
        camera.unproject(mouse);
        font.draw(batch,"Well Done!",mouse.x,mouse.y);
        mouse.set(game.WIDTH/2-150, 190, 0);
        camera.unproject(mouse);
        fonts.draw(batch,"You've Defeated The Game!",mouse.x,mouse.y);
        mouse.set(game.WIDTH/2-back.getWidth()/2+70, 350, 0);
        camera.unproject(mouse);
        fonts.draw(batch,"You finished with the score: " + play.getPlayer().getTotalPoints(),mouse.x,mouse.y);
        mouse.set(game.WIDTH/2-back.getWidth()/2+70, 380, 0);
        camera.unproject(mouse);
        fonts.draw(batch,"Deaths: "+play.getPlayer().getNumberDeaths(),mouse.x,mouse.y);
        mouse.set(game.WIDTH/2-back.getWidth()/2+70, 410, 0);
        camera.unproject(mouse);
        fonts.draw(batch,"Time: "+(int)Math.floor(play.getTotalTime()),mouse.x,mouse.y);
        mouse.set(game.WIDTH/2-back.getWidth()/2+70, 440, 0);
        camera.unproject(mouse);
        fonts.draw(batch,"Kills: "+play.getTotalKills(),mouse.x,mouse.y);
        batch.end();
    }

    @Override
    public void dispose() {

    }
    private float round(float number) {

        number /= 10;

        number = Math.round(number);
        return number *= 10;
    }
}
