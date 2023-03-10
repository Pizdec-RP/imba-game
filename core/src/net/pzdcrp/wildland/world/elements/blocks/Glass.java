package net.pzdcrp.wildland.world.elements.blocks;

import net.pzdcrp.wildland.GameInstance;
import net.pzdcrp.wildland.data.BlockFace;
import net.pzdcrp.wildland.data.Vector3D;

public class Glass extends Block {
	static String tname = "glass";
	public Glass(Vector3D pos,BlockFace blockface) {
		super(pos, tname);
	}
	
	@Override
	public BlockType getType() {
		return BlockType.glass;
	}
	
	@Override
	public Block clone() {
		return new Glass(this.pos,null);
	}
}
