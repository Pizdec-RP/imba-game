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
import net.pzdcrp.Hyperborea.data.Settings;
import net.pzdcrp.Hyperborea.data.Vector2I;
import net.pzdcrp.Hyperborea.multiplayer.packets.ClientPlayerPositionPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ClientWorldSuccLoadPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ServerLoadColumnPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ServerSpawnPlayerPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ServerUnloadColumnPacket;
import net.pzdcrp.Hyperborea.player.Player;
import net.pzdcrp.Hyperborea.server.ServerWorld;
import net.pzdcrp.Hyperborea.utils.VectorU;
import net.pzdcrp.Hyperborea.world.elements.Chunk;
import net.pzdcrp.Hyperborea.world.elements.Column;


public class ServerPlayer {

	public UUID id;
	public String name;
	public int renderDistance;
	public Session session;
	public Player playerentity;
	public ServerWorld server;
	public Player player;
	
	//хранит только ссылки на классы в стеке, оптимизировать не надо
	public Map<Vector2I, Column> columnsAroundPlayer = new ConcurrentHashMap<>();

	public ServerPlayer(UUID id, String name, int renderDistance, Session s, ServerWorld server) {
		this.id=id;
		this.name=name;
		this.renderDistance=renderDistance;
		this.session = s;
		this.server = server;
	}
	
	public void onPacket(Packet p) {
		if (p instanceof ClientWorldSuccLoadPacket) {
			server.broadcast(name+" logged in!");
		} else if (p instanceof ClientPlayerPositionPacket) {
			ClientPlayerPositionPacket packet = (ClientPlayerPositionPacket)p;
			server.broadcast("received pos packet "+packet.x+" "+packet.y+" "+packet.z);
			player.pos.setComponents(packet.x, packet.y, packet.z);
		}
	}
	
	public void updateLoadedColumns(Vector2I echc)  {
	    Set<Vector2I> cl = new HashSet<>();
	    for (int x = -renderDistance; x <= renderDistance; x++) {
            for (int z = -renderDistance; z <= renderDistance; z++) {
                Vector2I vector = new Vector2I(echc.x + x, echc.z + z);
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
	    	Column col = server.getColumn(c);
	    	columnsAroundPlayer.put(col.pos, col);
	    	session.send(new ServerLoadColumnPacket(col));
	    }
	}

	public void setEntiyDataset(Player enplayer) {
		this.player = enplayer;
		session.send(new ServerSpawnPlayerPacket(player.pos));
		//init other shit
	}
	
	
	
}
