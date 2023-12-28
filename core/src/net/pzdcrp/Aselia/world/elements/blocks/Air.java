package net.pzdcrp.Aselia.world.elements.blocks;

import com.badlogic.gdx.graphics.Texture;

import net.pzdcrp.Aselia.data.AABB;
import net.pzdcrp.Aselia.data.AABBList;
import net.pzdcrp.Aselia.data.BlockFace;
import net.pzdcrp.Aselia.data.Vector3D;
import net.pzdcrp.Aselia.world.elements.blocks.Block.BlockType;

public class Air extends Block {
	public Air(Vector3D pos) {
		super(pos, null);
	}

	@Override
	public boolean isRenderable() {
		return false;
	}
	
	@Override
	public boolean isCollide() {
		return false;
	}
	
	@Override
	public AABBList getHitbox() {
		return null;
	}
	
	@Override
	public BlockType getType() {
		return BlockType.air;
	}
	
	@Override
	public boolean isTransparent() {
		return true;
	}
	
	@Override
	public Block clone(Vector3D poss) {
		return new Air(poss);
	}
}
