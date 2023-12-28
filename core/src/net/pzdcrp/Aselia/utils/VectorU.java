package net.pzdcrp.Aselia.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.math.Vector3;

import net.pzdcrp.Aselia.Hpb;
import net.pzdcrp.Aselia.data.BlockFace;
import net.pzdcrp.Aselia.data.Vector2I;
import net.pzdcrp.Aselia.data.Vector3D;
import net.pzdcrp.Aselia.data.Vector3I;
import net.pzdcrp.Aselia.world.elements.Column;
import net.pzdcrp.Aselia.world.elements.blocks.Block;
import net.pzdcrp.Aselia.world.elements.entities.Entity;

public class VectorU {
	public static double sqrt(Vector3D one, Vector3D two) {
		float distance = MathU.sqrt(MathU.pow(one.getX() - two.getX(), 2) + MathU.pow(one.getY() - two.getY(), 2) + MathU.pow(one.getZ() - two.getZ(), 2));
		return distance;
	}

	public static double sqrt(Vector3D one, Vector3 two) {
		float distance = MathU.sqrt(MathU.pow(one.getX() - two.x, 2) + MathU.pow(one.getY() - two.y, 2) + MathU.pow(one.getZ() - two.y, 2));
		System.out.println(one.toString()+" "+two.toString());
		return distance;
	}

	public static Block findFacingBlock(Vector3D from, Vector3 dir) {
		List<Vector3D> list = ray(from, dir);
		for (Vector3D temppos : list) {
			Block b = Hpb.world.getBlock(new Vector3D(MathU.floorDouble(temppos.x), MathU.floorDouble(temppos.y), MathU.floorDouble(temppos.z)));
			if (b.isCollide()) return b;
		}
		return null;
	}

	public static Object[] findFacingPair(Vector3D from, Vector3 dir, Entity curen) {
		Object[] oarr = new Object[4];
		List<Vector3D> list = ray(from, dir);
		Entity entity = null;
		for (Column column : Hpb.world.loadedColumns.values()) {
			for (Entity en : column.entites) {
				for (Vector3D ps : list) {
					if (!en.equals(curen) && en.getHitbox().collide(ps)) {
						entity = en;
						break;
					}
				}
			}
		}
		oarr[0] = null;
		oarr[1] = list.get(0);
		oarr[2] = entity;
		for (Vector3D temppos : list) {
			Block b = Hpb.world.getBlock(temppos.floor());
			if (b.getHitbox() != null) {
				oarr[0] = b;
				Vector3D face = getNear(temppos, b.pos.floor().sides());
				oarr[1] = face;
				oarr[3] = temppos;
				return oarr;
			}
		}
		oarr[0] = null;
		oarr[1] = null;
		return oarr;
	}

	public static List<Vector3D> ray(Vector3D pos, Vector3 direction) {
		List<Vector3D> l = new ArrayList<>();
		Vector3D dir = Vector3D.translate(direction.cpy().nor()).multiply(0.05f);
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
			GameU.end("unknown face");
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
        for (Vector3D position : allPos) {
        	double distance = sqrt(position, target);
        	if (minpos == null) {
        		minpos = position;
        	} else {
        		double distanceminpos = sqrt(minpos, target);
        		if (distance < distanceminpos) {
        			minpos = position;
        		}
        	}
        }
        return minpos;
    }


    public static Vector2I posToRegion(Vector3D pos) {
    	return new Vector2I((int)Math.floor(pos.x) >> 10, (int)Math.floor(pos.z) >> 10);
    }

    public static Vector2I posToColumn(Vector3I pos) {
    	return new Vector2I(pos.x >> 4, pos.z >> 4);
    }

    public static Vector3I posToChunk(Vector3D pos) {
    	return new Vector3I((int)Math.floor(pos.x) >> 4, (int)pos.y/16, (int)Math.floor(pos.z) >> 4);
    }

    public static Vector2I posToColumn(Vector3D pos) {
    	return new Vector2I((int)Math.floor(pos.x) >> 4, (int)Math.floor(pos.z) >> 4);
    }

    public static Vector2I xzToColumn(int x, int z) {
    	return new Vector2I(x >> 4, z >> 4);
    }

    public static Vector2I ColumnToRegion(Vector2I column) {
    	return new Vector2I((int)Math.floor(column.x) >> 3, (int)Math.floor(column.z) >> 3);
    }

    public static void sortBlocksByDistance(Set<Block> blocks, Vector3D cameraPosition) {
        List<Block> blockList = new ArrayList<>(blocks);

        // Используем компаратор для сортировки блоков по расстоянию от камеры
        Comparator<Block> distanceComparator = (block1, block2) -> {
            double distance1 = VectorU.sqrt(block1.pos, cameraPosition);
            double distance2 = VectorU.sqrt(block2.pos, cameraPosition);
            return Double.compare(distance2, distance1); // Сравниваем в обратном порядке (от дальних к ближним)
        };

        // Сортируем блоки
        Collections.sort(blockList, distanceComparator);

        // Обновляем множество блоков с отсортированной версией
        blocks.clear();
        blocks.addAll(blockList);
    }

    public static List<Vector2I> generateVectorsInRadius(Vector2I center, int radius) {
        List<Vector2I> vectors = new ArrayList<>();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                Vector2I vector = new Vector2I(center.x + x, center.z + z);

                if (vector.distanceTo(center) <= radius) {
                    vectors.add(vector);
                }
            }
        }
        return vectors;
    }

    public static Entity getNearest(List<? extends Entity> list, Vector3D target) {
    	Entity minen = null;
    	double minDistance = Double.MAX_VALUE;

    	for (Entity en : list) {
    		double dist = sqrt(en.pos, target);
    		if (
    				(minDistance < dist) ||
    				(minDistance == dist && MathU.rndnrm() > 0.5)
    		) {
    			minDistance = dist;
    			minen = en;
    		}
    	}
    	return minen;
    }

    public static List<? extends Entity> sortNearest(List<? extends Entity> list, Vector3D target) {
        List<? extends Entity> sorted = new ArrayList<>(list);
        sorted.sort(Comparator.comparingDouble(entity -> sqrt(entity.pos, target)));
        return sorted;
    }
}
