package net.pzdcrp.Hyperborea.world.elements.blocks;

import net.pzdcrp.Hyperborea.data.BlockFace;
import net.pzdcrp.Hyperborea.data.Vector3D;

public class FacingBlock extends Block {
	public BlockFace blockface;
	
	public FacingBlock(Vector3D pos, String texture, BlockFace face) {
		super(pos, texture);
		this.blockface = face;
	}
	
	@Override
	public BlockFace getFace() {
		return blockface;
	}
}
