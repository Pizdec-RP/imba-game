package net.pzdcrp.Hyperborea.world.elements.blocks;

import com.badlogic.gdx.graphics.Texture;

import net.pzdcrp.Hyperborea.data.BlockFace;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.world.elements.blocks.Block.BlockType;

public class Air extends Liquifyable {
	public Air(Vector3D pos) {
		super(pos);
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
	public Block clone(Vector3D poss) {
		return new Air(poss);
	}
}
