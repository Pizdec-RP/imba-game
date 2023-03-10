package net.pzdcrp.wildland.world.elements.inventory.items;

import net.pzdcrp.wildland.GameInstance;
import net.pzdcrp.wildland.data.BlockFace;
import net.pzdcrp.wildland.world.elements.blocks.Dirt;
import net.pzdcrp.wildland.world.elements.inventory.IInventory;
import net.pzdcrp.wildland.world.elements.inventory.Item;

public class DirtItem extends Item {
	public DirtItem(IInventory inventory) {
		super(inventory);
		
	}

	@Override
	public void onLClick() {
		this.inventory.owner.placeBlock(new Dirt(this.inventory.owner.currentAimBlock,BlockFace.PX));
	}
}
