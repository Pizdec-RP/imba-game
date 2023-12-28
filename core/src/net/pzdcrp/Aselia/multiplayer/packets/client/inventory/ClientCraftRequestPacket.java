package net.pzdcrp.Aselia.multiplayer.packets.client.inventory;

import de.datasecs.hydra.shared.protocol.packets.Packet;
import de.datasecs.hydra.shared.protocol.packets.PacketId;
import io.netty.buffer.ByteBuf;

@PacketId(29)
public class ClientCraftRequestPacket extends Packet {
	public int recid;
	public ClientCraftRequestPacket() {

	}

	public ClientCraftRequestPacket(int recid) {
		this.recid = recid;
	}

	@Override
	public void read(ByteBuf byteBuf) {
		recid = this.readInt(byteBuf);
	}

	@Override
	public void write(ByteBuf byteBuf) {
		this.writeInt(byteBuf, recid);
	}

}
