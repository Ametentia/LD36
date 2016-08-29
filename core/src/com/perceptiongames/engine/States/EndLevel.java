package com.perceptiongames.engine.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.perceptiongames.engine.Game;
import com.perceptiongames.engine.Handlers.GameStateManager;

public class EndLevel extends State {

    private BitmapFont font;

    private Play play;

    private Texture bg;

    public EndLevel(GameStateManager gsm) {
        super(gsm);
        font = content.getFont("Ubuntu");
        camera.setToOrtho(true, 1280, 720);
        camera.zoom = 1;
        camera.rotate(0);
        camera.lookAt(100,100,0);
        bg = content.getTexture("Background");
        bg.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        play = (Play) gsm.get(0);
    }


    public void update(float dt) {
        if(Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            play.reset();
            gsm.popState();
        }
    }

    public void render() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(bg, -320, -180,
                Game.WORLD_WIDTH + 640, Game.WORLD_HEIGHT + 360, 0, 0,
                Game.WORLD_WIDTH / bg.getWidth(), Game.WORLD_HEIGHT / bg.getHeight());
        font.draw(batch, "Level Completed!", 100, 100);
        font.draw(batch, "Time Taken: " + play.getTime(), 100, 130);
        batch.end();
    }

    public void dispose() {}
}
