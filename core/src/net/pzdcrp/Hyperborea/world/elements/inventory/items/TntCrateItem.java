package net.pzdcrp.Hyperborea.world.elements.inventory.items;

import com.badlogic.gdx.graphics.Texture;

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.BlockFace;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.utils.VectorU;
import net.pzdcrp.Hyperborea.world.elements.blocks.Air;
import net.pzdcrp.Hyperborea.world.elements.blocks.Block;
import net.pzdcrp.Hyperborea.world.elements.blocks.OakLog;
import net.pzdcrp.Hyperborea.world.elements.blocks.TntCrate;
import net.pzdcrp.Hyperborea.world.elements.inventory.IInventory;

public class TntCrateItem extends Item {
	public TntCrateItem(IInventory inventory, int count) {
		super(inventory, 8, count);
	}

	@Override
	public void onRClick() {
		if (this.inventory.owner.currentAimBlock == null) return;
		Vector3D clickedBlock = VectorU.fromFace(
				this.inventory.owner.currentAimBlock.pos,
				this.inventory.owner.currentAimFace
			);
		boolean bol = this.inventory.owner.currentAimBlock.onClick(this.inventory.owner);
		System.out.println(bol);
		if (bol) return;
		this.inventory.owner.placeBlock(
			new TntCrate(
				clickedBlock
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
	public String getName() {
		return "TNT crate";
	}
}
