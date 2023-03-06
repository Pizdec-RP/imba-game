package net.pzdcrp.game.world.elements.deGenerator;

import riven.PerlinNoise;

public class Noise { // Classic Perlin noise in 3D, for comparison
	public static PerlinNoise p = new PerlinNoise(254);
	public static float get(float x, float y, float z) {
		
		float a = p.noise(x, y, z);
		//System.out.println(a+" "+x+" "+y+" "+z);
		return a;
	}
}