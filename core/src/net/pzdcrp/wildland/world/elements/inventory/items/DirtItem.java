package net.pzdcrp.wildland.world.elements.inventory.items;

import com.badlogic.gdx.graphics.Texture;

import net.pzdcrp.wildland.GameInstance;
import net.pzdcrp.wildland.data.BlockFace;
import net.pzdcrp.wildland.utils.VectorU;
import net.pzdcrp.wildland.world.elements.blocks.Air;
import net.pzdcrp.wildland.world.elements.blocks.Dirt;
import net.pzdcrp.wildland.world.elements.inventory.IInventory;
import net.pzdcrp.wildland.world.elements.inventory.Item;

public class DirtItem extends Item {
	public DirtItem(IInventory inventory) {
		super(inventory);
	}

	@Override
	public void onLClick() {
		if (this.inventory.owner.currentAimBlock == null) return;
		this.inventory.owner.placeBlock(
			new Dirt(
				VectorU.fromFace(
					this.inventory.owner.currentAimBlock.pos,
					this.inventory.owner.currentAimFace
				),
				BlockFace.PX
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
		return GameInstance.getTexture(Dirt.tname);
	}
}
