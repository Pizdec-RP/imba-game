package net.pzdcrp.Hyperborea.world.elements.inventory.items;

import com.badlogic.gdx.graphics.Texture;

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.BlockFace;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.utils.VectorU;
import net.pzdcrp.Hyperborea.world.elements.blocks.Air;
import net.pzdcrp.Hyperborea.world.elements.blocks.Block;
import net.pzdcrp.Hyperborea.world.elements.blocks.Dirt;
import net.pzdcrp.Hyperborea.world.elements.blocks.Glass;
import net.pzdcrp.Hyperborea.world.elements.inventory.IInventory;

public class GlassItem extends Item {
	public GlassItem(IInventory inventory, int count) {
		super(inventory, 3, count);
	}

	@Override
	public void onRClick(Vector3D cp) {
		this.inventory.owner.placeBlock(
			new Glass(cp)
		);
	}

	@Override
	public String getName() {
		return "Glass";
	}
}