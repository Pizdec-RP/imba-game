package net.pzdcrp.wildland.world.elements.inventory.items;

import com.badlogic.gdx.graphics.Texture;

import net.pzdcrp.wildland.GameInstance;
import net.pzdcrp.wildland.data.BlockFace;
import net.pzdcrp.wildland.data.Vector3D;
import net.pzdcrp.wildland.utils.VectorU;
import net.pzdcrp.wildland.world.elements.blocks.Air;
import net.pzdcrp.wildland.world.elements.blocks.Block;
import net.pzdcrp.wildland.world.elements.blocks.Dirt;
import net.pzdcrp.wildland.world.elements.inventory.IInventory;

public class DirtItem extends Item {
	public DirtItem(IInventory inventory, int count) {
		super(inventory, 1, count);
	}

	@Override
	public void onRClick() {
		if (this.inventory.owner.currentAimBlock == null) return;
		Vector3D clickedBlock = VectorU.fromFace(
				this.inventory.owner.currentAimBlock.pos,
				this.inventory.owner.currentAimFace
			);
		if (this.inventory.owner.currentAimBlock.onClick(this.inventory.owner)) return;
		this.inventory.owner.placeBlock(
			new Dirt(
				clickedBlock,
				BlockFace.PX
			)
		);
	}
	
	@Override
	public void onLClick() {
		if (this.inventory.owner.currentAimBlock == null) return;
		this.inventory.owner.placeBlock(
			new Air(
				this.inventory.owner.currentAimBlock.pos,
				BlockFace.PX
			)
		);
	}
	
	@Override
	public String getName() {
		return "Dirt";
	}
}
