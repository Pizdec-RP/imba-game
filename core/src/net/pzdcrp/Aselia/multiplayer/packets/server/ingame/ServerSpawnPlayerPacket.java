package net.pzdcrp.Aselia.multiplayer.packets.server.ingame;

import de.datasecs.hydra.shared.protocol.packets.Packet;
import de.datasecs.hydra.shared.protocol.packets.PacketId;
import io.netty.buffer.ByteBuf;
import net.pzdcrp.Aselia.data.Vector3D;

@PacketId(4)
public class ServerSpawnPlayerPacket extends Packet {
	
	public double x,y,z;
	public int lid;

	public ServerSpawnPlayerPacket() {
		
	}
	
	public ServerSpawnPlayerPacket(double x, double y, double z, int lid) {
		this.x=x;
		this.y=y;
		this.z=z;
		this.lid = lid;
	}
	
	public ServerSpawnPlayerPacket(Vector3D pos, int lid) {
		this.x=pos.x;
		this.y=pos.y;
		this.z=pos.z;
		this.lid = lid;
	}

	@Override
	public void read(ByteBuf byteBuf) {
		x = byteBuf.readDouble();
		y = byteBuf.readDouble();
		z = byteBuf.readDouble();
		lid = byteBuf.readInt();
	}

	@Override
	public void write(ByteBuf byteBuf) {
		byteBuf.writeDouble(x);
		byteBuf.writeDouble(y);
		byteBuf.writeDouble(z);
		byteBuf.writeInt(lid);
	}

}
