package net.pzdcrp.Aselia.world.elements.blocks;

import net.pzdcrp.Aselia.Hpb;
import net.pzdcrp.Aselia.data.AABB;
import net.pzdcrp.Aselia.data.AABBList;
import net.pzdcrp.Aselia.data.BlockModelBuilder;
import net.pzdcrp.Aselia.data.Vector3D;
import net.pzdcrp.Aselia.data.MBIM.offset;
import net.pzdcrp.Aselia.extended.SexyMeshBuilder;
import net.pzdcrp.Aselia.utils.GameU;
import net.pzdcrp.Aselia.utils.ModelUtils;

public class OakSlab extends Block {
	public static String tname = "planks";
	public boolean up = false;
	
	public OakSlab(Vector3D pos, boolean upp) {
		super(pos, tname);
		this.up = upp;
		if (up) {
			hitbox = new AABBList(new AABB(pos.x,pos.y+0.5,pos.z,pos.x+1,pos.y+1,pos.z+1));
		} else {
			hitbox = new AABBList(new AABB(pos.x,pos.y,pos.z,pos.x+1,pos.y+0.5,pos.z+1));
		}
	}

	@Override
	public BlockType getType() {
		return BlockType.slab;//FIXME халтура
	}

	@Override
	public Block clone(Vector3D poss) {
		return new OakSlab(poss, this.up);
	}
	
	@Override
	public boolean equals(Object block) {
		if (block instanceof Block) {
			Block b = (Block) block;
			if (block.getClass() == this.getClass()) {
				if (up == ((OakSlab)b).up) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void addModel(boolean py, boolean ny, boolean nx, boolean px, boolean nz, boolean pz, BlockModelBuilder mbim) {
		SexyMeshBuilder a = mbim.obtain(pos, this.isTransparent());
		ModelUtils.setTransform(pos);
		Hpb.mutex.hookuvr(a, tname, 0, 0, 1, 1);
		
		ModelUtils.setModelSizes(hitbox.get()[0]);
		
    	if (!py) {
    		mbim.setCuroffset(offset.py);
    		ModelUtils.buildTopX(a);//PY
    	}
    	if (!ny) {
	    	mbim.setCuroffset(offset.ny);
	    	ModelUtils.buildBottomX(a);//NY
	    }
    	
    	if (up) {
			Hpb.mutex.hookuvr(a, tname, 0, 0.5f, 1, 1);
		} else {
			Hpb.mutex.hookuvr(a, tname, 0, 0, 1, 0.5f);
		}
    	
	    if (!nx) {
	    	mbim.setCuroffset(offset.nx);
	    	ModelUtils.buildLeftPY(a);//NX
	    }
	    if (!px) {
	    	mbim.setCuroffset(offset.px);
	    	ModelUtils.buildRightPY(a);//PX
	    }
	    if (!nz) {
	    	mbim.setCuroffset(offset.nz);
	    	ModelUtils.buildFrontY(a);//NZ
	    }
	    if (!pz) {
	    	mbim.setCuroffset(offset.pz);
	    	ModelUtils.buildBackY(a);//PZ
	    }
	}

	@Override
	public float getResistance() {
		return 2.5f;
	}
}
