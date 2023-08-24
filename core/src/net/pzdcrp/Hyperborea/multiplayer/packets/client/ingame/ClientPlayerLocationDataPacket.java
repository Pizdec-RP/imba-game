package net.pzdcrp.Hyperborea.multiplayer.packets.client.ingame;

import de.datasecs.hydra.shared.protocol.packets.Packet;
import de.datasecs.hydra.shared.protocol.packets.PacketId;
import io.netty.buffer.ByteBuf;
import net.pzdcrp.Hyperborea.data.Vector3D;

@PacketId(5)
public class ClientPlayerLocationDataPacket extends Packet {
	
	public Vector3D pos,vel;
	public boolean onGround;
	public float yaw, pitch;

	public ClientPlayerLocationDataPacket() {
		
	}

	public ClientPlayerLocationDataPacket(Vector3D pos, Vector3D vel, boolean onGround, float yaw, float pitch) {
		this.pos=pos;
		this.vel=vel;
		this.onGround = onGround;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	@Override
	public void read(ByteBuf byteBuf) {
		pos = new Vector3D(byteBuf);
		vel = new Vector3D(byteBuf);
		onGround = byteBuf.readBoolean();
		yaw = byteBuf.readFloat();
		pitch = byteBuf.readFloat();
	}

	@Override
	public void write(ByteBuf byteBuf) {
		pos.writeBuffer(byteBuf);
		vel.writeBuffer(byteBuf);
		byteBuf.writeBoolean(onGround);
		byteBuf.writeFloat(yaw);
		byteBuf.writeFloat(pitch);
	}

}
