package net.pzdcrp.wildland.world.elements.blocks;

import net.pzdcrp.wildland.GameInstance;
import net.pzdcrp.wildland.data.Vector3D;

public class Glass extends Block {
	static String tname = "glass";
	public Glass(Vector3D pos) {
		super(pos,1d,1d,1d, tname);
	}
	
	public static int id() {
		return 3;
	}
	
	@Override
	public BlockType getType() {
		return BlockType.glass;
	}
}
