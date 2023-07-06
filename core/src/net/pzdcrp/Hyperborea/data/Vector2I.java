package net.pzdcrp.Hyperborea.data;

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.world.World;

public class Vector2I {
	public final int x;
	public final int z;
	public boolean updated = false;
	
	public Vector2I(int columnX, int columnZ) {
		this.x = columnX;
		this.z = columnZ;
	}
	
	public Vector2I(double entityX, double entityZ) {
		this.x = (int)Math.floor(entityX) >> 4;
		this.z = (int)Math.floor(entityZ) >> 4;
	}

	public int getcolumnX() {
		return x;
	}

	public int getcolumnZ() {
		return z;
	}
	
	public Vector3D toVec() {
		return new Vector3D(x*16+16/2,Hpb.world.player.pos.y,z*16+16/2);
	}
	
	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + x;
		hash = 31 * hash + z;
		return hash;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Vector2I)) {
			return false;
		}
		Vector2I coordsObj = (Vector2I) obj;
		if (coordsObj.getcolumnX() == x && coordsObj.getcolumnZ() == z) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return "CC[ x:"+x+", z:"+z+"]";
	}
	
	public static Vector2I fromString(String s) {
		if (s.startsWith("CC[ x:") && s.endsWith("]")) {
			int x = 0;
			int z = 0;
			s = s.replace("CC[ ", "");
			s = s.replace("]", "");
			for (String a : s.split(", ")) {
				if (a.startsWith("x:")) {
					a = a.replace("x:", "");
					x = Integer.parseInt(a);
				} else if (a.startsWith("z:")) {
					a = a.replace("z:", "");
					z = Integer.parseInt(a);
				}
			}
			return new Vector2I(x,z);
		} else {
			return null;
		}
	}

	public double distanceTo(Vector2I other) {
        int dx = other.x - x;
        int dz = other.z - z;
        return Math.sqrt(dx * dx + dz * dz);
    }
}
