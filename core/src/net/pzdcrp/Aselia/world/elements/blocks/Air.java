package net.pzdcrp.Aselia.world.elements.blocks;

import net.pzdcrp.Aselia.data.AABBList;
import net.pzdcrp.Aselia.data.Vector3D;

public class Air extends Block {
	public Air(Vector3D pos) {
		super(pos, null);
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
		return true;
	}

	@Override
	public Block clone(Vector3D poss) {
		return new Air(poss);
	}

	@Override
	public int getId() {
		return 0;
	}
}
