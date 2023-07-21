package net.pzdcrp.Hyperborea.multiplayer.packets;

import java.io.ByteArrayInputStream;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.datasecs.hydra.shared.protocol.packets.Packet;
import de.datasecs.hydra.shared.protocol.packets.PacketId;
import io.netty.buffer.ByteBuf;
import net.pzdcrp.Hyperborea.world.PlayerWorld;
import net.pzdcrp.Hyperborea.world.World;
import net.pzdcrp.Hyperborea.world.elements.Chunk;
import net.pzdcrp.Hyperborea.world.elements.Column;

@PacketId(6)
public class ServerLoadColumnPacket extends Packet {
	
	public Column c;
	
	public ServerLoadColumnPacket() {
		
	}
	
	public ServerLoadColumnPacket(Column c) {
		this.c=c;
	}

	@Override
	public void read(ByteBuf byteBuf) {
		int x = byteBuf.readInt();
		int z = byteBuf.readInt();
		Integer[] blocks = new Integer[World.maxheight*16*16];
		int i = 0;
		for (int px = 0; px < 16; px++) {
	        for (int py = 0; py < PlayerWorld.maxheight; py++) {
	            for (int pz = 0; pz < 16; pz++) {
	            	blocks[i++] = c.getBlocki(px, py, pz);
	            }
	        }
	    }
		byteBuf.writeInt(x);
		byteBuf.writeInt(z);
		this.writeArray(byteBuf, (Object[])blocks);
	}

	@Override
	public void write(ByteBuf byteBuf) {
		c = new Column(byteBuf.readInt(),byteBuf.readInt(),false);
		Object[] blocks = this.readArray(byteBuf);
		int i = 0;
		for (int px = 0; px < 16; px++) {
			for (int py = 0; py < PlayerWorld.maxheight; py++) {
				for (int pz = 0; pz < 16; pz++) {
	            	c.fastSetBlock(px, py, pz, (Integer) blocks[i]);
	            	i++;
	            }
	        }
	    }
	}

}
