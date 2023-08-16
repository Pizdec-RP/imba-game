package net.pzdcrp.Hyperborea.multiplayer.packets;

import de.datasecs.hydra.shared.protocol.packets.Packet;
import de.datasecs.hydra.shared.protocol.packets.PacketId;
import io.netty.buffer.ByteBuf;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.utils.GameU;

@PacketId(8)
public class ClientPlayerActionPacket extends Packet {
	public enum PlayerAction {
		StartBreakingBlock, EndBreakingBlock, ResetBreakingBlock
	}
	
	public PlayerAction action;
	public Vector3D pos;
	
	public ClientPlayerActionPacket() {
		
	}
	
	public ClientPlayerActionPacket(PlayerAction action, Vector3D pos) {
		this.action = action;
		this.pos = pos;
	}

	@Override
	public void read(ByteBuf byteBuf) {
		switch (byteBuf.readByte()) {
			case 0:
				action = PlayerAction.EndBreakingBlock;
				break;
			case 1:
				action = PlayerAction.ResetBreakingBlock;
				break;
			case 2:
				action = PlayerAction.StartBreakingBlock;
				break;
			default:
				GameU.end("unknown action");
		}
		pos = new Vector3D(byteBuf);
	}

	@Override
	public void write(ByteBuf byteBuf) {
		switch (action) {
			case EndBreakingBlock:
				byteBuf.writeByte(0);
				break;
			case ResetBreakingBlock:
				byteBuf.writeByte(1);
				break;
			case StartBreakingBlock:
				byteBuf.writeByte(2);
				break;
			default:
				GameU.end("unknown action");
		}
		pos.writeBuffer(byteBuf);
	}
}
