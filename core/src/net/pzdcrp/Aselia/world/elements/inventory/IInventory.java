package net.pzdcrp.Aselia.world.elements.inventory;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

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
