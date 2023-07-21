package net.pzdcrp.Hyperborea.world.elements.blocks;

import com.badlogic.gdx.math.Vector3;

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.AABB;
import net.pzdcrp.Hyperborea.data.BlockModelBuilder;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.data.MBIM.offset;
import net.pzdcrp.Hyperborea.extended.SexyMeshBuilder;
import net.pzdcrp.Hyperborea.utils.ModelUtils;
import net.pzdcrp.Hyperborea.world.elements.blocks.Block.BlockType;

public class Weed extends Block {
	public static String tname = "weed";
	public Weed(Vector3D pos) {
		super(pos, tname);
	}
	
	@Override
	public void addModel(boolean py, boolean ny, boolean nx, boolean px, boolean nz, boolean pz, BlockModelBuilder mbim) {
		SexyMeshBuilder a = mbim.obtain(pos, this.isTransparent());
		ModelUtils.setTransform(pos);
		Hpb.mutex.hookuvr(a, tname, 0, 0, 1, 1);
		
		mbim.setCuroffset(offset.no);
		Vector3 sp = ModelUtils.sp;
	    a.rect(
	    		sp.x+0.1f, sp.y, sp.z+1f,
	    		sp.x+0.1f, sp.y, sp.z,
	    		sp.x+0.1f, sp.y+1f, sp.z,
	    		sp.x+0.1f, sp.y+1f, sp.z+1f,
	            -1, 0, 0);
	    a.rect(
	    		sp.x+0.9f, sp.y, sp.z,
	    		sp.x+0.9f, sp.y, sp.z+1f,
	    		sp.x+0.9f, sp.y+1f, sp.z+1f,
	    		sp.x+0.9f, sp.y+1f, sp.z,
	             1, 0, 0);
	    a.rect(
	    		sp.x, sp.y, sp.z+0.1f,
	    		sp.x+1f, sp.y, sp.z+0.1f,
	    		sp.x+1f, sp.y+1f, sp.z+0.1f,
	    		sp.x, sp.y+1f, sp.z+0.1f,
	            0, 0, -1);
	    a.rect(
	    		sp.x+1f, sp.y, sp.z+0.9f,
	    		sp.x, sp.y, sp.z+0.9f,
	    		sp.x, sp.y+1f, sp.z+0.9f,
	    		sp.x+1f, sp.y+1f, sp.z+0.9f,
	             0, 0, 1);
	}

	@Override
	public boolean isRenderable() {
		return true;
	}
	
	@Override
	public boolean isCollide() {
		return false;
	}
	
	@Override
	public BlockType getType() {
		return BlockType.noncollideabe;
	}
	
	@Override
	public boolean isTransparent() {
		return true;
	}
	
	@Override
	public Block clone(Vector3D poss) {
		return new Weed(poss);
	}
	
	@Override
	public void onNeighUpdate() {
		if (this.under().getType() != BlockType.solid) {
			world.breakBlock(pos);
		}
	}
	
	@Override
	public float getResistance() {
		return 0f;
	}
}
