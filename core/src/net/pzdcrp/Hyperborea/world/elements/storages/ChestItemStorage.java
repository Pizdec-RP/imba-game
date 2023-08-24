package net.pzdcrp.Hyperborea.world.elements.storages;

import java.util.List;
import java.util.Map;
import net.pzdcrp.Hyperborea.player.Player;
import net.pzdcrp.Hyperborea.utils.GameU;
import net.pzdcrp.Hyperborea.world.elements.inventory.PlayerInventory;
import net.pzdcrp.Hyperborea.world.elements.inventory.items.Item;

public class ChestItemStorage extends ItemStorage {
	//start slot id = 60 all slots: 60 - 89 (30 slots)
	//item grid width-9 height-3
	
	/**on server*/
	public ChestItemStorage() {//for first create only
		this.onserver = true;
		for (int i = 60; i < 60+slotcount(); i++) {
			items.put(i, PlayerInventory.EMPTY);
		}
	}
	
	public ChestItemStorage(boolean onserver) {
		this.onserver = onserver;
	}
	
	/**server side*/
	@Override
	public void open(Player opener) {
		super.open(opener);
	}
	
	/**server side*/
	@Override
	public void close(Player p) {
		super.close(p);
	}
	
	@Override
	public Item getslot(int index) {
		return super.getslot(index);
	}
	
	@Override
	public void setItems(Map<Integer, Item> items) {
		super.setItems(items);
	}
	
	/**client side*/
	@Override
	public List<float[]> getSlotmap() {
		return super.getSlotmap();
	}
	
    /**client side*/
	@Override
    public void reloadBounds() {
    	super.reloadBounds();
    	GameU.log("chest item storage bounds reloaded");
    }
	
	@Override
	public void setFromPacket(int index, Item item) {
		super.setFromPacket(index, item);
	}
	
	@Override
	public void setSlotSilentOnServer(int index, Item item, String playername) {
		super.setSlotSilentOnServer(index, item, playername);
	}
	
	@Override
	public void render() {
		super.render();
	}
	
	@Override
	public int slotcount() {
		return 20;
	}
	
	@Override
	public ItemStorage sclone() {
		return new ChestItemStorage();
	}
}
