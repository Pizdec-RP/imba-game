package net.pzdcrp.Aselia.server;

import de.datasecs.hydra.server.HydraServer;
import de.datasecs.hydra.server.Server;
import de.datasecs.hydra.shared.handler.Session;
import de.datasecs.hydra.shared.handler.listener.HydraSessionListener;
import de.datasecs.hydra.shared.protocol.HydraProtocol;
import de.datasecs.hydra.shared.protocol.packets.Packet;
import de.datasecs.hydra.shared.protocol.packets.listener.HydraPacketListener;
import de.datasecs.hydra.shared.protocol.packets.listener.PacketHandler;
import io.netty.channel.ChannelOption;
import net.pzdcrp.Aselia.multiplayer.HpbProtocol;
import net.pzdcrp.Aselia.utils.GameU;

public class InternalServer implements HydraPacketListener {
	public HydraServer server;
	private HydraProtocol protocol = new HpbProtocol(this);
	public static ServerWorld world;

	public InternalServer() {
		world = new ServerWorld("save");
		server = new Server.Builder("127.0.0.1", 7777, protocol)
			.bossThreads(2)
            .workerThreads(4)
            .childOption(ChannelOption.TCP_NODELAY, true)
            .childOption(ChannelOption.SO_KEEPALIVE, true)
            .addListener(new HydraSessionListener() {
                @Override
                public void onConnected(Session session) {
                    GameU.log("client connected");
                }

                @Override
                public void onDisconnected(Session session) {
                	GameU.log("client disconnected");
                }
            })
            .build();
		GameU.log("server created");
		world.start();
	}

	@PacketHandler
    public void onStandardPacket(Packet packet, Session session) {
		world.packetReceived(session, packet);
	}
}
