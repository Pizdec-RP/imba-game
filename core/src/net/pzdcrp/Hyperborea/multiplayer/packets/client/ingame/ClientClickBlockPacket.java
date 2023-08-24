package net.pzdcrp.Hyperborea.multiplayer.packets.client.ingame;

import de.datasecs.hydra.shared.protocol.packets.Packet;
import de.datasecs.hydra.shared.protocol.packets.PacketId;
import io.netty.buffer.ByteBuf;
import net.pzdcrp.Hyperborea.data.Vector3D;

@PacketId(28)
public class ClientClickBlockPacket extends Packet {
	public Vector3D pos;
	public ClientClickBlockPacket() {
		
	}

	public ClientClickBlockPacket(Vector3D pos) {
		this.pos = pos;
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
