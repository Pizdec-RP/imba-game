package net.pzdcrp.Hyperborea.multiplayer.packets;

import de.datasecs.hydra.shared.protocol.packets.Packet;
import de.datasecs.hydra.shared.protocol.packets.PacketId;
import io.netty.buffer.ByteBuf;

@PacketId(5)
public class ClientPlayerPositionPacket extends Packet {
	
	public double x,y,z;

	public ClientPlayerPositionPacket() {
		
	}
	
	public ClientPlayerPositionPacket(double x, double y, double z) {
		this.x=x;
		this.y=y;
		this.z=z;
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
