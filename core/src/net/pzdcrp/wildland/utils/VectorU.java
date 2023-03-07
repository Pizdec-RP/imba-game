package net.pzdcrp.wildland.utils;

import net.pzdcrp.wildland.data.Vector3D;

public class VectorU {
	public static double sqrt(Vector3D one, Vector3D two) {
		double distance = Math.sqrt(Math.pow(one.getX() - two.getX(), 2) + Math.pow(one.getY() - two.getY(), 2) + Math.pow(one.getZ() - two.getZ(), 2));
		return distance;
	}
}
