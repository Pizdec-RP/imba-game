package net.pzdcrp.Aselia.multiplayer.packets.client;

import java.util.UUID;

import de.datasecs.hydra.shared.protocol.packets.Packet;
import de.datasecs.hydra.shared.protocol.packets.PacketId;
import io.netty.buffer.ByteBuf;

@PacketId(3)
public class ClientWorldSuccLoadPacket extends Packet {
	public UUID id;

	public ClientWorldSuccLoadPacket() {

	}

	public ClientWorldSuccLoadPacket(UUID id) {
		this.id=id;
	}

	@Override
	public void read(ByteBuf buffer) {
		id = UUID.fromString(this.readString(buffer));
	}

	@Override
	public void write(ByteBuf buffer) {
		writeString(buffer, id.toString());
	}
}

