package net.pzdcrp.Hyperborea.multiplayer.packets;

import de.datasecs.hydra.shared.protocol.packets.Packet;
import de.datasecs.hydra.shared.protocol.packets.PacketId;
import io.netty.buffer.ByteBuf;
import net.pzdcrp.Hyperborea.data.Vector3D;

@PacketId(13)
public class ServerEntityPositionVelocityPacket extends Packet {
	
	public Vector3D pos, vel;
	public int id;
	
	public ServerEntityPositionVelocityPacket() {
		
	}
	
	public ServerEntityPositionVelocityPacket(Vector3D pos, Vector3D vel, int id) {
		this.pos = pos;
		this.vel = vel;
		this.id = id;
	}

	@Override
	public void read(ByteBuf byteBuf) {
		pos = new Vector3D(byteBuf);
		vel = new Vector3D(byteBuf);
		id = byteBuf.readInt();
	}

	@Override
	public void write(ByteBuf byteBuf) {
		pos.writeBuffer(byteBuf);
		vel.writeBuffer(byteBuf);
		byteBuf.writeInt(id);
	}

}
