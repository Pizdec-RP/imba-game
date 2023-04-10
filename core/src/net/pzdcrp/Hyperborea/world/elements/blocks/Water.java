package net.pzdcrp.Hyperborea.world.elements.blocks;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;

import net.pzdcrp.Hyperborea.data.AABB;
import net.pzdcrp.Hyperborea.data.MBIM;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.utils.ModelUtils;

public class Water extends Liquifyable {
	public static String tname = "water";
	
	public Water(Vector3D pos, int liquidLevel) {
		super(pos, tname, liquidLevel);
	}
	
	public boolean ableToTick = true;
	private int tickcd = 2;
	@Override
	public void tick() {
		if (!ableToTick) return;
		if (tickcd > 0) {
			tickcd--;
			return;
		}
		//System.out.println("tick pos: "+this.pos.toString());
		tickcd = 10;
		Block under = world.getBlock(pos.add(0, -1, 0));
		if (under instanceof Water) {
			Water nw = (Water)under;
			int ost = nw.liqlide(ll);
			if (ost == 0) {
				world.setBlock(new Air(this.pos));
				ableToTick=false;
			} else {
				this.ll = ost;
				this.updateCurrentChunkModel();
				ableToTick=false;
			}
		} else if (under instanceof Air) {
			world.setBlock(new Air(this.pos));
			this.pos = under.pos;
			this.hitbox = new AABB(pos.x,pos.y,pos.z,pos.x+1,pos.y+heightMap.get(ll),pos.z+1);
			world.setBlock(this);
			return;
		}
		if (ll != 1) {
			Block[] neighbors = new Block[] {
					world.getBlock(pos.add(1, 0, 0)),
					world.getBlock(pos.add(-1, 0, 0)),
					world.getBlock(pos.add(0, 0, 1)),
					world.getBlock(pos.add(0, 0, -1))};
			List<Block> a;
			Collections.shuffle(a = Arrays.asList(neighbors));
			neighbors = a.toArray(new Block[4]);
			for (Block n : neighbors) {
				if (this.ll <= 1) break;
				if (n instanceof Liquifyable) {
					Liquifyable ln = (Liquifyable) n;
					if (ln.ll < this.ll) {
						ln.liqlide(1);
						this.ll--;
					}
				}
			}
			ableToTick=false;
		} else if (ll == 1) {
			Block[] neighbors = new Block[] {
					world.getBlock(pos.add(1, 0, 0)),
					world.getBlock(pos.add(-1, 0, 0)),
					world.getBlock(pos.add(0, 0, 1)),
					world.getBlock(pos.add(0, 0, -1))};
			List<Block> a;
			Collections.shuffle(a = Arrays.asList(neighbors));
			neighbors = a.toArray(new Block[4]);
			for (Block n : neighbors) {
				if (n instanceof Air) {
					Block uunder = n.under();
					if(uunder instanceof Air || uunder instanceof Water) {
						world.setBlock(new Air(this.pos));
						this.pos = n.pos;
						this.hitbox = new AABB(pos.x,pos.y,pos.z,pos.x+1,pos.y+heightMap.get(ll),pos.z+1);
						world.setBlock(this);
						return;
					}
				}
			}
			ableToTick=false;
		}
	}
	
	@Override
	public void onNeighUpdate() {
		this.ableToTick = true;
	}
	
	@Override
	public void addModel(boolean py, boolean ny, boolean nx, boolean px, boolean nz, boolean pz, MBIM mbim) {
		MeshPartBuilder mpb = mbim.obtain("tr:water", tname);
		ModelUtils.setTransform(pos);
		float h = heightMap.get(ll).floatValue();
		mpb.setUVRange(0, 0, 1, 1);
		ModelUtils.buildBottomX(mpb);
		if (!py) {
			mpb.rect(
			ModelUtils.sp.x-0.5f, ModelUtils.sp.y-0.5f+h, ModelUtils.sp.z+0.5f,
			ModelUtils.sp.x-0.5f, ModelUtils.sp.y-0.5f+h, ModelUtils.sp.z-0.5f,
			ModelUtils.sp.x+0.5f, ModelUtils.sp.y-0.5f+h, ModelUtils.sp.z-0.5f,
			ModelUtils.sp.x+0.5f, ModelUtils.sp.y-0.5f+h, ModelUtils.sp.z+0.5f,
			0, 1, 0);
		}
		mpb.setUVRange(0, 0, 1, h);
		if (!nx) {
			mpb.rect(
			ModelUtils.sp.x-0.5f, ModelUtils.sp.y-0.5f, ModelUtils.sp.z+0.5f,
			ModelUtils.sp.x-0.5f, ModelUtils.sp.y-0.5f, ModelUtils.sp.z-0.5f,
			ModelUtils.sp.x-0.5f, ModelUtils.sp.y-0.5f+h, ModelUtils.sp.z-0.5f,
			ModelUtils.sp.x-0.5f, ModelUtils.sp.y-0.5f+h, ModelUtils.sp.z+0.5f,
	        -1, 0, 0);
		}
		if (!px) {
			mpb.rect(
			ModelUtils.sp.x+0.5f, ModelUtils.sp.y-0.5f, ModelUtils.sp.z-0.5f,
			ModelUtils.sp.x+0.5f, ModelUtils.sp.y-0.5f, ModelUtils.sp.z+0.5f,
			ModelUtils.sp.x+0.5f, ModelUtils.sp.y-0.5f+h, ModelUtils.sp.z+0.5f,
			ModelUtils.sp.x+0.5f, ModelUtils.sp.y-0.5f+h, ModelUtils.sp.z-0.5f,
	         1, 0, 0);
		}
		if (!nz) {
			mpb.rect(
			ModelUtils.sp.x-0.5f, ModelUtils.sp.y-0.5f, ModelUtils.sp.z-0.5f,
			ModelUtils.sp.x+0.5f, ModelUtils.sp.y-0.5f, ModelUtils.sp.z-0.5f,
			ModelUtils.sp.x+0.5f, ModelUtils.sp.y-0.5f+h, ModelUtils.sp.z-0.5f,
			ModelUtils.sp.x-0.5f, ModelUtils.sp.y-0.5f+h, ModelUtils.sp.z-0.5f,
	        0, 0, -1);
		}
		if (!pz) {
			mpb.rect(
			ModelUtils.sp.x+0.5f, ModelUtils.sp.y-0.5f, ModelUtils.sp.z+0.5f,
			ModelUtils.sp.x-0.5f, ModelUtils.sp.y-0.5f, ModelUtils.sp.z+0.5f,
			ModelUtils.sp.x-0.5f, ModelUtils.sp.y-0.5f+h, ModelUtils.sp.z+0.5f,
			ModelUtils.sp.x+0.5f, ModelUtils.sp.y-0.5f+h, ModelUtils.sp.z+0.5f,
	         0, 0, 1);
		}
	}
	
	@Override
	public Block clone(Vector3D poss) {
		return new Water(poss,ll);
	}
	
	@Override
	public BlockType getType() {
		return BlockType.transparent;
	}
	
	@Override
	public boolean tickable() {
		return true;
	}
	
	@Override
	public boolean equals(Object block) {
		if (block instanceof Block) {
			Block b = (Block) block;
			if (block.getClass() == this.getClass() && b.getFace() == this.getFace() && this.ll == ((Water)block).ll) return true;
		}
		return false;
	}
}
