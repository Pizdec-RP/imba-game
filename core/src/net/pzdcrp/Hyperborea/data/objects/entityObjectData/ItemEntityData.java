package net.pzdcrp.Hyperborea.data.objects.entityObjectData;

import io.netty.buffer.ByteBuf;
import net.pzdcrp.Hyperborea.data.objects.ObjectData;
import net.pzdcrp.Hyperborea.utils.GameU;

public class ItemEntityData implements ObjectData {
	public int item, count;

	@Override
	public void fromBuff(ByteBuf buff) {
		item = buff.readInt();
		count = buff.readInt();
	}

	@Override
	public void toBuff(ByteBuf buff) {
		buff.writeInt(item);
		buff.writeInt(count);
	}
}
