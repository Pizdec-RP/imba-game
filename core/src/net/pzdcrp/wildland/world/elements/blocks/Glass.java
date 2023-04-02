package net.pzdcrp.wildland.world.elements.blocks;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;

import net.pzdcrp.wildland.GameInstance;
import net.pzdcrp.wildland.data.BlockFace;
import net.pzdcrp.wildland.data.MBIM;
import net.pzdcrp.wildland.data.Vector3D;
import net.pzdcrp.wildland.utils.ModelUtils;

public class Glass extends Block {
	public static String tname = "glass";
	public Glass(Vector3D pos,BlockFace blockface) {
		super(pos, tname);
	}
	
	@Override
	public BlockType getType() {
		return BlockType.glass;
	}
	
	@Override
	public Block clone() {
		return new Glass(this.pos,null);
	}
	
	@Override
	public void addModel(boolean py, boolean ny, boolean nx, boolean px, boolean nz, boolean pz, MBIM mbim) {
		MeshPartBuilder a = mbim.obtain("tr:glass", tname);
		ModelUtils.setTransform(pos);
		a.setUVRange(0, 0, 1, 1);
    	if (!py) ModelUtils.buildTopX(a);//PY
	    if (!nx) ModelUtils.buildLeftPY(a);//NX
	    if (!px) ModelUtils.buildRightPY(a);//PX
	    if (!nz) ModelUtils.buildFrontY(a);//NZ
	    if (!pz) ModelUtils.buildBackY(a);//PZ
	    if (!ny) ModelUtils.buildBottomX(a);//NY
	}
	
}
