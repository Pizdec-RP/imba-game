package net.pzdcrp.Aselia.world.elements.storages;

import java.util.List;
import java.util.Map;

import net.pzdcrp.Aselia.player.Player;
import net.pzdcrp.Aselia.utils.GameU;
import net.pzdcrp.Aselia.world.elements.inventory.PlayerInventory;
import net.pzdcrp.Aselia.world.elements.inventory.items.Item;

public class FurnaceInterface extends ItemStorage {

	/**on server*/
	public FurnaceInterface() {//for first create only
		this.onserver = true;
		for (int i = 60; i < 60+slotcount(); i++) {
			items.put(i, PlayerInventory.EMPTY);
		}
	}

	public FurnaceInterface(boolean onserver) {
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
	public void serverTick() {
		
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

	@Override
	public void render() {
		super.render();
	}

	@Override
	public int slotcount() {
		return 3;
	}

	@Override
	public ItemStorage sclone() {
		return new FurnaceInterface();
	}
}
