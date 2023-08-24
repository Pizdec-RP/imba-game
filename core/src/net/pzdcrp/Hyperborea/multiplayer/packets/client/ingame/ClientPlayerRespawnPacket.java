package net.pzdcrp.Hyperborea.multiplayer.packets.client.ingame;

import de.datasecs.hydra.shared.protocol.packets.Packet;
import de.datasecs.hydra.shared.protocol.packets.PacketId;
import io.netty.buffer.ByteBuf;

@PacketId(22)
public class ClientPlayerRespawnPacket extends Packet {
	//its empty
	@Override
	public void read(ByteBuf byteBuf) {

	}

	@Override
	public void write(ByteBuf byteBuf) {

	}

}
