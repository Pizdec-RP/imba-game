package net.pzdcrp.wildland;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import net.pizdecrp.game.test.VoxelTest;
import net.pzdcrp.wildland.GameInstance;
import riven.PerlinNoise;

import java.util.Random;


import com.badlogic.gdx.Graphics;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setWindowedMode(1200, 800);
		config.setTitle("wildland 0.1.3");
		new Lwjgl3Application(new GameInstance(), config);
	}
}
