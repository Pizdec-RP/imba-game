package net.pzdcrp.Hyperborea.world.elements.generators;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import io.netty.util.internal.ConcurrentSet;
import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.ActionAuthor;
import net.pzdcrp.Hyperborea.data.BlockFace;
import net.pzdcrp.Hyperborea.data.Vector2I;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.utils.MathU;
import net.pzdcrp.Hyperborea.utils.VectorU;
import net.pzdcrp.Hyperborea.world.PlayerWorld;
import net.pzdcrp.Hyperborea.world.elements.Column;
import net.pzdcrp.Hyperborea.world.elements.blocks.Block;
import net.pzdcrp.Hyperborea.world.elements.blocks.OakLog;
import net.pzdcrp.Hyperborea.world.elements.blocks.OakLeaves;

public class DefaultWorldGenerator {
	public static Map<Vector2I, CopyOnWriteArrayList<Block>> toadd = new ConcurrentHashMap<>();
	public enum Biome {
		field, forest, ocean;
	}
	public static void gen(Column c) {
		for (int px = 0; px < 16; px++) {
	        for (int pz = 0; pz < 16; pz++) {
	        	int x = c.pos.x*16+px;
	        	int z = c.pos.z*16+pz;
        		Biome biome = Biome.field;//calculate biome by xz pos
        		if (biome == Biome.field) {
        			int fieldMaxHeight = (int) (PlayerWorld.maxheight*0.4f);
        			int fieldMinHeight = (int) (PlayerWorld.maxheight*0.25f);
        			float sharpness = 0.02f;
        			double noise = Noise.get(x*sharpness, z*sharpness);
        			int maxy = MathU.diap(fieldMinHeight, fieldMaxHeight, noise);
        			for (int y = 0; y < PlayerWorld.maxheight; y++) {
        				if (y < maxy) {
        					c.fastSetBlock(px,y,pz, 6);
        				} else if (y == maxy && Double.toString(noise).contains("34")) {
        					c.fastSetBlock(px,y,pz, 23);
        				} else {
        					c.fastSetBlock(px,y,pz, 0);
        				}
        			}
        			c.recalculateSLMD(px,pz);
        			if (Double.toString(noise).contains("145")) {
        				Vector3D treepos = new Vector3D(c.normx(px), c.getSLMD(px, pz), c.normz(pz));
        				generateTree(treepos);
        			}
        		}
	        }
	    }
	}
	
	public static void generateTree(Vector3D pos) {
		CopyOnWriteArrayList<Block> blocks = new CopyOnWriteArrayList<>();
		pos.y -= 2;
		int trunk = MathU.rndi(9, 12);
		int maxcrona = trunk+1;
		int mincrona = 6;
		
		for (int i = 0; i < trunk; i++) {
            blocks.add(new OakLog(pos.add(0, i, 0), BlockFace.PY));
        }
		int mid = (mincrona+maxcrona)/2;
		for (int i = mincrona; i < maxcrona; i++) {
			int rad = 3;
			if (i == mincrona) rad = 1;
			if (i >= maxcrona - 1) rad = 2;
			if (i+1 >= mid && i-1 <= mid) pos = pos.add(MathU.rndi(-1, 1), 0, MathU.rndi(-1, 1)); 
			blocks.addAll(getBlocksInRadius(pos.add(0, i, 0), rad, new OakLeaves(new Vector3D())));
		}
		
        for (Block b : blocks) {
        	Vector2I bp2 = VectorU.posToColumn(b.pos);
        	if (Hpb.world.loadedColumns.containsKey(bp2)) {
        		Hpb.world.setBlock(b, ActionAuthor.world);
        	} else {
        		if (!toadd.containsKey(bp2)) toadd.put(bp2, new CopyOnWriteArrayList<Block>());
        		toadd.get(bp2).add(b);
        	}
        }
    }

    public static Set<Block> getBlocksInRadius(Vector3D center, int radius, Block block) {
        Set<Block> blocks = new HashSet<>();

        double minX = center.getX() - radius;
        double maxX = center.getX() + radius;
        double minZ = center.getZ() - radius;
        double maxZ = center.getZ() + radius;
        double y = center.getY();

        for (double x = minX; x <= maxX; x++) {
            for (double z = minZ; z <= maxZ; z++) {
                Vector3D bp = new Vector3D(x, y, z);
                if (bp.distanceSq(center) <= radius) blocks.add(block.clone(bp));
            }
        }

        return blocks;
    }
}