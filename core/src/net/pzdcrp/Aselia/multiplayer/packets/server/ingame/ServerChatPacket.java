package net.pzdcrp.Aselia.multiplayer.packets.server.ingame;

import java.util.UUID;

import de.datasecs.hydra.shared.protocol.packets.Packet;
import de.datasecs.hydra.shared.protocol.packets.PacketId;
import io.netty.buffer.ByteBuf;

@PacketId(2)
public class ServerChatPacket extends Packet {

	private String msg;
	
	public ServerChatPacket() {
		
	}

	public ServerChatPacket(String message) {
		this.msg = message;
	}
	
	public String getmsg() {
		return msg;
	}

	@Override
	public void read(ByteBuf buffer) {
		msg = this.readString(buffer);
	}

	@Override
	public void write(ByteBuf buffer) {
		writeString(buffer, msg);
	}
	
}
