package net.pzdcrp.Aselia.data;

import java.util.ArrayList;
import java.util.List;

import net.pzdcrp.Aselia.Hpb;
import net.pzdcrp.Aselia.world.World;
import net.pzdcrp.Aselia.world.elements.Chunk;
import net.pzdcrp.Aselia.world.elements.Column;

public class Vector3I {
	public int x,y,z;
	public Vector3I(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3I() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}

	public Vector3I(Vector3D pos) {
		this.x = (int)pos.x;
		this.y = (int)pos.y;
		this.z = (int)pos.z;
	}

	public Vector3I addd(int x, int y, int z) {
		return new Vector3I(this.x + x, this.y + y, this.z + z);
	}

	@Override
	public boolean equals(Object anObject) {
		if (anObject instanceof Vector3I) {
			Vector3I pos1 = (Vector3I) anObject;
			return pos1.x == this.x && pos1.y == this.y && pos1.z == this.z;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + x;
		hash = 31 * hash + y;
		hash = 31 * hash + z;
		return hash;
	}

	public List<Vector3I> sides() {
		List<Vector3I> sides = new ArrayList<>() {{
			add(addd(1, 0, 0));
			add(addd(0, 0, 1));
			add(addd(-1, 0, 0));
			add(addd(0, 0, -1));
			add(addd(0, 1, 0));
			add(addd(0, -1, 0));
		}};
		return sides;
	}

	public List<Chunk> getSidesChunks() {
		List<Chunk> chunks = new ArrayList<>();
		Chunk thiss = Hpb.world.getColumn(x, z).chunks[y/16];
		for (Vector3I side : sides()) {
			if (side.y >= World.maxheight) continue;
			Column col = Hpb.world.loadedColumns.get(new Vector2I(x >> 4, z >> 4));
			if (col == null) continue;
			Chunk c = Hpb.world.getColumn(side.x, side.z).chunks[side.y/16];
			if (c != thiss && !chunks.contains(c)) chunks.add(c);
		}
		return chunks;
	}

	@Override
	public String toString() {
		return "Vec3I [x:"+x+" y:"+y+" z:"+z+"]";
	}

	public static Vector3I fromString(String s) {
		if (s.startsWith("Vec3I [") && s.endsWith("]")) {
			s = s.replace("Vec3I [", "");
			s = s.replace("]", "");
			int X=0,Y=0,Z=0;
			for (String a : s.split(" ")) {
				if (a.contains("x:")) {
					a = a.replace("x:", "");
					X = Integer.parseInt(a);
				} else if (a.contains("y:")) {
					a = a.replace("y:", "");
					Y = Integer.parseInt(a);
				} else if (a.contains("z:")) {
					a = a.replace("z:", "");
					Z = Integer.parseInt(a);
				}
			}
			return new Vector3I(X,Y,Z);
		} else {
			return null;
		}
	}
}
