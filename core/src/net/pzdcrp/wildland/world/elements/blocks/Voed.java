package net.pzdcrp.wildland.world.elements.blocks;

import net.pzdcrp.wildland.data.BlockFace;
import net.pzdcrp.wildland.data.Vector3D;

public class Voed extends Block {
	public Voed(Vector3D pos, BlockFace blockface) {
		super(pos,null);
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
	public BlockType getType() {
		return BlockType.Void;
	}
	
	@Override
	public Block clone() {
		return new Voed(this.pos,null);
	}
}
