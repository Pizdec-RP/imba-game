package net.pzdcrp.Aselia.multiplayer.packets.client.ingame;

import de.datasecs.hydra.shared.protocol.packets.Packet;
import de.datasecs.hydra.shared.protocol.packets.PacketId;
import io.netty.buffer.ByteBuf;
import net.pzdcrp.Aselia.data.Vector3D;
import net.pzdcrp.Aselia.utils.GameU;

@PacketId(8)
public class ClientPlayerActionPacket extends Packet {
	public enum PlayerAction {
		StartBreakingBlock, EndBreakingBlock, ResetBreakingBlock, DropItem, DropItemStack
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
			case 3:
				action = PlayerAction.DropItem;
				break;
			case 4:
				action = PlayerAction.DropItemStack;
				break;
			default:
				GameU.end("unknown action");
		}
		if (action == PlayerAction.DropItem || action == PlayerAction.DropItemStack) {
			return;
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
			case DropItem:
				byteBuf.writeByte(3);
				break;
			case DropItemStack:
				byteBuf.writeByte(4);
				break;
			default:
				GameU.end("unknown action");
		}
		if (action == PlayerAction.DropItem || action == PlayerAction.DropItemStack) {
			return;
		}
		pos.writeBuffer(byteBuf);
	}
}
