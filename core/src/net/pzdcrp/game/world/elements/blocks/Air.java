package net.pzdcrp.game.world.elements.blocks;

import com.badlogic.gdx.graphics.Texture;

import net.pzdcrp.game.data.Vector3D;
import net.pzdcrp.game.world.elements.blocks.Block.BlockType;

public class Air extends Block {
	
	
	
	public Air(Vector3D pos) {
		super(pos,1,1,1,null);
	}

	@Override
	public boolean isRenderable() {
		return false;
	}
	
	@Override
	public boolean isCollide() {
		return false;
	}
	
	public static int id() {
		return 0;
	}
	
	@Override
	public BlockType getType() {
		return BlockType.air;
	}
}
