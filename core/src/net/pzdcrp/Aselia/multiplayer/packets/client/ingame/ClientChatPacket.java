package net.pzdcrp.Aselia.multiplayer.packets.client.ingame;

import de.datasecs.hydra.shared.protocol.packets.Packet;
import de.datasecs.hydra.shared.protocol.packets.PacketId;
import io.netty.buffer.ByteBuf;
import net.pzdcrp.Aselia.utils.GameU;

@PacketId(14)
public class ClientChatPacket extends Packet {
	public String msg;
	
	public ClientChatPacket() {
		
	}

	public ClientChatPacket(String message) {
		this.msg = message;
	}

	@Override
	public void read(ByteBuf buffer) {
		msg = this.readString(buffer);
		//GameU.log("r "+msg);
	}

	@Override
	public void write(ByteBuf buffer) {
		writeString(buffer, msg);
		//GameU.log("w "+msg);
	}
}