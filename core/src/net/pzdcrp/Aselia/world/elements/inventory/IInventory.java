package net.pzdcrp.Aselia.world.elements.inventory;

import net.pzdcrp.Aselia.world.elements.entities.Entity;
import net.pzdcrp.Aselia.world.elements.inventory.items.Item;

public interface IInventory {
	public Item getSlot(int index);

	public void render();

	public void setCurrentSlotInt(int i);

	public int getCurrentSlotInt();

	public default void onRClick() {

	}
	@Deprecated
	public default void onLClick() {

	}

	public void dropAllItems();

	public Entity getOwner();
}
