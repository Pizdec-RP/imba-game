package net.pzdcrp.Aselia.world.elements.blocks;

import net.pzdcrp.Aselia.Hpb;
import net.pzdcrp.Aselia.data.AABB;
import net.pzdcrp.Aselia.data.AABBList;
import net.pzdcrp.Aselia.data.BlockModelBuilder;
import net.pzdcrp.Aselia.data.Vector3D;
import net.pzdcrp.Aselia.data.MBIM.offset;
import net.pzdcrp.Aselia.extended.SexyMeshBuilder;
import net.pzdcrp.Aselia.utils.ModelUtils;

public class OakSlab extends Block {
	public static String tname = "planks";
	private boolean up;
	public OakSlab(Vector3D pos, boolean up) {
		super(pos, tname);
		this.up = up;
		if (up) {
			hitbox = new AABBList(new AABB(pos.x,pos.y+0.5,pos.z,pos.x+1,pos.y+1,pos.z+1));
		} else {
			hitbox = new AABBList(new AABB(pos.x,pos.y,pos.z,pos.x+1,pos.y+0.5,pos.z+1));
		}
	}

	@Override
	public BlockType getType() {
		return BlockType.solid;
	}

	@Override
	public Block clone(Vector3D poss) {
		return new Planks(poss);
	}

	@Override
	public void addModel(boolean py, boolean ny, boolean nx, boolean px, boolean nz, boolean pz, BlockModelBuilder mbim) {
		SexyMeshBuilder a = mbim.obtain(pos, this.isTransparent());
		ModelUtils.setTransform(pos);
		if (up) Hpb.mutex.hookuvr(a, tname, 0, 0, 1, 1);
		else Hpb.mutex.hookuvr(a, tname, 0, 0, 1, 1);
		
		
    	if (!py) {
    		mbim.setCuroffset(offset.py);
    		ModelUtils.buildTopX(a);//PY
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
	    if (!ny) {
	    	mbim.setCuroffset(offset.ny);
	    	ModelUtils.buildBottomX(a);//NY
	    }
	}

	@Override
	public float getResistance() {
		return 2.5f;
	}
}
