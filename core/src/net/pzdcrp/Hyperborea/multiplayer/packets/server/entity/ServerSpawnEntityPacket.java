package net.pzdcrp.Hyperborea.multiplayer.packets.server.entity;

import de.datasecs.hydra.shared.protocol.packets.Packet;
import de.datasecs.hydra.shared.protocol.packets.PacketId;
import io.netty.buffer.ByteBuf;
import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.data.objects.ObjectData;
import net.pzdcrp.Hyperborea.data.objects.entityObjectData.ItemEntityData;
import net.pzdcrp.Hyperborea.utils.GameU;
import net.pzdcrp.Hyperborea.world.elements.entities.Entity;

@PacketId(12)
public class ServerSpawnEntityPacket extends Packet {
	
	public Entity entity;
	
	public ServerSpawnEntityPacket() {
		
	}

	public ServerSpawnEntityPacket(Entity entity) {
		this.entity = entity;
	}

	@Override
	public void read(ByteBuf byteBuf) {//TODO сделать передачу скорости
		int id = byteBuf.readInt();
		Entity abstractEntity = Entity.entities.get(id);
		Vector3D pos = new Vector3D(byteBuf);
		abstractEntity.vel = new Vector3D(byteBuf);
		ObjectData data = null;
		if (id == 2) {//item entity
			data = new ItemEntityData();
		}// default entity, no slots entity 
		else {
			GameU.end("unregistered objectdata");
		}
		data.fromBuff(byteBuf);
		int lid = byteBuf.readInt();
		this.entity = abstractEntity.clone(pos, Hpb.world, data, lid);
	}

	@Override
	public void write(ByteBuf byteBuf) {
		byteBuf.writeInt(entity.getType());
		entity.pos.writeBuffer(byteBuf);
		entity.vel.writeBuffer(byteBuf);
		entity.consumeData().toBuff(byteBuf);
		byteBuf.writeInt(entity.localId);
	}

}
