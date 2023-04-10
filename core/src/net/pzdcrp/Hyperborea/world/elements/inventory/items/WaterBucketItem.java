package net.pzdcrp.Hyperborea.world.elements.inventory.items;

import com.badlogic.gdx.graphics.Texture;

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.BlockFace;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.utils.VectorU;
import net.pzdcrp.Hyperborea.world.elements.blocks.Air;
import net.pzdcrp.Hyperborea.world.elements.blocks.Grass;
import net.pzdcrp.Hyperborea.world.elements.blocks.Water;
import net.pzdcrp.Hyperborea.world.elements.inventory.IInventory;

public class WaterBucketItem extends Item {

	public WaterBucketItem(IInventory inventory, int id) {
		super(inventory, id);
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
			new Water(
				clickedBlock,
				3
			)
		);
	}
	
	@Override
	public void onLClick() {
		if (this.inventory.owner.currentAimBlock == null) return;
		this.inventory.owner.placeBlock(
			new Air(
				this.inventory.owner.currentAimBlock.pos
			)
		);
	}
	
	@Override
	public int stackSize() {
		return 1;
	}
	
	@Override
	public boolean isModel() {
		return false;
	}
	
	@Override
	public Texture getTexture() {
		return Hpb.getTexture("waterbucket");
	}
	
	@Override
	public String getName() {
		return "water bucket";
	}
}
