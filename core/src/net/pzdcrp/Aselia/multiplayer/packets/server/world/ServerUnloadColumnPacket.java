package net.pzdcrp.Aselia.multiplayer.packets.server.world;

import de.datasecs.hydra.shared.protocol.packets.Packet;
import de.datasecs.hydra.shared.protocol.packets.PacketId;
import io.netty.buffer.ByteBuf;
import net.pzdcrp.Aselia.data.Vector2I;

@PacketId(7)
public class ServerUnloadColumnPacket extends Packet {
	public Vector2I pos;
	public ServerUnloadColumnPacket() {
		
	}
	
	public ServerUnloadColumnPacket(Vector2I pos) {
		this.pos=pos;
	}

	@Override
	public void read(ByteBuf byteBuf) {
		pos = new Vector2I(byteBuf.readInt(), byteBuf.readInt());
	}

	@Override
	public void write(ByteBuf byteBuf) {
		byteBuf.writeInt(pos.x);
		byteBuf.writeInt(pos.z);
	}

}
