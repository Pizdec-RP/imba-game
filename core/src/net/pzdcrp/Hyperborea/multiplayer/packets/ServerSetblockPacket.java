package net.pzdcrp.Hyperborea.multiplayer.packets;

import de.datasecs.hydra.shared.protocol.packets.Packet;
import de.datasecs.hydra.shared.protocol.packets.PacketId;
import io.netty.buffer.ByteBuf;
import net.pzdcrp.Hyperborea.data.ActionAuthor;
import net.pzdcrp.Hyperborea.data.Vector3D;

@PacketId(10)
public class ServerSetblockPacket extends Packet {
	public Vector3D pos;
	public int id;
	public ActionAuthor author;
	
	public ServerSetblockPacket() {
		
	}
	
	public ServerSetblockPacket(Vector3D pos, int id, ActionAuthor author) {
		this.pos = pos;
		this.id=id;
		this.author=author;
	}

	@Override
	public void read(ByteBuf byteBuf) {
		pos = new Vector3D(byteBuf);
		id = byteBuf.readInt();
		author = ActionAuthor.fromByte(byteBuf.readByte());
	}

	@Override
	public void write(ByteBuf byteBuf) {
		pos.writeBuffer(byteBuf);
		byteBuf.writeInt(id);
		byteBuf.writeByte(ActionAuthor.toByte(author));
	}

}
