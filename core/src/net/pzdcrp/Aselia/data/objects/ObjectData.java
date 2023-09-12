package net.pzdcrp.Aselia.data.objects;

import io.netty.buffer.ByteBuf;

public interface ObjectData {
	void fromBuff(ByteBuf buff);
	void toBuff(ByteBuf buff);
}
