package net.pzdcrp.Hyperborea.multiplayer.packets;

import de.datasecs.hydra.shared.protocol.packets.Packet;
import de.datasecs.hydra.shared.protocol.packets.PacketId;
import io.netty.buffer.ByteBuf;
@PacketId(16)
public class ServerSetHealthPacket extends Packet {
	public byte hp;
	
	public ServerSetHealthPacket() {}
	
	public ServerSetHealthPacket(byte hp) {
		this.hp=hp;
	}
	
	@Override
	public void read(ByteBuf byteBuf) {
		hp=byteBuf.readByte();
	}

	@Override
	public void write(ByteBuf byteBuf) {
		byteBuf.writeByte(hp);
	}

}
