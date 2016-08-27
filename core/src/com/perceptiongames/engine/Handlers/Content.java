package com.perceptiongames.engine.Handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

import java.util.HashMap;

public class Content {

    private HashMap<String, Texture> textures;
    private HashMap<String, BitmapFont> fonts;
    private HashMap<String, Sound> sounds;
    private HashMap<String, Music> music;

    /**
     * Creates a new instance of the Content Manager
     */
    public Content() {
        textures = new HashMap<String, Texture>();
        fonts = new HashMap<String, BitmapFont>();
        sounds = new HashMap<String, Sound>();
        music = new HashMap<String, Music>();
    }


    /**
     * Loads a Texture into memory
     * @param name The name used to retrieve the texture
     * @param path The path relative to assets/Textures/ in which the texture is located
     */
    public void loadTexture(String name, String path) { textures.put(name, new Texture("Textures/" + path)); }

    /**
     * Gets a texture specified by name
     * @param name The name of the texture to be retrieved
     * @return If the name matches then the texture corresponding to it will be returned
     */
    public Texture getTexture(String name) {
        if(!textures.containsKey(name))
            throw new IllegalArgumentException("Error: Unknown Texture " + name);

        return textures.get(name);
    }

    /**
     * Loads a font into memory
     * @param name The name used to retrieve the font
     * @param path The path relative to assets/Fonts/ in which the font is located
     * @param size The size in pixels of the font
     */
    public void loadFont(String name, String path, int size) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Fonts/" + path));
        FreeTypeFontParameter param = new FreeTypeFontParameter();
        param.size = size;
        param.color = Color.ORANGE;
        param.flip = true;

        fonts.put(name, generator.generateFont(param));
        generator.dispose();
    }

    /**
     * Gets a font specified by name
     * @param name The name of the font to be retrieved
     * @return If the name matches then the font corresponding to it will be returned
     */
    public BitmapFont getFont(String name) {
        if(!fonts.containsKey(name))
            throw new IllegalArgumentException("Error: Unknown Font " + name);

        return fonts.get(name);
    }

    /**
     * Loads a sound effect into memory
     * @param name The name used to retrieve the sound
     * @param path The path relative to assets/Sounds/ in which the sound is located
     */
    public void loadSound(String name, String path) {
        sounds.put(name, Gdx.audio.newSound(Gdx.files.internal("Sounds/" + path)));
    }

    /**
     * Gets a sound specified by name
     * @param name The name of the sound to be retrieved
     * @return If the name matches then the sound corresponding to it will be returned
     */
    public Sound getSound(String name) {
        if(!sounds.containsKey(name))
            throw new IllegalArgumentException("Error: Unknown Sound Effect " + name);

        return sounds.get(name);
    }

    /**
     * Loads a piece of music into memory
     * @param name The name used to retrieve the piece of music
     * @param path The path relative to assets/Music/ in which the music is located
     */
    public void loadMusic(String name, String path) {
        music.put(name, Gdx.audio.newMusic(Gdx.files.internal("Music/" + path)));
    }

    /**
     * Gets a piece of music specified by name
     * @param name The name of the piece of music to be returned
     * @return If the name matches then the piece of music corresponding to it will be returned
     */
    public Music getMusic(String name) {
        if(!music.containsKey(name))
            throw new IllegalArgumentException("Error: Unknown Music " + name);

        return music.get(name);
    }
}
