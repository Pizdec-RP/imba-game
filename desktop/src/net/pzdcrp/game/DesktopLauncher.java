package net.pzdcrp.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import net.pizdecrp.game.test.VoxelTest;
import riven.PerlinNoise;

import java.util.Random;


import com.badlogic.gdx.Graphics;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(0);
		config.setWindowedMode(1200, 800);
		config.setTitle("testgame");
		//new Lwjgl3Application(new VoxelTest(), config);
		new Lwjgl3Application(new GameInstance(), config);
	}
}
