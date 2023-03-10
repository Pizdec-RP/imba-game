package net.pzdcrp.wildland.world.elements.blocks;

import net.pzdcrp.wildland.data.BlockFace;
import net.pzdcrp.wildland.data.Vector3D;

public class FacingBlock extends Block {
	public BlockFace blockface;
	
	public FacingBlock(Vector3D pos, String tname, BlockFace face) {
		super(pos, tname);
	}
	
	@Override
	public BlockFace getFace() {
		return blockface;
	}
	
}
