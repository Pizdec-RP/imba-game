package net.pzdcrp.Hyperborea.multiplayer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map.Entry;

import com.badlogic.gdx.math.Vector3;

import de.datasecs.hydra.shared.handler.Session;
import de.datasecs.hydra.shared.protocol.packets.Packet;
import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.ActionAuthor;
import net.pzdcrp.Hyperborea.data.Settings;
import net.pzdcrp.Hyperborea.data.Vector2I;
import net.pzdcrp.Hyperborea.multiplayer.packets.ClientChatPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ClientInventoryActionPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ClientPlaceBlockPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ClientPlayerActionPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ClientPlayerPositionPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ClientWorldSuccLoadPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ServerChatPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ServerLoadColumnPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ServerSetHealthPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ServerSetblockPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ServerSetupInventoryPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ServerSpawnEntityPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ServerSpawnPlayerPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ServerUnloadColumnPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ClientPlayerActionPacket.PlayerAction;
import net.pzdcrp.Hyperborea.player.Player;
import net.pzdcrp.Hyperborea.server.ServerWorld;
import net.pzdcrp.Hyperborea.utils.GameU;
import net.pzdcrp.Hyperborea.utils.VectorU;
import net.pzdcrp.Hyperborea.world.elements.Chunk;
import net.pzdcrp.Hyperborea.world.elements.Column;
import net.pzdcrp.Hyperborea.world.elements.blocks.Air;
import net.pzdcrp.Hyperborea.world.elements.entities.Entity;


public class ServerPlayer {

	public UUID id;
	public String name;
	public int renderDistance;
	public Session session;
	public ServerWorld world;
	public Player playerEntity;
	private boolean isLoaded = false;
	
	//хранит только ссылки на классы в стеке, оптимизировать не надо
	public Map<Vector2I, Column> columnsAroundPlayer = new ConcurrentHashMap<>();

	public ServerPlayer(UUID id, String name, int renderDistance, Session s, ServerWorld world) {
		this.id=id;
		this.name=name;
		this.renderDistance=renderDistance;
		this.session = s;
		this.world = world;
	}
	
	public void onPacket(Packet p) {
		try {
			if (p instanceof ClientWorldSuccLoadPacket) {
				if (isLoaded) {
					GameU.end("ClientWorldSuccLoadPacket должен отсылаться 1 раз");
				}
				isLoaded = true;
				session.send(new ServerSpawnPlayerPacket(playerEntity.pos, playerEntity.localId));
				session.send(new ServerSetupInventoryPacket(playerEntity.castedInv.getItems()));
				session.send(new ServerSetHealthPacket(playerEntity.hp));
				world.broadcast(name+" logged in!");
				session.send(new ServerLoadColumnPacket(playerEntity.curCol));
			} else if (p instanceof ClientPlayerPositionPacket) {
				ClientPlayerPositionPacket packet = (ClientPlayerPositionPacket)p;
				playerEntity.pos.setComponents(packet.x, packet.y, packet.z);
				playerEntity.onGround = packet.onGround;
			} else if (p instanceof ClientPlayerActionPacket) {
				ClientPlayerActionPacket packet = (ClientPlayerActionPacket)p;
				//TODO доделать
				if (packet.action == PlayerAction.EndBreakingBlock) {
					world.breakBlock(packet.pos);
				}
			} else if (p instanceof ClientPlaceBlockPacket) {
				//TODO
			} else if (p instanceof ClientChatPacket) {
				ClientChatPacket packet = (ClientChatPacket) p;
				if (packet.msg.startsWith("/")) {
					world.commandExecutor(packet.msg.replaceFirst("/", ""), this);
				} else {
					world.broadcast("["+name+"] "+packet.msg);
				}
			/*} else if (p instanceof ClientTransferSlotPacket) {
				ClientTransferSlotPacket packet = (ClientTransferSlotPacket)p;
				playerEntity.castedInv.transferSlotOnServerByPacket(packet.fromSlot, packet.toSlot);*/
			} else if (p instanceof ClientInventoryActionPacket) {
				ClientInventoryActionPacket packet = (ClientInventoryActionPacket)p;
				playerEntity.castedInv.doActionByPacketOnServer(packet.clickedslot, packet.downclick, packet.mousebutton);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void updateLoadedColumns()  {
		if (!isLoaded) return;
		if (playerEntity.echc == null) {
			playerEntity.echc = new Vector2I(playerEntity.pos.x,playerEntity.pos.z);
		}
		//GameU.log(playerEntity.echc.x+" "+playerEntity.echc.z);
	    Set<Vector2I> cl = new HashSet<>();
	    for (int x = -renderDistance; x <= renderDistance; x++) {
            for (int z = -renderDistance; z <= renderDistance; z++) {
                Vector2I vector = new Vector2I(playerEntity.echc.x + x, playerEntity.echc.z + z);
                cl.add(vector);
            }
        }
	    
	    // Хранит ключи столбцов, которые нужно удалить
	    List<Vector2I> toRemove = new ArrayList<>();
	    // Проверяем какие столбцы уже загружены и убираем из cl то что есть
	    for (Entry<Vector2I, Column> col : columnsAroundPlayer.entrySet()) {
	        if (cl.contains(col.getValue().pos)) {
	            cl.remove(col.getValue().pos);
	        } else { // отмечаем для удаления столбцы, которых нет в cl
	            toRemove.add(col.getKey());
	        }
	    }
	    // Удаляем столбцы
	    for (Vector2I key : toRemove) {
	    	columnsAroundPlayer.remove(key);
	    	session.send(new ServerUnloadColumnPacket(key));
	    }
	    // подгружаем недоставшиеся
	    for (Vector2I c : cl) {
	    	Column col = world.getColumn(c);
	    	columnsAroundPlayer.put(col.pos, col);
	    	session.send(new ServerLoadColumnPacket(col));
	    	for (Entity entity : col.entites) {
	    		if (!entity.isPlayer) {//TODO send player if it online
	    			session.send(new ServerSpawnEntityPacket(entity));
	    		}
	    	}
	    }
	}

	public void setEntityDataset(Player enplayer) {
		this.playerEntity = enplayer;
		//session.send(new ServerSpawnPlayerPacket(playerEntity.pos));
		//подгрузка остальных данных по типу инвентаря
	}

	public void sendmsg(String text) {
		session.send(new ServerChatPacket(text));
	}
	
}
