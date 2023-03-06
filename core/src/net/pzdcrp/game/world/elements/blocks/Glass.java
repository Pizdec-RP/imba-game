package net.pzdcrp.game.world.elements.blocks;

import net.pzdcrp.game.GameInstance;
import net.pzdcrp.game.data.Vector3D;

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
