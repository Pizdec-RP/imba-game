package net.pzdcrp.Aselia.multiplayer.packets.server.inventory;

import de.datasecs.hydra.shared.protocol.packets.Packet;
import de.datasecs.hydra.shared.protocol.packets.PacketId;
import io.netty.buffer.ByteBuf;

@PacketId(25)
public class ServerCloseInventoryPacket extends Packet {
	public ServerCloseInventoryPacket() {

	}

	@Override
	public void read(ByteBuf byteBuf) {

	}

	@Override
	public void write(ByteBuf byteBuf) {

	}

}
