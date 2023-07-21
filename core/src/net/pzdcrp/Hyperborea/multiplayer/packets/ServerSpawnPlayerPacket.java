package net.pzdcrp.Hyperborea.multiplayer.packets;

import de.datasecs.hydra.shared.protocol.packets.Packet;
import de.datasecs.hydra.shared.protocol.packets.PacketId;
import io.netty.buffer.ByteBuf;
import net.pzdcrp.Hyperborea.data.Vector3D;

@PacketId(4)
public class ServerSpawnPlayerPacket extends Packet {
	
	public double x,y,z;

	public ServerSpawnPlayerPacket() {
		
	}
	
	public ServerSpawnPlayerPacket(double x, double y, double z) {
		this.x=x;
		this.y=y;
		this.z=z;
	}
	
	public ServerSpawnPlayerPacket(Vector3D pos) {
		this.x=pos.x;
		this.y=pos.y;
		this.z=pos.z;
	}

	@Override
	public void read(ByteBuf byteBuf) {
		x = byteBuf.readDouble();
		y = byteBuf.readDouble();
		z = byteBuf.readDouble();
	}

	@Override
	public void write(ByteBuf byteBuf) {
		byteBuf.writeDouble(x);
		byteBuf.writeDouble(y);
		byteBuf.writeDouble(z);
	}

}
