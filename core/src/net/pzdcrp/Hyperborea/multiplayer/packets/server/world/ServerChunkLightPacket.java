package net.pzdcrp.Hyperborea.multiplayer.packets.server.world;

import de.datasecs.hydra.shared.protocol.packets.Packet;
import de.datasecs.hydra.shared.protocol.packets.PacketId;
import io.netty.buffer.ByteBuf;
import net.pzdcrp.Hyperborea.data.BitStorage;
import net.pzdcrp.Hyperborea.data.Vector3I;
import net.pzdcrp.Hyperborea.utils.GameU;
import net.pzdcrp.Hyperborea.world.elements.Chunk;

@PacketId(9)
public class ServerChunkLightPacket extends Packet {
	public BitStorage light;
	public Vector3I chunkPos;
	
	public ServerChunkLightPacket() {}
	
	public ServerChunkLightPacket(BitStorage light, Vector3I pos) {
		this.light = light;
		this.chunkPos = pos;
	}

	@Override
	public void read(ByteBuf byteBuf) {
		chunkPos = new Vector3I(byteBuf.readInt(),byteBuf.readInt(),byteBuf.readInt());
		light = new BitStorage(4, 4096);
		Object[] lights = this.readArray(byteBuf);
		int i = 0;
		for (int px = 0; px < 16; px++) {
			for (int py = 0; py < 16; py++) {
				for (int pz = 0; pz < 16; pz++) {
					light.set(Chunk.index(px, py, pz), (int) lights[i]);
	            	i++;
	            }
	        }
	    }
		
	}

	@Override
	public void write(ByteBuf byteBuf) {
		byteBuf.writeInt(chunkPos.x);
		byteBuf.writeInt(chunkPos.y);
		byteBuf.writeInt(chunkPos.z);
		Object[] lights = new Object[16*16*16];
		int i = 0;
		for (int px = 0; px < 16; px++) {
	        for (int py = 0; py < 16; py++) {
	            for (int pz = 0; pz < 16; pz++) {
	            	lights[i++] = light.get(Chunk.index(px, py, pz));
	            }
	        }
	    }
		this.writeArray(byteBuf, lights);
	}

}
