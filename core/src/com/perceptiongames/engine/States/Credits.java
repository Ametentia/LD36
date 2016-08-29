package com.perceptiongames.engine.States;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.perceptiongames.engine.Entities.AABB;
import com.perceptiongames.engine.Entities.Button;
import com.perceptiongames.engine.Game;
import com.perceptiongames.engine.Handlers.Animation;
import com.perceptiongames.engine.Handlers.GameStateManager;

/**
 * Created by matt on 29/08/16.
 */
public class Credits extends State {

    private SpriteBatch batch;
    private Button back;
    private Button returnM;
    private Texture bg;

    public Credits(GameStateManager gsm)
    {
        super(gsm);
        loadContent();
        batch = new SpriteBatch();
        Animation a = new Animation(content.getTexture("Credits"),1,1,10f);
        back = new Button(a,"BEING ALIVE",new AABB(Game.WIDTH/2,Game.HEIGHT/2,150,200));

        Animation d = new Animation(content.getTexture("back"),1,1,10f);
        returnM = new Button(d,"BEING ALIVE",new AABB(24,24,24,24));

        bg = content.getTexture("Background");
        bg.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

    }
    public void loadContent()
    {
        content.loadTexture("Credits","backdrop.png");
        content.loadTexture("back","Icons/back.png");
    }
    @Override
    public void update(float dt) {
        Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mouse);
        if(returnM.getAABB().contains(new Vector2(mouse.x,mouse.y)) && Gdx.input.isTouched())
        {
            gsm.popState();
        }
    }

    @Override
    public void render() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(bg, -320, -180,
                Game.WORLD_WIDTH + 640, Game.WORLD_HEIGHT + 360, 0, 0,
                Game.WORLD_WIDTH / bg.getWidth(), Game.WORLD_HEIGHT / bg.getHeight());
        back.render(batch);
        returnM.render(batch);
        batch.end();
    }

    @Override
    public void dispose() {

    }
}
