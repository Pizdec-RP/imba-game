package net.pzdcrp.Aselia.multiplayer.packets.client.ingame;

import de.datasecs.hydra.shared.protocol.packets.Packet;
import de.datasecs.hydra.shared.protocol.packets.PacketId;
import io.netty.buffer.ByteBuf;
import net.pzdcrp.Aselia.data.BlockFace;
import net.pzdcrp.Aselia.data.Vector3D;

@PacketId(11)
public class ClientPlaceBlockPacket extends Packet {
	public Vector3D pos, origin;
	public BlockFace face;

	public ClientPlaceBlockPacket() {}

	public ClientPlaceBlockPacket(Vector3D pos, BlockFace face, Vector3D currentaimpoint) {
		this.pos=pos;
		this.origin = currentaimpoint;
		this.face=face;
	}

	@Override
	public void read(ByteBuf byteBuf) {
		pos = new Vector3D(byteBuf);
		face = BlockFace.fromByte(byteBuf.readByte());
		origin = new Vector3D(byteBuf);
	}

	@Override
	public void write(ByteBuf byteBuf) {
		pos.writeBuffer(byteBuf);
		byteBuf.writeByte(BlockFace.toByte(face));
		origin.writeBuffer(byteBuf);
	}

}
