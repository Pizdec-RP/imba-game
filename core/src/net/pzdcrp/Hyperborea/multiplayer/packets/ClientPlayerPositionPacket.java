package net.pzdcrp.Hyperborea.multiplayer.packets;

import de.datasecs.hydra.shared.protocol.packets.Packet;
import de.datasecs.hydra.shared.protocol.packets.PacketId;
import io.netty.buffer.ByteBuf;
import net.pzdcrp.Hyperborea.data.Vector3D;

@PacketId(5)
public class ClientPlayerPositionPacket extends Packet {
	
	public double x,y,z;
	public boolean onGround;

	public ClientPlayerPositionPacket() {
		
	}
	
	public ClientPlayerPositionPacket(double x, double y, double z, boolean onGround) {
		this.x=x;
		this.y=y;
		this.z=z;
	}

	public ClientPlayerPositionPacket(Vector3D pos, boolean onGround) {
		this.x=pos.x;
		this.y=pos.y;
		this.z=pos.z;
	}

	@Override
	public void read(ByteBuf byteBuf) {
		x = byteBuf.readDouble();
		y = byteBuf.readDouble();
		z = byteBuf.readDouble();
		onGround = byteBuf.readBoolean();
	}

	@Override
	public void write(ByteBuf byteBuf) {
		byteBuf.writeDouble(x);
		byteBuf.writeDouble(y);
		byteBuf.writeDouble(z);
		byteBuf.writeBoolean(onGround);
	}

}
