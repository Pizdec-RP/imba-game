package net.pzdcrp.Aselia.multiplayer.packets.client.inventory;

import de.datasecs.hydra.shared.protocol.packets.Packet;
import de.datasecs.hydra.shared.protocol.packets.PacketId;
import io.netty.buffer.ByteBuf;

@PacketId(20)
public class ClientInventoryActionPacket extends Packet {
	
	public int clickedslot, mousebutton;
	public boolean downclick;
	
	public ClientInventoryActionPacket() {}
	
	public ClientInventoryActionPacket(int clickedslot, boolean downclick, int mousebutton) {
		this.clickedslot = clickedslot;
		this.downclick = downclick;
		this.mousebutton = mousebutton;
	}

	@Override
	public void read(ByteBuf byteBuf) {
		clickedslot = byteBuf.readByte();
		downclick = byteBuf.readBoolean();
		mousebutton = byteBuf.readInt();
	}

	@Override
	public void write(ByteBuf byteBuf) {
		byteBuf.writeByte(clickedslot);
		byteBuf.writeBoolean(downclick);
		byteBuf.writeInt(mousebutton);
	}

}
