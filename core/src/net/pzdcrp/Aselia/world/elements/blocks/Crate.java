package net.pzdcrp.Aselia.world.elements.blocks;

import net.pzdcrp.Aselia.Hpb;
import net.pzdcrp.Aselia.data.BlockModelBuilder;
import net.pzdcrp.Aselia.data.Vector3D;
import net.pzdcrp.Aselia.data.MBIM.offset;
import net.pzdcrp.Aselia.extended.SexyMeshBuilder;
import net.pzdcrp.Aselia.player.Player;
import net.pzdcrp.Aselia.utils.ModelUtils;
import net.pzdcrp.Aselia.world.World;
import net.pzdcrp.Aselia.world.elements.Column;
import net.pzdcrp.Aselia.world.elements.storages.ChestItemStorage;
import net.pzdcrp.Aselia.world.elements.storages.ItemStorage;

public class Crate extends Block {
	public static String tname = "crate";
	public Crate(Vector3D pos) {
		super(pos, tname);
	}
	
	@Override
	public boolean clickable() {
		return true;
	}
	
	@Override
	public BlockType getType() {
		return BlockType.solid;
	}
	
	@Override
	public Block clone(Vector3D poss) {
		return new Crate(poss);
	}
	
	@Override
	public void addModel(boolean py, boolean ny, boolean nx, boolean px, boolean nz, boolean pz, BlockModelBuilder mbim) {
		SexyMeshBuilder a = mbim.obtain(pos, this.isTransparent());
		ModelUtils.setTransform(pos);
		Hpb.mutex.hookuvr(a, tname, 0, 0, 1, 1);
		
		mbim.setCuroffset(offset.py);
    	if (!py) ModelUtils.buildTopX(a);//PY
    	mbim.setCuroffset(offset.nx);
	    if (!nx) ModelUtils.buildLeftPY(a);//NX
	    mbim.setCuroffset(offset.px);
	    if (!px) ModelUtils.buildRightPY(a);//PX
	    mbim.setCuroffset(offset.nz);
	    if (!nz) ModelUtils.buildFrontY(a);//NZ
	    mbim.setCuroffset(offset.pz);
	    if (!pz) ModelUtils.buildBackY(a);//PZ
	    mbim.setCuroffset(offset.ny);
	    if (!ny) ModelUtils.buildBottomX(a);//NY
	}
	
	@Override
	public void onClick(Player actor) {
		Column c = actor.world.getColumn(pos.x, pos.z);
		ItemStorage is = c.blockData.get(pos);
		if (is == null) {
			c.blockData.put(pos, is = new ChestItemStorage());
		}
		actor.castedInv.open(is);
	}
	
	@Override
	public float getResistance() {
		return 2.5f;
	}
	
	@Override
	public void onBreak(World world) {
		super.onBreak(world);
		Column c = world.getColumn(pos.x, pos.z);
		ItemStorage is = c.blockData.remove(pos);
		is.onbreak(pos.add(0.5d));
	}
}
