package net.pzdcrp.Aselia.multiplayer.packets.server.ingame;

import de.datasecs.hydra.shared.protocol.packets.Packet;
import de.datasecs.hydra.shared.protocol.packets.PacketId;
import io.netty.buffer.ByteBuf;
import net.pzdcrp.Aselia.data.Vector3D;

@PacketId(23)
public class ServerPlayerRespawnPacket extends Packet {
	public Vector3D pos;
	
	public ServerPlayerRespawnPacket() {}
	
	public ServerPlayerRespawnPacket(Vector3D pos) {
		this.pos=pos;
	}

	@Override
	public void read(ByteBuf byteBuf) {
		pos = new Vector3D(byteBuf);
	}

	@Override
	public void write(ByteBuf byteBuf) {
		pos.writeBuffer(byteBuf);
	}
}
