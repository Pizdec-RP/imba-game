package net.pzdcrp.Hyperborea.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.math.Vector3;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import de.datasecs.hydra.server.Server;
import de.datasecs.hydra.shared.handler.Session;
import de.datasecs.hydra.shared.protocol.packets.Packet;
import io.netty.channel.ChannelHandlerContext;
import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.ActionAuthor;
import net.pzdcrp.Hyperborea.data.Vector2I;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.multiplayer.ServerPlayer;
import net.pzdcrp.Hyperborea.multiplayer.packets.ClientPlayerConnectionPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ClientWorldSuccLoadPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ServerChatPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ServerSuccessConnectPacket;
import net.pzdcrp.Hyperborea.player.Player;
import net.pzdcrp.Hyperborea.utils.GameU;
import net.pzdcrp.Hyperborea.utils.MathU;
import net.pzdcrp.Hyperborea.utils.VectorU;
import net.pzdcrp.Hyperborea.world.World;
import net.pzdcrp.Hyperborea.world.elements.Chunk;
import net.pzdcrp.Hyperborea.world.elements.Column;
import net.pzdcrp.Hyperborea.world.elements.Region;
import net.pzdcrp.Hyperborea.world.elements.blocks.Air;
import net.pzdcrp.Hyperborea.world.elements.blocks.Block;
import net.pzdcrp.Hyperborea.world.elements.blocks.Voed;
import net.pzdcrp.Hyperborea.world.elements.entities.Entity;
import net.pzdcrp.Hyperborea.world.elements.generators.DefaultWorldGenerator;

public class ServerWorld implements World {
	private Map<Session, ServerPlayer> players = new ConcurrentHashMap<>();
	public Map<Vector2I,Region> memoriedRegions = new ConcurrentHashMap<>();
	public Map<Vector2I,Column> loadedColumns = new ConcurrentHashMap<>();
	public int time = 0;
	private static final int DAY_LENGTH = 60000;
	private static final float DISTANCE_FROM_CENTER = 2000f;
	private boolean started = false;
	private JsonObject worlddata;
	private String save;
	
	/**
	 * @param savefolder ("saves/save1" or "save1")
	 */
	public ServerWorld(String savefolder) {
		this.save = savefolder;
	}
	
	public void packetReceived(Session s, Packet p) {
		GameU.log("server got packet "+p.getClass().getName());
		if (p instanceof ClientPlayerConnectionPacket) {
			ClientPlayerConnectionPacket packet = (ClientPlayerConnectionPacket) p;
			UUID id = UUID.randomUUID();
			ServerPlayer player = new ServerPlayer(id , packet.name, packet.renderDistance, s, this);
			players.put(s, player);
			s.send(new ServerSuccessConnectPacket(id));
			Vector2I playercol = readPlayerPos(packet.name);
			if (playercol == null) {
				broadcast("player is new on server!");
				playercol = VectorU.posToColumn(randomSpawnPoint());
			}
			Column c = this.getColumn(playercol);
			for (Entity entity : c.entites) {
				if (entity instanceof Player) {
					Player enplayer = (Player) entity;
					if (enplayer.nickname == packet.name) {
						player.setEntiyDataset(enplayer);
						return;
					}
				}
			}
		} else {
			ServerPlayer player = players.get(s);
			player.onPacket(p);
		}
	}
	
	public void broadcast(String text) {
		for (ServerPlayer player : players.values()) {
			player.session.send(new ServerChatPacket(text));
		}
	}
	
	public Vector3D randomSpawnPoint() {
		Vector3D pos = new Vector3D(MathU.rndi(-100, 100), 0, MathU.rndi(-100, 100));
		
		pos.y = 100;//TODO fix
		
		return pos;
	}
	
