package net.pzdcrp.wildland.world.elements.blocks;

import com.badlogic.gdx.graphics.Texture;

import net.pzdcrp.wildland.data.BlockFace;
import net.pzdcrp.wildland.data.Vector3D;
import net.pzdcrp.wildland.world.elements.blocks.Block.BlockType;

public class Air extends Block {
	public Air(Vector3D pos, BlockFace blockface) {
		super(pos,null);
	}

	@Override
	public boolean isRenderable() {
		return false;
	}
	
	@Override
	public boolean isCollide() {
		return false;
	}
	
	@Override
	public BlockType getType() {
		return BlockType.air;
	}
	
	@Override
	public Block clone() {
		return new Air(this.pos,null);
	}
}
