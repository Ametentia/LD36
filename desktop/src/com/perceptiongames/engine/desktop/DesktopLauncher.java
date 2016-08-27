package com.perceptiongames.engine.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.perceptiongames.engine.Game;
import org.lwjgl.Sys;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        // Window width and Height
		config.width = Game.WIDTH;
        config.height = Game.HEIGHT;

        // Window title
        config.title = Game.TITLE;

        // Prevents game from closing with exit code 255
        // Closes with exit code 0 like it should
        config.forceExit = false;
		//System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
        config.vSyncEnabled = false;

		new LwjglApplication(new Game(), config);
	}
}
