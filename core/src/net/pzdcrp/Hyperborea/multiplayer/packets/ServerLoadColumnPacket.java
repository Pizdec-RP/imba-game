package net.pzdcrp.Hyperborea.multiplayer.packets;

import de.datasecs.hydra.shared.protocol.packets.Packet;
import de.datasecs.hydra.shared.protocol.packets.PacketId;
import io.netty.buffer.ByteBuf;
import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.world.PlayerWorld;
import net.pzdcrp.Hyperborea.world.World;
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
		
		c = new Column(x,z,false, Hpb.world); //пакет получает клиент, поэтому используется клиентский мир
		Object[] blocks = this.readArray(byteBuf);
		int i = 0;
		for (int px = 0; px < 16; px++) {
			for (int py = 0; py < PlayerWorld.maxheight; py++) {
				for (int pz = 0; pz < 16; pz++) {
	            	c.fastSetBlock(px, py, pz, (int) blocks[i]);
	            	i++;
	            }
	        }
	    }
		
		/*Object[] lights = this.readArray(byteBuf);
		i = 0;
		for (int px = 0; px < 16; px++) {
			for (int py = 0; py < PlayerWorld.maxheight; py++) {
				for (int pz = 0; pz < 16; pz++) {
	            	c.setInternalLight(px, py, pz, (int) lights[i]);
	            	i++;
	            }
	        }
	    }*/
	}

	@Override
	public void write(ByteBuf byteBuf) {
		byteBuf.writeInt(c.pos.x);
		byteBuf.writeInt(c.pos.z);
		
		Object[] blocks = new Object[World.maxheight*16*16];
		int i = 0;
		for (int px = 0; px < 16; px++) {
	        for (int py = 0; py < PlayerWorld.maxheight; py++) {
	            for (int pz = 0; pz < 16; pz++) {
	            	blocks[i++] = c.getBlocki(px, py, pz);
	            }
	        }
	    }
		this.writeArray(byteBuf, blocks);
		
		/*Object[] lights = new Object[World.maxheight*16*16];
		i = 0;
		for (int px = 0; px < 16; px++) {
	        for (int py = 0; py < PlayerWorld.maxheight; py++) {
	            for (int pz = 0; pz < 16; pz++) {
	            	lights[i++] = c.getInternalLight(px, py, pz);
	            }
	        }
	    }
		this.writeArray(byteBuf, lights);*/
	}

}
