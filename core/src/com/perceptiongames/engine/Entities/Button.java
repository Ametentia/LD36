package com.perceptiongames.engine.Entities;

import com.perceptiongames.engine.Handlers.Animation;

public class Button extends Entity{

    private String text;

    public Button(Animation animation, String animationName, AABB aabb) {
        super(animation, animationName, aabb);
        text = "";
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
