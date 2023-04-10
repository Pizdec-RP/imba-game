package net.pzdcrp.Hyperborea.world.elements.blocks;

import net.pzdcrp.Hyperborea.data.BlockFace;
import net.pzdcrp.Hyperborea.data.Vector3D;

public class Voed extends Block {
	public Voed(Vector3D pos) {
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
		return BlockType.air;
	}
	
	@Override
	public Block clone(Vector3D poss) {
		return new Voed(poss);
	}
}
