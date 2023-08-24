package net.pzdcrp.Hyperborea.multiplayer.packets.client.inventory;

import de.datasecs.hydra.shared.protocol.packets.Packet;
import de.datasecs.hydra.shared.protocol.packets.PacketId;
import io.netty.buffer.ByteBuf;

@PacketId(24)
public class ClientPlayerInventoryActionPacket extends Packet {
	public boolean open;
	
	public ClientPlayerInventoryActionPacket() {}
	
	public ClientPlayerInventoryActionPacket(boolean open) {
		this.open=open;
	}

	@Override
	public void read(ByteBuf byteBuf) {
		open = byteBuf.readBoolean();
	}

	@Override
	public void write(ByteBuf byteBuf) {
		byteBuf.writeBoolean(open);
	}
	
}
