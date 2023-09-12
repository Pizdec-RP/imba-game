package net.pzdcrp.Aselia.multiplayer.packets.client.ingame;

import de.datasecs.hydra.shared.protocol.packets.Packet;
import de.datasecs.hydra.shared.protocol.packets.PacketId;
import io.netty.buffer.ByteBuf;

@PacketId(21)
public class ClientSetHotbarSlotPacket extends Packet {
	public byte slot;
	
	public ClientSetHotbarSlotPacket() {}
	
	public ClientSetHotbarSlotPacket(byte slot) {
		this.slot = slot;
	}
	
	@Override
	public void read(ByteBuf byteBuf) {
		slot = byteBuf.readByte();
	}

	@Override
	public void write(ByteBuf byteBuf) {
		byteBuf.writeByte(slot);
	}
	
}
