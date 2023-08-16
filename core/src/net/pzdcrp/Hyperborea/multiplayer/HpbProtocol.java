package net.pzdcrp.Hyperborea.multiplayer;

import de.datasecs.hydra.shared.protocol.HydraProtocol;
import de.datasecs.hydra.shared.protocol.packets.listener.HydraPacketListener;
import net.pzdcrp.Hyperborea.multiplayer.packets.ClientChatPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ClientInventoryActionPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ClientPlaceBlockPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ClientPlayerActionPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ClientPlayerConnectionPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ClientPlayerPositionPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ClientWorldSuccLoadPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ServerChatPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ServerChunkLightPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ServerEntityDespawnPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ServerEntityPositionVelocityPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ServerLoadColumnPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ServerSetHealthPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ServerSetSlotPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ServerSetblockPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ServerSetupInventoryPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ServerSpawnEntityPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ServerSpawnPlayerPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ServerSuccessConnectPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ServerUnloadColumnPacket;
import net.pzdcrp.Hyperborea.server.InternalServer;

public class HpbProtocol extends HydraProtocol {
	public HpbProtocol(HydraPacketListener l) {
		this.registerListener(l);
		registerPacket(ServerSuccessConnectPacket.class);//0
		registerPacket(ClientPlayerConnectionPacket.class);//1
		registerPacket(ServerChatPacket.class);//2
		registerPacket(ClientWorldSuccLoadPacket.class);//3
		registerPacket(ServerSpawnPlayerPacket.class);//4
		registerPacket(ClientPlayerPositionPacket.class);//5
		registerPacket(ServerLoadColumnPacket.class);//6
		registerPacket(ServerUnloadColumnPacket.class);//7
		registerPacket(ClientPlayerActionPacket.class);//8
		registerPacket(ServerChunkLightPacket.class);//9
		registerPacket(ServerSetblockPacket.class);//10
		registerPacket(ClientPlaceBlockPacket.class);//11
		registerPacket(ServerSpawnEntityPacket.class);//12
		registerPacket(ServerEntityPositionVelocityPacket.class);//13
		registerPacket(ClientChatPacket.class);//14
		registerPacket(ServerEntityDespawnPacket.class);//15
		registerPacket(ServerSetHealthPacket.class);//16
		registerPacket(ServerSetupInventoryPacket.class);//17
		//registerPacket(ClientTransferSlotPacket.class);//18
		registerPacket(ServerSetSlotPacket.class);//19
		registerPacket(ClientInventoryActionPacket.class);//20
	}
}