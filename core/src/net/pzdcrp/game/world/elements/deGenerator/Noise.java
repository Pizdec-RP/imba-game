package net.pzdcrp.game.world.elements.deGenerator;

import kenperlin.PerlinNoise;

public class Noise { // Classic Perlin noise in 3D, for comparison
	public static PerlinNoise p = new PerlinNoise(254);
	public static double get(float x, float y, float z) {
		double a = p.noise(x, y, z);
		//System.out.println(a+" "+x+" "+y+" "+z);
		return a;
	}
	public static double get(double x, double z) {
		return p.noise(x, z);
	}
}