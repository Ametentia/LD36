package com.perceptiongames.engine.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.perceptiongames.engine.Entities.AABB;
import com.perceptiongames.engine.Entities.Button;
import com.perceptiongames.engine.Game;
import com.perceptiongames.engine.Handlers.Animation;
import com.perceptiongames.engine.Handlers.GameStateManager;
import org.ietf.jgss.GSSManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matt on 29/08/16.
 */
public class Menu extends State{

    private List<Button> buttons;
    private SpriteBatch batch;
    private Texture bg;
    private Texture cnt;


    public Menu(GameStateManager gsm)
    {
        super(gsm);
        batch = new SpriteBatch();
        buttons = new ArrayList<Button>();

        loadContent();
        bg = content.getTexture("Background");
        cnt=content.getTexture("controls");
        bg.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        Animation a = new Animation(content.getTexture("ButtonBack"),1,1,10f);
        Button play = new Button(a,"Back",new AABB(Game.WIDTH/2, Game.HEIGHT/2 -80,110,40));
        play.setText("Play");
        buttons.add(play);

        a = new Animation(content.getTexture("ButtonBack"),1,1,10f);
        Button credits = new Button(a,"Back",new AABB(Game.WIDTH/2, Game.HEIGHT/2+40,110,40));
        credits.setText("Credits");
        buttons.add(credits);
    }

    public void loadContent(){
        content.loadTexture("ButtonBack", "button.png");
        content.loadFont("Menu","UbuntuBold.ttf",55);
        content.loadFont("Menus","UbuntuBold.ttf",25);
        content.loadTexture("Background", "Background.png");
        content.loadSound("Boop", "Boop.mp3");
        content.loadTexture("controls","controls.png");
    }

    @Override
    public void update(float dt) {
        Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mouse);
        for(Button b: buttons)
        {

            if(b.getAABB().contains(new Vector2(mouse.x,mouse.y)) && Gdx.input.isTouched())
            {
                content.getSound("Boop").play(0.2f);
                if(b.getText().equals("Play"))
                {
                    gsm.pushState(GameStateManager.PLAY);
                }
                else if(b.getText().equals("Credits"))
                {
                    gsm.pushState(GameStateManager.CREDITS);
                }

            }
        }
    }

    @Override
    public void render() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(bg, -320, -180,
                Game.WORLD_WIDTH + 640, Game.WORLD_HEIGHT + 360, 0, 0,
                Game.WORLD_WIDTH / bg.getWidth(), Game.WORLD_HEIGHT / bg.getHeight());
        for(Button b: buttons)
        {
            b.render(batch);
            content.getFont("Menu").draw(batch,b.getText(),b.getAABB().getPosition().x+15,b.getAABB().getPosition().y+20);
        }
        batch.draw(cnt,Game.WIDTH/2-cnt.getWidth()/2,500);
        batch.end();
    }

    @Override
    public void dispose() {

    }
}
