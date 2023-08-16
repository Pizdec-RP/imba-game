package net.pzdcrp.Hyperborea.data.objects.entityObjectData;

import io.netty.buffer.ByteBuf;
import net.pzdcrp.Hyperborea.data.objects.ObjectData;
import net.pzdcrp.Hyperborea.utils.GameU;

public class ItemEntityData implements ObjectData {
	
	public int blockIdModel;

	@Override
	public void fromBuff(ByteBuf buff) {
		blockIdModel = buff.readInt();
		if (blockIdModel == 0) {
			GameU.end("zero id readed");
		}
	}

	@Override
	public void toBuff(ByteBuf buff) {
		buff.writeInt(blockIdModel);
		if (blockIdModel == 0) {
			GameU.end("zero id writed");
		}
		//GameU.tracer();
	}
	
}
