package net.pzdcrp.Aselia;

import de.datasecs.hydra.shared.handler.Session;
import de.datasecs.hydra.shared.protocol.packets.Packet;
import de.datasecs.hydra.shared.protocol.packets.listener.HydraPacketListener;
import de.datasecs.hydra.shared.protocol.packets.listener.PacketHandler;

public class HpbPacketListener implements HydraPacketListener {
	
	private Hpb h;

	public HpbPacketListener(Hpb h) {
		this.h = h;
	}
	
	@PacketHandler
    public void onStandardPacket(Packet standardPacket, Session session) {
		h.packetReceived(session, standardPacket);
	}
}
