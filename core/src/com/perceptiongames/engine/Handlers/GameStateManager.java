package com.perceptiongames.engine.Handlers;

import com.perceptiongames.engine.Game;
import com.perceptiongames.engine.States.*;

import java.util.Stack;

public class GameStateManager {

    public final Game game;
    private Stack<State> states;

    public static final int MENU = 0;
    public static final int PLAY = 1;
    public static final int PAUSE = 2;
    public static final int END_LEVEL = 3;
    public static final int CREDITS = 4;

    public GameStateManager(Game game) {
        this.game = game;
        states = new Stack<State>();

        pushState(MENU);
    }

    private State getState(int state) {
        switch (state) {
            case PLAY:
                return new Play(this);
            case PAUSE:
                return new Pause(this);
            case END_LEVEL:
                return new EndLevel(this);
            case MENU:
                return new Menu(this);
            case CREDITS:
                return new Credits(this);
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

    public State get(int index) { return  states.elementAt(index); }

    public void update(float dt) { states.peek().update(dt); }
    public void render() { states.peek().render(); }
}
