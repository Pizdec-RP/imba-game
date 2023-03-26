package net.pzdcrp.wildland;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import net.pizdecrp.game.test.ShaderTest;
import net.pizdecrp.game.test.shadowMapping.ShadowMappingTest;

import java.util.Random;


import com.badlogic.gdx.Graphics;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static final String ver = "0.1.4";
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setWindowedMode(1200, 800);
		config.setTitle("wildland "+ver);
		new Lwjgl3Application(new GameInstance(), config);
		//new Lwjgl3Application(new ShadowMappingTest(), config);
		//new Lwjgl3Application(new ShaderTest(), config);
		//for (s : new Vector3D(1,1,1).sides()) {
			
		//}
	}
}
