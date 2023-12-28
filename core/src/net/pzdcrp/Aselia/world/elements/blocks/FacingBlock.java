package net.pzdcrp.Aselia.world.elements.blocks;

import net.pzdcrp.Aselia.data.BlockFace;
import net.pzdcrp.Aselia.data.Vector3D;

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