	public void start() {
		try {
			JsonReader reader = new JsonReader(new FileReader(save+"/wdata.dat"));
			worlddata = JsonParser.parseReader(reader).getAsJsonObject();
			startTickLoop();
			started = true;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		Thread chunkUpdateThread = new Thread(()->{
			while (true) {
				updateChunkLights();
				GameU.sleep(500);
			}
		}, "server chunk update thread");
		chunkUpdateThread.start();
	}
	
	public int calculateSkylight(float abstracty) {
	    return 13;
	}
	
	final int minSkylight = 1;
    final int maxSkylight = 13;
    public int skylight = 13;
	
	public void tick() {
		time++;
		if (time > DAY_LENGTH) time = 0;
		
		float angle = 2f * (float) Math.PI * time / DAY_LENGTH;
		float y = (DISTANCE_FROM_CENTER * MathU.sin(angle)) * 2;
	    
	    //System.out.println("suny: "+y);
	    int newSkylight = calculateSkylight(y);
	    if (newSkylight != skylight) {
	        skylight = newSkylight;
	        for (Region r : memoriedRegions.values()) {
		        for (Column col : r.columns.values()) {
		            for (Chunk chunk : col.chunks) {
		                chunk.inlightupd = true;//TODO желательно сделать чтобы они обновлялись не зависимо от фрустрации
		            }
		        }
	        }
	    }
		
		for (Entry<Vector2I, Column> column : loadedColumns.entrySet()) {
			column.getValue().tick();
		}
	}
	
	public Vector2I readPlayerPos(String name) {
		for (JsonElement el : worlddata.get("players").getAsJsonArray()) {
			JsonObject opl = el.getAsJsonObject();
			if (opl.get("name").getAsString().equals(name)) {
				return Vector2I.fromString(opl.get("pos").getAsString());
			}
		}
		return null;
	}
	
	public void updateChunkLights() {
		for (Column col : loadedColumns.values()) {
			for (Chunk chunk : col.chunks) {
				if (chunk.inlightupd) {
					chunk.updateLightMain();
					chunk.inlightupd = false;
					chunk.outlightupd = true;
					Hpb.world.isCycleFree = false;
				}
			}
		}
		if (Hpb.world.isCycleFree) {
			for (Column col : loadedColumns.values()) {
				for (Chunk chunk : col.chunks) {
					if (chunk.outlightupd) {
						chunk.updateLightFromOutbounds();
						Hpb.world.isCycleFree = false;
						chunk.outlightupd = false;
						chunk.updateModel();
					}
				}
			}
		}
		
	}
	
	static final int tickrate = 50;
	public void startTickLoop() {
		new Thread(() -> {
			long timeone;
			while (true) {
	        	timeone = System.nanoTime();
	    	    tick();
	    	    long two = System.nanoTime();
	    	    int elapsed = (int)(two - timeone);
	    	    int normaled = elapsed/1_000_000;
	    	    int additional = elapsed/100_000-normaled*10;
	    	    int itog = normaled + (additional >= 5 ? 1 : 0);
	    	    int tosleep = tickrate - itog;
	    	    if (tosleep > 0) {
	    	    	GameU.sleep(tosleep);
	    	    }
	        }
	    }, "server tick thread").start();
	}

	@Override
	public boolean setBlock(Block block, ActionAuthor author) {
		if (block.pos.y < 0 || block.pos.y >= buildheight) {
			Hpb.displayInfo("build limit reached");
			return false;
		}
		
		if (author == ActionAuthor.player) {
			for (Entry<Vector2I, Column> tcol : loadedColumns.entrySet()) {
				for (Entity en : tcol.getValue().entites) {
					if (block.isCollide() && block.collide(en.getHitbox())) return false;
				}
			}
		}
		Column col = getColumn(block.pos.x,block.pos.z);
		col.setBlock(block);
		for (Block block1 : block.getSides()) {
			block1.onNeighUpdate();
			block1.callChunkUpdate();
		}
		return true;
	}
	
	@Override
	public void spawnEntity(Entity entity) {
		Column spawnin = getColumn(VectorU.posToColumn(entity.pos));
		spawnin.entites.add(entity);
	}

	@Override
	public void breakBlock(Vector3D pos) {
		if (pos.y < 0 || pos.y >= buildheight) return;
		Block before = getBlock(pos);
		setBlock(new Air(pos), ActionAuthor.player);
		before.onBreak();
	}
	@Override
	public Column getColumn(double x, double z) {
		try {
			return getColumn(new Vector2I((int)Math.floor(x) >> 4, (int)Math.floor(z) >> 4));
		} catch (Exception e) {
			return null;
		}
	}
	@Override
	public Column getColumn(Vector2I cc) {
		return loadedColumns.get(cc);
	}
	@Override
	public int getLight(int x, int y, int z) {
		if (y < 0 || y >= maxheight) return 14;
		Column col = loadedColumns.get(new Vector2I(x>>4, z>>4));
		if (col == null) return 0;
		return col.chunks[y/16].rawGetLight(x&15, y&15, z&15);
	}
	@Override
	public void setLight(int x, int y, int z, int num) {
		if (y < 0 || y >= maxheight) return;
		getColumn(x,z).chunks[y/16].rawSetLight(x&15, y&15, z&15, num);
	}
	@Override
	public Block getBlock(double x, double y, double z) {
		if (y < 0 || y >= maxheight) return new Voed(new Vector3D(x,y,z));
		Column col = getColumn(x,z);
		if (col == null) return new Voed(new Vector3D(x,y,z));
		Block c = col.getBlock((int)x&15,(int)y,(int)z&15);
		if (c == null) return new Voed(new Vector3D(x,y,z));
		return c;
	}
	@Override
	public Block getBlock(Vector3D v) {
		if (v.y < 0 || v.y >= maxheight) return new Voed(v);
		Column col = getColumn(v.x,v.z);
		if (col == null) return new Voed(v);
		Block c = col.getBlock((int)v.x&15,(int)v.y,(int)v.z&15);
		if (c == null) return new Voed(v);
		return c;
	}
	
	public void addLC(Column c) {
		loadedColumns.put(c.pos, c);
		if (DefaultWorldGenerator.toadd.containsKey(c.pos)) {
    		for (Block b : DefaultWorldGenerator.toadd.get(c.pos)) {
    			c.setBlock(b);
    		}
    		DefaultWorldGenerator.toadd.remove(c.pos);
    	}
	}
	
	public boolean save() {
		try {
			Hpb.exit = true;
			System.out.println("saving");
			//loadedColumns.clear();
			int i = 0;
			for (Entry<Vector2I, Region> region : memoriedRegions.entrySet()) {
				i++;
				System.out.println("сохраняем "+i+" регион из "+memoriedRegions.size());
				writeRegion(region.getValue());
			}
			FileWriter writer = new FileWriter(save+"/wdata.dat");
			writer.write(worlddata.toString());
			writer.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			//System.exit(0);
			return false;
		}
	}
	
	public Region genOrLoadRegion(Vector2I regpos) {
		if (memoriedRegions.containsKey(regpos)) {
			return memoriedRegions.get(regpos);
		}
		String need = regpos.x+"_"+regpos.z+".reg";
	    for (final File fileEntry : new File(save).listFiles()) {
	        if (!fileEntry.isDirectory()) {
	        	if (fileEntry.getName().equals(need)) {
	        		Region reg = readRegion(regpos);
	        		memoriedRegions.put(regpos, reg);
	        		return reg;
	        	}
	        }
	    }
	    Region reg = new Region(regpos);
		memoriedRegions.put(regpos, reg);
		return reg;
	}
	
	public void writeRegion(Region reg) throws IOException {
		FileWriter writer = new FileWriter(save+"/"+reg.pos.x+"_"+reg.pos.z+".reg");
		writer.write(reg.toJson().toString());
		writer.close();
	}
	
	public Region readRegion(Vector2I regpos) {
		JsonReader reader;
		try {
			reader = new JsonReader(new FileReader(save+"/"+regpos.x+"_"+regpos.z+".reg"));
			JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();
			Region r = new Region(regpos);
			r.fromJson(obj);
			return r;
		} catch (ClassCastException e) {
			return new Region(regpos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("nema regiona :"+regpos.toString());
			System.exit(0);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		return null;
	}

	@Override
	public boolean isLocal() {
		return false;
	}

	@Override
	public List<Entity> getEntities(Vector3D pos, double radius) {
		ArrayList<Entity> e = new ArrayList<Entity>();
		for (Column column : Hpb.world.loadedColumns.values()) {
			for (Entity en : column.entites) {
				if (VectorU.sqrt(en.pos, pos) <= radius) {
					e.add(en);
				}
			}
		}
		return e;
	}
	
	@Override
	public boolean posDostupna(int x, int y, int z) {
		if (y < 0 || y > maxheight-1) {
			return false;
		}
		return loadedColumns.containsKey(VectorU.xzToColumn(x,z));
	}
}