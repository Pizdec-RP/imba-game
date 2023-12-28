package net.pzdcrp.Aselia.world.elements.blocks;

import com.badlogic.gdx.math.Vector3;

import net.pzdcrp.Aselia.Hpb;
import net.pzdcrp.Aselia.data.AABB;
import net.pzdcrp.Aselia.data.AABBList;
import net.pzdcrp.Aselia.data.BlockModelBuilder;
import net.pzdcrp.Aselia.data.MBIM.offset;
import net.pzdcrp.Aselia.data.Vector3D;
import net.pzdcrp.Aselia.extended.SexyMeshBuilder;
import net.pzdcrp.Aselia.utils.ModelUtils;
import net.pzdcrp.Aselia.world.World;

public class Weed extends Block {
	public static String tname = "weed";
	public Weed(Vector3D pos) {
		super(pos, tname);
		hitbox = new AABBList(new AABB(pos.x,pos.y,pos.z,pos.x+1,pos.y+1,pos.z+1));
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
	public void onNeighUpdate(World world) {
		if (world.getBlock(pos.x,pos.y-1,pos.z).getType() != BlockType.solid) {
			world.breakBlock(pos);
		}
	}

	@Override
	public float getResistance() {
		return 0f;
	}
}
