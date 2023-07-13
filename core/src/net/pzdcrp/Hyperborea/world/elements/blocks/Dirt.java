package net.pzdcrp.Hyperborea.world.elements.blocks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.BlockFace;
import net.pzdcrp.Hyperborea.data.MBIM;
import net.pzdcrp.Hyperborea.data.MBIM.offset;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.extended.SexyMeshBuilder;
import net.pzdcrp.Hyperborea.utils.ModelUtils;
import net.pzdcrp.Hyperborea.world.elements.blocks.Block.BlockType;

public class Dirt extends Block {
	public static String tname = "dirt";
	public Dirt(Vector3D pos) {
		super(pos,tname);
	}
	
	
	@Override
	public BlockType getType() {
		return BlockType.solid;
	}
	
	@Override
	public Block clone(Vector3D poss) {
		return new Dirt(poss);
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
		return 1f;
	}
}	
