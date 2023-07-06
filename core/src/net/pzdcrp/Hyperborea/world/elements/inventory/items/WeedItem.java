package net.pzdcrp.Hyperborea.world.elements.inventory.items;

import com.badlogic.gdx.graphics.Texture;

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.world.elements.blocks.Grass;
import net.pzdcrp.Hyperborea.world.elements.blocks.Weed;
import net.pzdcrp.Hyperborea.world.elements.inventory.IInventory;

public class WeedItem extends Item {
	public WeedItem(IInventory inventory, int count) {
		super(inventory, 9, count);
	}

	@Override
	public void onRClick(Vector3D cp) {
		this.inventory.owner.placeBlock(
			new Weed(cp)
		);
	}

	@Override
	public String getName() {
		return "Weed";
	}
	
	@Override
	public boolean isModel() {
		return false;
	}
	
	@Override
	public Texture getTexture() {
		return Hpb.mutex.getItemTexture("weed");
	}
}
