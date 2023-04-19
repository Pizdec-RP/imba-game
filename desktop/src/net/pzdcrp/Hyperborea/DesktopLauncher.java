package net.pzdcrp.Hyperborea;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.Vector3D;

import java.io.File;
import java.io.FileReader;
import java.util.Random;


import com.badlogic.gdx.Graphics;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static final String ver = "0.1.6";
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(120);
		config.setWindowedMode(1280, 720);
		config.setTitle("Hyperborea "+ver);
		
		new Lwjgl3Application(new Hpb(), config);
		
		/*for (int i = 0; i < 6; i++) {
			System.out.println("put("+(i+1)+", "+(1f/6f)*(i+1)+");");
		}*/
	}
}