package com.perceptiongames.engine.States;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.perceptiongames.engine.Game;
import com.perceptiongames.engine.Handlers.Content;
import com.perceptiongames.engine.Handlers.GameStateManager;

public abstract class State {

    protected GameStateManager gsm;
    protected Game game;

    protected SpriteBatch batch;
    protected Content content;

    protected OrthographicCamera camera;
    protected Viewport viewport;

    protected final Vector3 mouse;

    public State(GameStateManager gsm) {
        this.gsm = gsm;
        game = gsm.game;

        batch = game.getSpriteBatch();
        camera = game.getCamera();

        viewport = game.getViewport();

        content = game.getContent();

        mouse = new Vector3();
    }

    public abstract void update(float dt);
    public abstract void render();

    public abstract void dispose();
}
