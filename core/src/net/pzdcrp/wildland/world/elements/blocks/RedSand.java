package net.pzdcrp.wildland.world.elements.blocks;

import net.pzdcrp.wildland.GameInstance;
import net.pzdcrp.wildland.data.Vector3D;
import net.pzdcrp.wildland.world.elements.blocks.Block.BlockType;

public class RedSand extends Block {
	static String tname = "redsand";
	public RedSand(Vector3D pos) {
		super(pos,1d,1d,1d, tname);
	}
	
	public static int id() {
		return 4;
	}
	
	@Override
	public BlockType getType() {
		return BlockType.solid;
	}
}
