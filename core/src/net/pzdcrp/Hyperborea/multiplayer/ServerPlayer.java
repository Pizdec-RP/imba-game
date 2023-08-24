package net.pzdcrp.Hyperborea.multiplayer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Map.Entry;

import com.badlogic.gdx.math.Vector3;

import de.datasecs.hydra.shared.handler.Session;
import de.datasecs.hydra.shared.protocol.packets.Packet;
import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.ActionAuthor;
import net.pzdcrp.Hyperborea.data.Settings;
import net.pzdcrp.Hyperborea.data.Vector2I;
import net.pzdcrp.Hyperborea.data.Vector3I;
import net.pzdcrp.Hyperborea.multiplayer.packets.client.ClientWorldSuccLoadPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.client.ingame.ClientChatPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.client.ingame.ClientClickBlockPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.client.ingame.ClientPlaceBlockPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.client.ingame.ClientPlayerActionPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.client.ingame.ClientPlayerActionPacket.PlayerAction;
import net.pzdcrp.Hyperborea.multiplayer.packets.client.ingame.ClientPlayerLocationDataPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.client.ingame.ClientPlayerRespawnPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.client.ingame.ClientSetHotbarSlotPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.client.inventory.ClientCloseInventoryPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.client.inventory.ClientInventoryActionPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.client.inventory.ClientPlayerInventoryActionPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.server.entity.ServerSpawnEntityPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.server.ingame.ServerChatPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.server.ingame.ServerSetHealthPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.server.ingame.ServerSpawnPlayerPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.server.inventory.ServerCloseInventoryPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.server.inventory.ServerSetupInventoryPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.server.world.ServerChunkLightPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.server.world.ServerLoadColumnPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.server.world.ServerUnloadColumnPacket;
import net.pzdcrp.Hyperborea.player.Player;
import net.pzdcrp.Hyperborea.server.ServerWorld;
import net.pzdcrp.Hyperborea.utils.GameU;
import net.pzdcrp.Hyperborea.utils.VectorU;
import net.pzdcrp.Hyperborea.world.elements.Chunk;
import net.pzdcrp.Hyperborea.world.elements.Column;
import net.pzdcrp.Hyperborea.world.elements.blocks.Air;
import net.pzdcrp.Hyperborea.world.elements.entities.Entity;
import net.pzdcrp.Hyperborea.world.elements.generators.DefaultWorldGenerator;


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
	public List<Vector3I> chunkLightOrder = new CopyOnWriteArrayList<>();

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
			} else if (p instanceof ClientPlayerLocationDataPacket) {
				ClientPlayerLocationDataPacket packet = (ClientPlayerLocationDataPacket)p;
				//TODO античит + антикраш
				playerEntity.pos.set(packet.pos);
				playerEntity.vel.set(packet.vel);
				playerEntity.onGround = packet.onGround;
				playerEntity.setYaw(packet.yaw);
				playerEntity.setPitch(packet.pitch);
			} else if (p instanceof ClientPlayerActionPacket) {
				ClientPlayerActionPacket packet = (ClientPlayerActionPacket)p;
				//TODO доделать
				if (packet.action == PlayerAction.DropItem) {
					playerEntity.castedInv.dropHandItem(false);
				} else if (packet.action == PlayerAction.DropItemStack) {
					playerEntity.castedInv.dropHandItem(true);
				} else {
					if (!world.loadedColumns.containsKey(VectorU.posToColumn(packet.pos))) {
						disconnect("click outside of range "+packet.pos.toString());
					}
					if (packet.action == PlayerAction.EndBreakingBlock) {
						world.breakBlock(packet.pos);
					}
				}
			} else if (p instanceof ClientPlaceBlockPacket) {
				ClientPlaceBlockPacket packet = (ClientPlaceBlockPacket)p;
				if (!world.loadedColumns.containsKey(VectorU.posToColumn(packet.pos))) {
					disconnect("click outside of range "+packet.pos.toString());
				}
				playerEntity.castedInv.onRClick(packet.pos, packet.face);
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
				if (!playerEntity.castedInv.correctSlot(packet.clickedslot)) {
					disconnect("bad slot "+packet.clickedslot);
				}
				playerEntity.castedInv.doActionByPacketOnServer(packet.clickedslot, packet.downclick, packet.mousebutton);
			} else if (p instanceof ClientSetHotbarSlotPacket) {
				ClientSetHotbarSlotPacket packet = (ClientSetHotbarSlotPacket)p;
				if (packet.slot > 9 || packet.slot < 0) {
					disconnect("bad hotbar slot "+packet.slot);
				}
				playerEntity.castedInv.setCurrentSlotInt(packet.slot);
			} else if (p instanceof ClientPlayerRespawnPacket) {
				if (playerEntity.hp != 0) {
					disconnect("respawn with "+playerEntity.hp+" hp?");
				}
				playerEntity.respawn();
			} else if (p instanceof ClientPlayerInventoryActionPacket) {
				if (!playerEntity.castedInv.isOpened) {
					session.send(new ServerCloseInventoryPacket());
					return;
				}
				ClientPlayerInventoryActionPacket packet = (ClientPlayerInventoryActionPacket)p;
				playerEntity.castedInv.isOpened = packet.open;
			} else if (p instanceof ClientCloseInventoryPacket) {
				playerEntity.castedInv.close();
			} else if (p instanceof ClientClickBlockPacket) {
				ClientClickBlockPacket packet = (ClientClickBlockPacket)p;
				world.getBlock(packet.pos).onClick(playerEntity);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void disconnect(String reason) {
		GameU.end(reason);
		//TODO переделать
	}
	
	public void updateLoadedColumns()  {
		if (!isLoaded) return;
		if (playerEntity.echc == null) {
			playerEntity.echc = new Vector2I(playerEntity.pos.x,playerEntity.pos.z);
		}
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
	    	for (Chunk chunk : world.getColumn(key).chunks) {
	    		if (chunkLightOrder.contains(chunk.getPos()))
	    			chunkLightOrder.remove(chunk.getPos());
	    	}
	    	session.send(new ServerUnloadColumnPacket(key));
	    }
	    // подгружаем недоставшиеся
	    for (Vector2I c : cl) {
	    	Column col = world.getColumn(c);
	    	columnsAroundPlayer.put(col.pos, col);
	    	session.send(new ServerLoadColumnPacket(col));
	    	for (Chunk chunk : col.chunks) {
	    		chunkLightOrder.add(chunk.getPos());
	    	}
	    	for (Entity entity : col.entites) {
	    		if (!entity.isPlayer) {//TODO send player if it online
	    			session.send(new ServerSpawnEntityPacket(entity));
	    		} else {
	    			GameU.err("send player if it online");
	    		}
	    	}
	    }
	    //GameU.log("chunks in order: "+chunkLightOrder.size());
	    int i = 0;
	    for (Vector3I orderedPos : chunkLightOrder) {
	    	Chunk c = world.getColumn(new Vector2I(orderedPos.x,orderedPos.z)).chunks[orderedPos.y];
	    	if (c.inlightupd || c.outlightupd) {//убираем потомучто эти чанки отсылаются после обновления света
	    		chunkLightOrder.remove(orderedPos);
	    	} else {
	    		i++;
	    		session.send(new ServerChunkLightPacket(c.getLightStorage(), orderedPos));
	    		chunkLightOrder.remove(orderedPos);
	    	}
	    	if (i >= 128) break;
	    }
	    //GameU.log(DefaultWorldGenerator.toadd.size());
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
