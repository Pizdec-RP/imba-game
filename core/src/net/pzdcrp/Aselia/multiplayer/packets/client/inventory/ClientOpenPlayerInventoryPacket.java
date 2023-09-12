package net.pzdcrp.Aselia.multiplayer.packets.client.inventory;

import de.datasecs.hydra.shared.protocol.packets.Packet;
import de.datasecs.hydra.shared.protocol.packets.PacketId;
import io.netty.buffer.ByteBuf;

@PacketId(24)
public class ClientOpenPlayerInventoryPacket extends Packet {
	
	public ClientOpenPlayerInventoryPacket() {}

	@Override
	public void read(ByteBuf byteBuf) {
	}

	@Override
	public void write(ByteBuf byteBuf) {
	}
	
}
