package net.pzdcrp.wildland.world.elements.inventory;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

import net.pzdcrp.wildland.world.elements.entities.Entity;
import net.pzdcrp.wildland.world.elements.inventory.items.Item;

public class IInventory {
	public Entity owner;
	
	public IInventory(Entity owner) {
		this.owner = owner;
	}
	
	public Item getSlot(int index) {
		return null;
	}
	
	public void addItem(Item item, int index) {
		
	}

	public void render(SpriteBatch spriteBatch) {
		
	}
	
	public void setCurrentSlotInt(int i) {
		
	}
	
	public int getCurrentSlotInt() {
		return 0;
	}
}
