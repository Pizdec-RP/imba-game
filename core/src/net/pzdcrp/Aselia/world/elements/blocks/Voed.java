package net.pzdcrp.Aselia.world.elements.blocks;

import net.pzdcrp.Aselia.data.AABBList;
import net.pzdcrp.Aselia.data.Vector3D;

public class Voed extends Block {
	public Voed(Vector3D pos) {
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
	public AABBList getHitbox() {
		return null;
	}

	@Override
	public BlockType getType() {
		return BlockType.air;
	}

	@Override
	public boolean isTransparent() {
		return false;
	}

	@Override
	public Block clone(Vector3D poss) {
		return new Voed(poss);
	}
}
