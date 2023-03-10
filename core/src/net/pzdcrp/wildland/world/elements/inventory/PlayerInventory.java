package net.pzdcrp.wildland.world.elements.inventory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.pzdcrp.wildland.world.elements.entities.Entity;

public class PlayerInventory extends IInventory {
	private Map<Integer,Item> items = new ConcurrentHashMap<Integer,Item>();
	private int chs = 0;//0-9
	
	public PlayerInventory(Entity owner) {
		super(owner);
		for (int i = 0; i < 40; i++) {
			items.put(i, new Item(this));
		}
	}
	
	@Override
    public void addItem(Item item, int index) {
        if (items.containsKey(index)) {
        	items.replace(index, item);
        } else {
        	items.put(index, item);
        }
    }
    
    @Override
    public Item getSlot(int index) {
    	return items.get(index);
    }
    
    @Override
    public int currentHitboxSlot() {
    	return chs;
    }
}
