package net.pzdcrp.wildland.world.elements.blocks;

import net.pzdcrp.wildland.data.BlockFace;
import net.pzdcrp.wildland.data.Vector3D;

public class Grass extends Block {
	static String tname = "grassblock";
	public Grass(Vector3D pos,BlockFace blockface) {
		super(pos, tname);
	}
	
	@Override
	public BlockType getType() {
		return BlockType.solid;
	}
	
	@Override
	public Block clone() {
		return new Grass(this.pos,null);
	}
}
