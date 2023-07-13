package net.pzdcrp.Hyperborea.world.elements.blocks;

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.MBIM;
import net.pzdcrp.Hyperborea.data.MBIM.offset;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.extended.SexyMeshBuilder;
import net.pzdcrp.Hyperborea.utils.ModelUtils;

public class OakLeaves extends Block {
	public static String tname = "oakleaves";
	public OakLeaves(Vector3D pos) {
		super(pos,tname);
	}
	
	
	@Override
	public BlockType getType() {
		return BlockType.solid;
	}
	
	@Override
	public Block clone(Vector3D poss) {
		return new OakLeaves(poss);
	}
	
	@Override
	public void addModel(boolean py, boolean ny, boolean nx, boolean px, boolean nz, boolean pz, MBIM mbim) {
		SexyMeshBuilder a = mbim.obtain(pos, this.isTransparent());
		ModelUtils.setTransform(pos);
		Hpb.mutex.hookuvr(a, tname, 0, 0, 1, 1);
		
		mbim.curoffset = offset.py;
    	if (!py) ModelUtils.buildTopX(a);//PY
    	mbim.curoffset = offset.nx;
	    if (!nx) ModelUtils.buildLeftPY(a);//NX
	    mbim.curoffset = offset.px;
	    if (!px) ModelUtils.buildRightPY(a);//PX
	    mbim.curoffset = offset.nz;
	    if (!nz) ModelUtils.buildFrontY(a);//NZ
	    mbim.curoffset = offset.pz;
	    if (!pz) ModelUtils.buildBackY(a);//PZ
	    mbim.curoffset = offset.ny;
	    if (!ny) ModelUtils.buildBottomX(a);//NY
	}
	
	@Override
	public float getResistance() {
		return 0.5f;
	}
}	

