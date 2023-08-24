package net.pzdcrp.Hyperborea.multiplayer.packets.server;

import java.util.UUID;

import de.datasecs.hydra.shared.protocol.packets.Packet;
import de.datasecs.hydra.shared.protocol.packets.PacketId;
import io.netty.buffer.ByteBuf;

@PacketId(0)
public class ServerSuccessConnectPacket extends Packet {

	private UUID id;
	
	public ServerSuccessConnectPacket() {
		
	}

	public ServerSuccessConnectPacket(UUID id) {
		this.id=id;
	}
	
	public UUID getid() {
		return id;
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
