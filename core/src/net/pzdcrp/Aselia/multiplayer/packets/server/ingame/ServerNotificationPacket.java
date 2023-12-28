package net.pzdcrp.Aselia.multiplayer.packets.server.ingame;

import de.datasecs.hydra.shared.protocol.packets.Packet;
import de.datasecs.hydra.shared.protocol.packets.PacketId;
import io.netty.buffer.ByteBuf;
import net.pzdcrp.Aselia.utils.GameU;

@PacketId(30)
public class ServerNotificationPacket extends Packet {
	public String text;
	public ServerNotificationPacket() {
		// TODO Auto-generated constructor stub
	}
	
	public ServerNotificationPacket(String s) {
		text=s;
	}

	@Override
	public void read(ByteBuf byteBuf) {
		text = this.readString(byteBuf);
	}

	@Override
	public void write(ByteBuf byteBuf) {
		writeString(byteBuf, text);
	}
}
