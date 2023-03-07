package net.pzdcrp.wildland.world.elements.blocks;

import net.pzdcrp.wildland.data.Vector3D;

public class Voed extends Block {
	public Voed(Vector3D pos) {
		super(pos,1,1,1,null);
	}

	@Override
	public boolean isRenderable() {
		return false;
	}
	
	@Override
	public boolean isCollide() {
		return false;
	}
	
	public static int id() {
		return 5;
	}
	
	@Override
	public BlockType getType() {
		return BlockType.Void;
	}
}
