package net.pzdcrp.wildland.world.elements.inventory.items;

import com.badlogic.gdx.graphics.Texture;

import net.pzdcrp.wildland.GameInstance;
import net.pzdcrp.wildland.data.BlockFace;
import net.pzdcrp.wildland.utils.VectorU;
import net.pzdcrp.wildland.world.elements.blocks.Air;
import net.pzdcrp.wildland.world.elements.blocks.Dirt;
import net.pzdcrp.wildland.world.elements.blocks.Grass;
import net.pzdcrp.wildland.world.elements.inventory.IInventory;

public class GrassItem extends Item {
	public GrassItem(IInventory inventory) {
		super(inventory);
	}

	@Override
	public void onLClick() {
		if (this.inventory.owner.currentAimBlock == null) return;
		this.inventory.owner.placeBlock(
			new Grass(
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

	@Override
	public String getName() {
		return "Grass";
	}
}