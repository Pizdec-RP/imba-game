package net.pzdcrp.Hyperborea.multiplayer.packets.client.inventory;

import de.datasecs.hydra.shared.protocol.packets.Packet;
import de.datasecs.hydra.shared.protocol.packets.PacketId;
import io.netty.buffer.ByteBuf;

@PacketId(27)
public class ClientCloseInventoryPacket extends Packet {

	public ClientCloseInventoryPacket() {
	}

	@Override
	public void read(ByteBuf byteBuf) {

	}

	@Override
	public void write(ByteBuf byteBuf) {

	}

}
