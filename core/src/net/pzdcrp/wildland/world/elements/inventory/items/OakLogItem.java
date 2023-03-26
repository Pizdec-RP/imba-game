package net.pzdcrp.wildland.world.elements.inventory.items;

import com.badlogic.gdx.graphics.Texture;

import net.pzdcrp.wildland.GameInstance;
import net.pzdcrp.wildland.data.BlockFace;
import net.pzdcrp.wildland.utils.VectorU;
import net.pzdcrp.wildland.world.elements.blocks.Air;
import net.pzdcrp.wildland.world.elements.blocks.Dirt;
import net.pzdcrp.wildland.world.elements.blocks.OakLog;
import net.pzdcrp.wildland.world.elements.blocks.Stone;
import net.pzdcrp.wildland.world.elements.inventory.IInventory;

public class OakLogItem extends Item {
	public OakLogItem(IInventory inventory) {
		super(inventory);
	}

	@Override
	public void onLClick() {
		if (this.inventory.owner.currentAimBlock == null) return;
		this.inventory.owner.placeBlock(
			new OakLog(
				VectorU.fromFace(
					this.inventory.owner.currentAimBlock.pos,
					this.inventory.owner.currentAimFace
				),
				this.inventory.owner.currentAimFace
			)
		);
	}
	
	@Override
	public void onRClick() {
		if (this.inventory.owner.currentAimBlock == null) return;
		this.inventory.owner.placeBlock(
			new Air(
				this.inventory.owner.currentAimBlock.pos,
				BlockFace.PX
			)
		);
	}
	
	@Override
	public Texture getTexture() {
		return null;
	}

	@Override
	public String getName() {
		return "Oak log";
	}
}

