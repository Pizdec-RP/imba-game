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
import net.pzdcrp.Hyperborea.data.MBIM;
import net.pzdcrp.Hyperborea.data.Vector3D;
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
	public void addModel(boolean py, boolean ny, boolean nx, boolean px, boolean nz, boolean pz, MBIM mbim) {
		MeshPartBuilder a = mbim.obtain("stone", tname);
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
