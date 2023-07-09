package net.pzdcrp.Hyperborea.world.elements.generators;

import kenperlin.PerlinNoise;
import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.world.World;

public class Noise { // Classic Perlin noise in 3D, for comparison
	public static PerlinNoise p = new PerlinNoise(World.seed);
	
	/**
	 * @return double (0 - 1)
	 **/
	public static double get(float x, float y, float z) {
		double a = p.noise(x, y, z);
		a = (a + 1) / 2;
		return a;
	}
	/**
	 * @return double (0 - 1)
	 **/
	public static double get(double x, double z) {
		return (p.noise(x, z) + 1) / 2;
	}
}