package net.pzdcrp.wildland;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import net.pizdecrp.game.test.ShaderTest;
import net.pizdecrp.game.test.shadowMapping.ShadowMappingTest;
import net.pzdcrp.wildland.data.Vector3D;

import java.io.File;
import java.io.FileReader;
import java.util.Random;


import com.badlogic.gdx.Graphics;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static final String ver = "0.1.5";
	public static GameInstance gi;
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setWindowedMode(1280, 720);
		config.setTitle("wildland "+ver);
		
		//System.out.println(new File("save/gay.txt").canRead());
		
		new Lwjgl3Application(gi = new GameInstance(), config);
	}
	
	public static void lerp(Vector3D toset, Vector3D before, Vector3D now) {
		Vector3D ltemp = new Vector3D(now.x-before.x,now.y-before.y,now.z-before.z);
		float mul = GameInstance.curCBT / GameInstance.renderCallsBetweenTicks;
		toset.setComponents(before.x + ltemp.x*mul, before.y + ltemp.y*mul, before.z + ltemp.z*mul);
	}
}
