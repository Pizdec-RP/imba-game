package net.pzdcrp.Hyperborea.world.elements.blocks;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.BlockFace;
import net.pzdcrp.Hyperborea.data.BlockModelBuilder;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.data.MBIM.offset;
import net.pzdcrp.Hyperborea.extended.SexyMeshBuilder;
import net.pzdcrp.Hyperborea.utils.ModelUtils;
import net.pzdcrp.Hyperborea.world.elements.blocks.Block.BlockType;

public class Stone extends Block {
	public static String tname = "stone";
	public Stone(Vector3D pos) {
		super(pos, tname);
	}
	
	@Override
	public BlockType getType() {
		return BlockType.solid;
	}
	
	@Override
	public Block clone(Vector3D poss) {
		return new Stone(poss);
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
	public float getResistance() {
		return 5f;
	}
}
