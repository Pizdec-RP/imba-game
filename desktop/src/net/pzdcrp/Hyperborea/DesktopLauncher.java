package net.pzdcrp.Hyperborea;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.world.elements.blocks.Block;
import net.pzdcrp.Hyperborea.world.elements.generators.Noise;

import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.Random;


import com.badlogic.gdx.Graphics;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static final String ver = "0.1.6";
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(120);
		config.setWindowedMode(1280, 720);//1280, 720
		config.setTitle("Hyperborea "+ver);
		//new Lwjgl3Application(new rendertest(), config);
		new Lwjgl3Application(new Hpb(), config);
		/*float scl = 0.1f;
		DecimalFormat decimalFormat = new DecimalFormat("#.##");
		
		for (int x = 0; x < 16; x++) {
	        for (int z = 0; z < 16; z++) {
	        	System.out.print(decimalFormat.format(Noise.get(x*scl, z*scl))+" ");
	        }
	        System.out.println("");
		}*/
	}
}
