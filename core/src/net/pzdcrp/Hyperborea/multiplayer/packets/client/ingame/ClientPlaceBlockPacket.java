package net.pzdcrp.Hyperborea.multiplayer.packets.client.ingame;

import de.datasecs.hydra.shared.protocol.packets.Packet;
import de.datasecs.hydra.shared.protocol.packets.PacketId;
import io.netty.buffer.ByteBuf;
import net.pzdcrp.Hyperborea.data.BlockFace;
import net.pzdcrp.Hyperborea.data.Vector3D;

@PacketId(11)
public class ClientPlaceBlockPacket extends Packet {
	public Vector3D pos;
	public BlockFace face;
	
	public ClientPlaceBlockPacket() {}
	
	public ClientPlaceBlockPacket(Vector3D pos, BlockFace face) {
		this.pos=pos;
		this.face=face;
	}
	
	@Override
	public void read(ByteBuf byteBuf) {
		pos = new Vector3D(byteBuf);
		face = BlockFace.fromByte(byteBuf.readByte());
	}

	@Override
	public void write(ByteBuf byteBuf) {
		pos.writeBuffer(byteBuf);
		byteBuf.writeByte(BlockFace.toByte(face));
	}

}
