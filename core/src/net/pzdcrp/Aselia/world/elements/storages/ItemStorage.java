package net.pzdcrp.Aselia.world.elements.storages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.datasecs.hydra.shared.protocol.packets.Packet;
import net.pzdcrp.Aselia.Hpb;
import net.pzdcrp.Aselia.data.Vector3D;
import net.pzdcrp.Aselia.multiplayer.packets.server.inventory.ServerOpenInventoryPacket;
import net.pzdcrp.Aselia.multiplayer.packets.server.inventory.ServerSetSlotPacket;
import net.pzdcrp.Aselia.player.Player;
import net.pzdcrp.Aselia.server.InternalServer;
import net.pzdcrp.Aselia.utils.GameU;
import net.pzdcrp.Aselia.utils.MathU;
import net.pzdcrp.Aselia.world.elements.blocks.Block;
import net.pzdcrp.Aselia.world.elements.entities.Entity;
import net.pzdcrp.Aselia.world.elements.entities.ItemEntity;
import net.pzdcrp.Aselia.world.elements.inventory.PlayerInventory;
import net.pzdcrp.Aselia.world.elements.inventory.items.Item;
import net.pzdcrp.Aselia.world.elements.inventory.items.NoItem;

public class ItemStorage {
	public static Map<Byte, ItemStorage> storageTable = new HashMap<>() {{
		put((byte) 0, new ChestItemStorage(false));
	}};
	
	public Map<Integer, Item> items = new HashMap<>();
	protected List<Player> viewers = new CopyOnWriteArrayList<>();
	protected boolean onserver;
	private List<float[]> slotposmap = new ArrayList<>();
	
	public ItemStorage() {
		
	}

	public void setItems(Map<Integer, Item> items) {
		this.items = items;
	}

	public void render() {
		for (int i = 60; i < 60+slotcount(); i++) {
    		int insideAlignedIndex = i % 10;
    		int insideAlignedHeightIndex = i / 10;
    		float slotX = PlayerInventory.x + insideAlignedIndex * (PlayerInventory.slotWidth + PlayerInventory.spacing);
            float slotY = insideAlignedHeightIndex * (PlayerInventory.slotHeight + PlayerInventory.spacing);
            
            Hpb.spriteBatch.draw(PlayerInventory.slot, 
            		slotX, 
            		slotY, 
            		PlayerInventory.slotWidth, 
            		PlayerInventory.slotHeight);
            Hpb.world.player.castedInv.displaySlot(i, 
            		slotX + PlayerInventory.spacing, 
            		slotY + PlayerInventory.spacing, 
            		PlayerInventory.slotWidth - PlayerInventory.spacing * 2, 
            		PlayerInventory.slotHeight - PlayerInventory.spacing * 2);
    	}
	}


	public void open(Player opener) {
		viewers.add(opener);
		opener.sendSelfPacket(new ServerOpenInventoryPacket((byte) 0, items));
	}


	public void close(Player p) {
		viewers.remove(p);
	}


	public Item getslot(int index) {
		return items.get(index);
	}
	
	public int slotcount() {
		return 10;
	}

	public void reloadBounds() {
		slotposmap.clear();
    	for (int i = 60; i < 60+slotcount(); i++) {
    		int insideAlignedIndex = i % 10;
    		int insideAlignedHeightIndex = i / 10;
    		float slotX = PlayerInventory.x + insideAlignedIndex * (PlayerInventory.slotWidth + PlayerInventory.spacing);
            float slotY = insideAlignedHeightIndex * (PlayerInventory.slotHeight + PlayerInventory.spacing);
            slotposmap.add(new float[] {slotX, slotY, slotX+PlayerInventory.slotWidth, slotY+PlayerInventory.slotHeight});
    	}
	}

	/**client side*/
	public List<float[]> getSlotmap() {
		return slotposmap;
	}


	public void setFromPacket(int index, Item item) {
		items.replace(index, item);
	}


	public void setSlotSilentOnServer(int index, Item item, String playername) {
		items.replace(index, item);
		Packet packet = new ServerSetSlotPacket(index, item);
		for (Player viewer : viewers) {
			if (!viewer.nickname.equals(playername)) {
				viewer.sendSelfPacket(packet);
			}
		}
	}
	
	@Override
	@Deprecated
	public ItemStorage clone() {
		GameU.end("do not use");
		return null;
	}
	
	public ItemStorage sclone() {
		GameU.end("unsetted method clien in class "+this.getClass().getName());
		return null;
	}
	
	public String toJson() {
		String items = "";
		for (Entry<Integer, Item> item : this.items.entrySet()) {
			if (item.getValue() instanceof NoItem) continue;
			if (!items.equals("")) items += "_";
			items += item.getKey()+"-"+item.getValue().toString();
		}
		return items;
	}

	public void fromJson(String items) {
		if (!items.equals("")) {
			for (String substr : items.split("_")) {
				String[] itempack = substr.split("-");
				this.items.put(Integer.parseInt(itempack[0]),
						Item.fromString(itempack[1]));
			}
		}
	}
	
	public static byte toId(ItemStorage is) {
		for (Entry<Byte, ItemStorage> entry : storageTable.entrySet()) {
			if (entry.getValue().getClass() == is.getClass()) {
				return entry.getKey();
			}
		}
		GameU.end("unregistered storage "+is.getClass().getName());
		return -1;
	}

	public void onbreak(Vector3D pos) {
		for (Player p : viewers) {
			p.castedInv.close();
		}
		for (Entry<Integer, Item> eitem : items.entrySet()) {
			if (eitem.getValue().getId() != 0) {
				ItemEntity entity = new ItemEntity(pos, eitem.getValue(), InternalServer.world, Entity.genLocalId());
				entity.vel.y = 0.02;
				entity.vel.x = MathU.rndd(-0.1, 0.1);
				entity.vel.z = MathU.rndd(-0.1, 0.1);
				InternalServer.world.spawnEntity(entity);
				items.replace(eitem.getKey(), PlayerInventory.EMPTY);
			}
		}
	}
}
