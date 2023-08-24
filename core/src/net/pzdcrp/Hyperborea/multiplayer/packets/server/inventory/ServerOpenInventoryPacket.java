package net.pzdcrp.Hyperborea.multiplayer.packets.server.inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.datasecs.hydra.shared.protocol.packets.Packet;
import de.datasecs.hydra.shared.protocol.packets.PacketId;
import io.netty.buffer.ByteBuf;
import net.pzdcrp.Hyperborea.world.elements.inventory.items.Item;

@PacketId(26)
public class ServerOpenInventoryPacket extends Packet {
	public byte id;
	public Map<Integer, Item> items;
	public ServerOpenInventoryPacket() {}
	
	public ServerOpenInventoryPacket(byte id, Map<Integer, Item> items) {
		this.id = id;
		this.items = items;
	}

	@Override
	public void read(ByteBuf byteBuf) {
		id = byteBuf.readByte();
		
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
		byteBuf.writeByte(id);
		
		byteBuf.writeByte((byte) items.size());
		for (Entry<Integer, Item> item : items.entrySet()) {
			byteBuf.writeByte(item.getKey().byteValue());
			byteBuf.writeInt(item.getValue().id);
			byteBuf.writeInt(item.getValue().count);
		}
	}

}
