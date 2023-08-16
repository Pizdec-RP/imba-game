
package net.pzdcrp.Hyperborea.multiplayer.packets;

import de.datasecs.hydra.shared.protocol.packets.Packet;
import de.datasecs.hydra.shared.protocol.packets.PacketId;
import io.netty.buffer.ByteBuf;
import net.pzdcrp.Hyperborea.world.elements.inventory.items.Item;

@PacketId(19)
public class ServerSetSlotPacket extends Packet {
	
	public int index;
	public Item item;
	
	public ServerSetSlotPacket() {}
	
	public ServerSetSlotPacket(int index, Item item) {
		this.index = index;
		this.item = item;
	}

	@Override
	public void read(ByteBuf byteBuf) {
		index = byteBuf.readByte();
		item = Item.items.get(byteBuf.readInt()).clone(byteBuf.readInt());
	}

	@Override
	public void write(ByteBuf byteBuf) {
		byteBuf.writeByte(index);
		byteBuf.writeInt(item.id);
		byteBuf.writeInt(item.count);
	}

}
