package net.pzdcrp.Aselia.world.elements.blocks;

import net.pzdcrp.Aselia.Hpb;
import net.pzdcrp.Aselia.data.AABB;
import net.pzdcrp.Aselia.data.AABBList;
import net.pzdcrp.Aselia.data.BlockModelBuilder;
import net.pzdcrp.Aselia.data.MBIM.offset;
import net.pzdcrp.Aselia.data.Vector3D;
import net.pzdcrp.Aselia.extended.SexyMeshBuilder;
import net.pzdcrp.Aselia.utils.ModelUtils;

public class Grass extends Block {
	public static String tname = "grassblock";
	public Grass(Vector3D pos) {
		super(pos, tname);
		hitbox = new AABBList(new AABB(pos.x,pos.y,pos.z,pos.x+1,pos.y+1,pos.z+1));
	}

	@Override
	public BlockType getType() {
		return BlockType.solid;
	}

	@Override
	public Block clone(Vector3D poss) {
		return new Grass(poss);
	}

	private static final float t = 1f/3f, tt = t*2;
	@Override
	public void addModel(boolean py, boolean ny, boolean nx, boolean px, boolean nz, boolean pz, BlockModelBuilder mbim) {
		SexyMeshBuilder a = mbim.obtain(pos, this.isTransparent());
		ModelUtils.setTransform(pos);
		Hpb.mutex.hookuvr(a, tname, 0, 0, 1, t);
		mbim.setCuroffset(offset.py);
    	if (!py) ModelUtils.buildTopX(a);//PY

    	Hpb.mutex.hookuvr(a, tname, 0, tt, 1, 1);
    	mbim.setCuroffset(offset.ny);
	    if (!ny) ModelUtils.buildBottomX(a);//NY

	    Hpb.mutex.hookuvr(a, tname, 0, t, 1, tt);
    	mbim.setCuroffset(offset.nx);
	    if (!nx) ModelUtils.buildLeftPY(a);//NX
	    mbim.setCuroffset(offset.px);
	    if (!px) ModelUtils.buildRightPY(a);//PX
	    mbim.setCuroffset(offset.nz);
	    if (!nz) ModelUtils.buildFrontY(a);//NZ
	    mbim.setCuroffset(offset.pz);
	    if (!pz) ModelUtils.buildBackY(a);//PZ

	}

	@Override
	public float getResistance() {
		return 1f;
	}


	@Override
	public int getId() {
		return 6;
	}
}
