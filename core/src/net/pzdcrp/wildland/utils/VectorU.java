package net.pzdcrp.wildland.utils;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector3;

import net.pzdcrp.wildland.GameInstance;
import net.pzdcrp.wildland.data.BlockFace;
import net.pzdcrp.wildland.data.OTripple;
import net.pzdcrp.wildland.data.Vector3D;
import net.pzdcrp.wildland.world.elements.Column;
import net.pzdcrp.wildland.world.elements.blocks.Block;
import net.pzdcrp.wildland.world.elements.entities.Entity;

public class VectorU {
	public static double sqrt(Vector3D one, Vector3D two) {
		double distance = Math.sqrt(Math.pow(one.getX() - two.getX(), 2) + Math.pow(one.getY() - two.getY(), 2) + Math.pow(one.getZ() - two.getZ(), 2));
		return distance;
	}
	
	public static Block findFacingBlock(Vector3D from, Vector3 dir) {
		List<Vector3D> list = ray(from, dir);
		for (Vector3D temppos : list) {
			Block b = GameInstance.world.getBlock(new Vector3D((int)MathU.floorDouble(temppos.x), (int)MathU.floorDouble(temppos.y), (int)MathU.floorDouble(temppos.z)));
			if (b.isCollide()) return b;
		}
		return null;
	}
	
	public static OTripple findFacingPair(Vector3D from, Vector3 dir) {
		List<Vector3D> list = ray(from, dir);
		Entity entity = null;
		for (Column column : GameInstance.world.loadedColumns.values()) {
			for (Entity en : column.entites) {
				for (Vector3D ps : list) {
					if (en.getHitbox().collide(ps)) {
						entity = en;
						break;
					}
				}
			}
		}
		OTripple tr = new OTripple(null,list.get(0), entity);//aim block, face, aim entity
		for (Vector3D temppos : list) {
			Block b = GameInstance.world.getBlock(temppos.floor());
			if (b.isCollide()) {
				tr.one = b;
				Vector3D face = getNear(temppos, b.pos.floor().sides());
				tr.two = face;//getFace(b.pos, face);
				//System.out.println(getFace(b.pos, face));
				return tr;
			}
		}
		tr.one = null;
		tr.two = null;
		return tr;
	}
	
	public static List<Vector3D> ray(Vector3D pos, Vector3 direction) {
		List<Vector3D> l = new ArrayList<>();
		Vector3D dir = Vector3D.translate(direction.cpy().nor()).multiply(0.05);
		Vector3D point = pos.clone();
		for (int i = 0; i < 120; i++) {
			l.add(point);
			point = point.add(dir);
		}
		return l;
	}
	
	public static BlockFace getFace(Vector3D target, Vector3D face) {
		face = face.floor();
		target = target.floor();
		//System.out.println("face: "+face.toString()+" of target: "+target.toString());
		if (target.add(1, 0, 0).equals(face)) {
			return BlockFace.PX;
		} else if (target.add(-1, 0, 0).equals(face)) {
			return BlockFace.NX;
		} else if (target.add(0, 1, 0).equals(face)) {
			return BlockFace.PY;
		} else if (target.add(0, -1, 0).equals(face)) {
			return BlockFace.NY;
		} else if (target.add(0, 0, 1).equals(face)) {
			return BlockFace.PZ;
		} else if (target.add(0, 0, -1).equals(face)) {
			return BlockFace.NZ;
		} else {
			System.out.println("unknown face: "+face.toString()+" of target: "+target.toString());
			return BlockFace.PX;
		}
	}
	
	public static Vector3D fromFace(Vector3D target, BlockFace face) {
		//System.out.println("FF target: "+target.toString()+" face: "+face.toString());
		if (face == BlockFace.NX) {
			return target.add(-1,0,0);
		} else if (face == BlockFace.NY) {
			return target.add(0,-1,0);
		} else if (face == BlockFace.NZ) {
			return target.add(0,0,-1);
		} else if (face == BlockFace.PX) {
			return target.add(1,0,0);
		} else if (face == BlockFace.PY) {
			return target.add(0,1,0);
		} else if (face == BlockFace.PZ) {
			return target.add(0,0,1);
		} else {
			System.out.println("wtf?");
			System.exit(0);
			return null;
		}
	}
	
	public static boolean equalsInt(Vector3D one, Vector3D two) {
		if (one == null || two == null) return false;
		//System.out.println(one.toStringInt() + " <<>> " + two.toStringInt());
		if ((int)Math.floor(one.getX()) == (int)Math.floor(two.getX()) && (int)Math.floor(one.getY()) == (int)Math.floor(two.getY()) && (int)Math.floor(one.getZ()) == (int)Math.floor(two.getZ())) return true;
		return false;
	}
	
	public static Vector3D getNear(Vector3D target, List<Vector3D> allPos) {
        Vector3D minpos = null;
        List<Vector3D> temp = new ArrayList<>();
        for (Vector3D position : allPos) {
        	if (!equalsInt(position, target)) {
	        	double distance = sqrt(position, target);
	        	if (minpos == null) {
	        		minpos = position;
	        	} else {
	        		double distanceminpos = sqrt(minpos, target);
	        		if (distance < distanceminpos) {
	        			minpos = position;
	        		} else if (distance == distanceminpos && MathU.rnd(1, 2) == 1) {
	        			minpos = position;
	        		}
	        	}
        	}
        }
        temp.add(minpos);
        for (Vector3D position : allPos) {
        	if (minpos == null || position == null) return null;
        	if (sqrt(minpos, target) == sqrt(position, target)) {
        		temp.add(position);
        	}
        }
        return temp.get(MathU.rnd(0, temp.size()-1));
    }
}
