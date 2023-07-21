package net.pzdcrp.Hyperborea.multiplayer;

import de.datasecs.hydra.shared.protocol.HydraProtocol;
import de.datasecs.hydra.shared.protocol.packets.listener.HydraPacketListener;
import net.pzdcrp.Hyperborea.multiplayer.packets.ClientPlayerConnectionPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ClientPlayerPositionPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ClientWorldSuccLoadPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ServerChatPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ServerLoadColumnPacket;
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
		registerPacket(ServerLoadColumnPacket.class);//7
		registerPacket(ServerUnloadColumnPacket.class);//8
	}
}