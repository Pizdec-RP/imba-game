package net.pzdcrp.Aselia.multiplayer;

import de.datasecs.hydra.shared.protocol.HydraProtocol;
import de.datasecs.hydra.shared.protocol.packets.listener.HydraPacketListener;
import net.pzdcrp.Aselia.multiplayer.packets.client.ClientPlayerConnectionPacket;
import net.pzdcrp.Aselia.multiplayer.packets.client.ClientWorldSuccLoadPacket;
import net.pzdcrp.Aselia.multiplayer.packets.client.ingame.ClientChatPacket;
import net.pzdcrp.Aselia.multiplayer.packets.client.ingame.ClientClickBlockPacket;
import net.pzdcrp.Aselia.multiplayer.packets.client.ingame.ClientPlaceBlockPacket;
import net.pzdcrp.Aselia.multiplayer.packets.client.ingame.ClientPlayerActionPacket;
import net.pzdcrp.Aselia.multiplayer.packets.client.ingame.ClientPlayerLocationDataPacket;
import net.pzdcrp.Aselia.multiplayer.packets.client.ingame.ClientPlayerRespawnPacket;
import net.pzdcrp.Aselia.multiplayer.packets.client.ingame.ClientSetHotbarSlotPacket;
import net.pzdcrp.Aselia.multiplayer.packets.client.inventory.ClientCloseInventoryPacket;
import net.pzdcrp.Aselia.multiplayer.packets.client.inventory.ClientInventoryActionPacket;
import net.pzdcrp.Aselia.multiplayer.packets.client.inventory.ClientOpenPlayerInventoryPacket;
import net.pzdcrp.Aselia.multiplayer.packets.server.ServerSuccessConnectPacket;
import net.pzdcrp.Aselia.multiplayer.packets.server.entity.ServerEntityDespawnPacket;
import net.pzdcrp.Aselia.multiplayer.packets.server.entity.ServerEntityPositionVelocityPacket;
import net.pzdcrp.Aselia.multiplayer.packets.server.entity.ServerSpawnEntityPacket;
import net.pzdcrp.Aselia.multiplayer.packets.server.ingame.ServerChatPacket;
import net.pzdcrp.Aselia.multiplayer.packets.server.ingame.ServerPlayerRespawnPacket;
import net.pzdcrp.Aselia.multiplayer.packets.server.ingame.ServerSetHealthPacket;
import net.pzdcrp.Aselia.multiplayer.packets.server.ingame.ServerSpawnPlayerPacket;
import net.pzdcrp.Aselia.multiplayer.packets.server.inventory.ServerCloseInventoryPacket;
import net.pzdcrp.Aselia.multiplayer.packets.server.inventory.ServerOpenInventoryPacket;
import net.pzdcrp.Aselia.multiplayer.packets.server.inventory.ServerSetSlotPacket;
import net.pzdcrp.Aselia.multiplayer.packets.server.inventory.ServerSetupInventoryPacket;
import net.pzdcrp.Aselia.multiplayer.packets.server.world.ServerChunkLightPacket;
import net.pzdcrp.Aselia.multiplayer.packets.server.world.ServerLoadColumnPacket;
import net.pzdcrp.Aselia.multiplayer.packets.server.world.ServerSetblockPacket;
import net.pzdcrp.Aselia.multiplayer.packets.server.world.ServerUnloadColumnPacket;
import net.pzdcrp.Aselia.server.InternalServer;

public class HpbProtocol extends HydraProtocol {
	public HpbProtocol(HydraPacketListener l) {
		this.registerListener(l);
		registerPacket(ServerSuccessConnectPacket.class);//0
		registerPacket(ClientPlayerConnectionPacket.class);//1
		registerPacket(ServerChatPacket.class);//2
		registerPacket(ClientWorldSuccLoadPacket.class);//3
		registerPacket(ServerSpawnPlayerPacket.class);//4
		registerPacket(ClientPlayerLocationDataPacket.class);//5
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
		registerPacket(ClientSetHotbarSlotPacket.class);//21
		registerPacket(ClientPlayerRespawnPacket.class);//22
		registerPacket(ServerPlayerRespawnPacket.class);//23
		registerPacket(ClientOpenPlayerInventoryPacket.class);//24
		registerPacket(ServerCloseInventoryPacket.class);//25
		registerPacket(ServerOpenInventoryPacket.class);//26
		registerPacket(ClientCloseInventoryPacket.class);//27
		registerPacket(ClientClickBlockPacket.class);//28
	}
}