package net.pzdcrp.Hyperborea.multiplayer.packets;

import de.datasecs.hydra.shared.protocol.packets.Packet;
import de.datasecs.hydra.shared.protocol.packets.PacketId;
import io.netty.buffer.ByteBuf;

@PacketId(1)
public class ClientPlayerConnectionPacket extends Packet {
	
	public String name;
	public int renderDistance;
	
	public ClientPlayerConnectionPacket() {
		
	}
	
	public ClientPlayerConnectionPacket(String name, int rd) {
		this.name = name;
		this.renderDistance = rd;
	}

	@Override
	public void read(ByteBuf buffer) {
		name = readString(buffer);
		renderDistance = readInt(buffer);
	}

	@Override
	public void write(ByteBuf buffer) {
		writeString(buffer, name);
		writeInt(buffer, renderDistance);
	}
	
}
