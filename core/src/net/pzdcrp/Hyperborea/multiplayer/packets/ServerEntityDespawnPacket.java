package net.pzdcrp.Hyperborea.multiplayer.packets;

import de.datasecs.hydra.shared.protocol.packets.Packet;
import de.datasecs.hydra.shared.protocol.packets.PacketId;
import io.netty.buffer.ByteBuf;
@PacketId(15)
public class ServerEntityDespawnPacket extends Packet {
	public int lid;
	public ServerEntityDespawnPacket() {
		
	}
	
	public ServerEntityDespawnPacket(int lid) {
		this.lid=lid;
	}

	@Override
	public void read(ByteBuf byteBuf) {
		lid=byteBuf.readInt();
	}

	@Override
	public void write(ByteBuf byteBuf) {
		byteBuf.writeInt(lid);
	}

}
