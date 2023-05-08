package net.pzdcrp.Hyperborea.world.elements.blocks;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;

import net.pzdcrp.Hyperborea.data.BlockFace;
import net.pzdcrp.Hyperborea.data.MBIM;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.extended.SexyMeshBuilder;
import net.pzdcrp.Hyperborea.utils.ModelUtils;

public class Planks extends Block {
	public static String tname = "planks";
	public Planks(Vector3D pos) {
		super(pos, tname);
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
	public void addModel(boolean py, boolean ny, boolean nx, boolean px, boolean nz, boolean pz, MBIM mbim) {
		SexyMeshBuilder a = mbim.obtain("planks", tname, pos);
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
