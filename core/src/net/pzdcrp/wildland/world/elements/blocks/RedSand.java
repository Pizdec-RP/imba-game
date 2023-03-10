package net.pzdcrp.wildland.world.elements.blocks;

import net.pzdcrp.wildland.GameInstance;
import net.pzdcrp.wildland.data.BlockFace;
import net.pzdcrp.wildland.data.Vector3D;
import net.pzdcrp.wildland.world.elements.blocks.Block.BlockType;

public class RedSand extends Block {
	static String tname = "redsand";
	public RedSand(Vector3D pos,BlockFace blockface) {
		super(pos,tname);
	}
	
	public static int id() {
		return 4;
	}
	
	@Override
	public BlockType getType() {
		return BlockType.solid;
	}
	
	@Override
	public Block clone() {
		return new RedSand(this.pos,null);
	}
}
