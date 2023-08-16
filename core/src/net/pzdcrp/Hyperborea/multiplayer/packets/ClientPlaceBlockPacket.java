package net.pzdcrp.Hyperborea.multiplayer.packets;

import de.datasecs.hydra.shared.protocol.packets.Packet;
import de.datasecs.hydra.shared.protocol.packets.PacketId;
import io.netty.buffer.ByteBuf;
import net.pzdcrp.Hyperborea.data.Vector3D;

@PacketId(11)
public class ClientPlaceBlockPacket extends Packet {
	public Vector3D pos;
	
	@Override
	public void read(ByteBuf byteBuf) {
		pos = new Vector3D(byteBuf);
	}

	@Override
	public void write(ByteBuf byteBuf) {
		pos.writeBuffer(byteBuf);
	}

}
