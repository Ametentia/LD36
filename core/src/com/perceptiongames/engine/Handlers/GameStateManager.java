package com.perceptiongames.engine.Handlers;

import com.perceptiongames.engine.Game;
import com.perceptiongames.engine.States.Pause;
import com.perceptiongames.engine.States.Play;
import com.perceptiongames.engine.States.State;

import java.util.Stack;

public class GameStateManager {

    public final Game game;
    private Stack<State> states;

    public static final int MENU = 0;
    public static final int PLAY = 1;
    public static final int PAUSE = 2;

    public GameStateManager(Game game) {
        this.game = game;
        states = new Stack<State>();

        pushState(PLAY);
    }

    private State getState(int state) {
        switch (state) {
            case PLAY:
                return new Play(this);
            case PAUSE:
                return new Pause(this);
            case MENU:
                // TODO
            default:
                throw new IllegalArgumentException();
        }
    }

    public void setState(int state) {
        popState();
        pushState(state);
    }

    public void pushState(int state) {
        states.push(getState(state));
    }

    public void popState() {
        State s = states.pop();
        if(s != null) {
            s.dispose();
        }
    }

    public void update(float dt) { states.peek().update(dt); }
    public void render() { states.peek().render(); }
}
