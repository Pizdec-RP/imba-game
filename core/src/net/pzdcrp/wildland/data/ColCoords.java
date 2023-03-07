package net.pzdcrp.wildland.data;

import net.pzdcrp.wildland.world.World;

public class ColCoords {
	public final int columnX;
	public final int columnZ;
	
	public ColCoords(int columnX, int columnZ) {
		this.columnX = columnX;
		this.columnZ = columnZ;
	}
	
	public ColCoords(double entityX, double entityZ) {
		this.columnX = (int)Math.floor(entityX) >> 4;
		this.columnZ = (int)Math.floor(entityZ) >> 4;
	}

	public int getcolumnX() {
		return columnX;
	}

	public int getcolumnZ() {
		return columnZ;
	}
	
	public Vector3D toVec() {
		return new Vector3D(columnX*World.chunkWidht+World.chunkWidht/2,World.player.pos.y,columnZ*World.chunkWidht+World.chunkWidht/2);
	}
	
	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + columnX;
		hash = 31 * hash + columnZ;
		return hash;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ColCoords)) {
			return false;
		}
		ColCoords coordsObj = (ColCoords) obj;
		if (coordsObj.getcolumnX() == columnX && coordsObj.getcolumnZ() == columnZ) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return "CC[ x:"+columnX+", z:"+columnZ+"]";
	}
	
	public static ColCoords fromString(String s) {
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
			return new ColCoords(x,z);
		} else {
			return null;
		}
	}
}
