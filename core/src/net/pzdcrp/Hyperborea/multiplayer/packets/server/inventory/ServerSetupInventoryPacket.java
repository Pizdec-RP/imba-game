package net.pzdcrp.Hyperborea.multiplayer.packets.server.inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.datasecs.hydra.shared.protocol.packets.Packet;
import de.datasecs.hydra.shared.protocol.packets.PacketId;
import io.netty.buffer.ByteBuf;
import net.pzdcrp.Hyperborea.world.elements.inventory.items.Item;

@PacketId(17)
public class ServerSetupInventoryPacket extends Packet {
	
	public Map<Integer,Item> items;
	
	public ServerSetupInventoryPacket() {}
	
	public ServerSetupInventoryPacket(Map<Integer,Item> items) {
		this.items = items;
	}

	@Override
	public void read(ByteBuf byteBuf) {
		byte length = byteBuf.readByte();
		items = new HashMap<>();
		for (byte i = 0; i < length; i++) {
			int slot = byteBuf.readByte();
			int id = byteBuf.readInt();
			Item item = Item.items.get(id);
			item.count = byteBuf.readInt();
			items.put(slot, item);
		}
	}

	@Override
	public void write(ByteBuf byteBuf) {
		byteBuf.writeByte((byte) items.size());
		
		for (Entry<Integer, Item> item : items.entrySet()) {
			byteBuf.writeByte(item.getKey().byteValue());
			byteBuf.writeInt(item.getValue().id);
			byteBuf.writeInt(item.getValue().count);
		}
	}
	
}
