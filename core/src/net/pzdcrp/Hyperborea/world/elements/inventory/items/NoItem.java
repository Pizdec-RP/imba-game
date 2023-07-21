package net.pzdcrp.Hyperborea.world.elements.inventory.items;

import net.pzdcrp.Hyperborea.world.elements.blocks.Air;
import net.pzdcrp.Hyperborea.world.elements.inventory.IInventory;

public class NoItem extends Item {
	public NoItem() {
		super(0);
	}
	
	@Override
	public Item clone(int count) {
		return new NoItem();
	}
}
