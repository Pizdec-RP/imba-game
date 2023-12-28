package net.pzdcrp.Aselia.world.elements.inventory;

import net.pzdcrp.Aselia.world.elements.entities.Entity;
import net.pzdcrp.Aselia.world.elements.inventory.items.Item;

public class EntityInventory implements IInventory {

	private Entity owner;

	public EntityInventory(Entity owner) {
		this.owner = owner;
	}

	@Override
	public Entity getOwner() {
		return owner;
	}

	@Override
	public Item getSlot(int index) {
		return null;
	}

	@Override
	public void render() {
	}

	@Override
	public void setCurrentSlotInt(int i) {
	}

	@Override
	public int getCurrentSlotInt() {
		return 0;
	}

	@Override
	public void dropAllItems() {
	}

}
