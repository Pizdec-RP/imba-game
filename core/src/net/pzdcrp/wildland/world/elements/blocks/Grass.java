package net.pzdcrp.wildland.world.elements.blocks;

import net.pzdcrp.wildland.data.Vector3D;

public class Grass extends Block {
	static String tname = "grassblock";
	public Grass(Vector3D pos) {
		super(pos,1d,1d,1d, tname);
	}
	
	public static int id() {
		return 6;
	}
	
	@Override
	public BlockType getType() {
		return BlockType.solid;
	}
}
