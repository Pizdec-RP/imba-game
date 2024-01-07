package net.pzdcrp.Aselia.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import de.datasecs.hydra.shared.handler.Session;
import de.datasecs.hydra.shared.protocol.packets.Packet;
import net.pzdcrp.Aselia.Hpb;
import net.pzdcrp.Aselia.data.ActionAuthor;
import net.pzdcrp.Aselia.data.Vector2I;
import net.pzdcrp.Aselia.data.Vector3D;
import net.pzdcrp.Aselia.data.Vector3I;
import net.pzdcrp.Aselia.multiplayer.ServerPlayer;
import net.pzdcrp.Aselia.multiplayer.packets.client.ClientPlayerConnectionPacket;
import net.pzdcrp.Aselia.multiplayer.packets.client.ingame.ClientPlayerLocationDataPacket;
import net.pzdcrp.Aselia.multiplayer.packets.server.ServerSuccessConnectPacket;
import net.pzdcrp.Aselia.multiplayer.packets.server.entity.ServerSpawnEntityPacket;
import net.pzdcrp.Aselia.multiplayer.packets.server.world.ServerChunkLightPacket;
import net.pzdcrp.Aselia.multiplayer.packets.server.world.ServerSetblockPacket;
import net.pzdcrp.Aselia.player.Player;
import net.pzdcrp.Aselia.utils.GameU;
import net.pzdcrp.Aselia.utils.MathU;
import net.pzdcrp.Aselia.utils.VectorU;
import net.pzdcrp.Aselia.world.World;
import net.pzdcrp.Aselia.world.elements.Chunk;
import net.pzdcrp.Aselia.world.elements.Column;
import net.pzdcrp.Aselia.world.elements.Region;
import net.pzdcrp.Aselia.world.elements.blocks.Air;
import net.pzdcrp.Aselia.world.elements.blocks.Block;
import net.pzdcrp.Aselia.world.elements.blocks.Fire;
import net.pzdcrp.Aselia.world.elements.blocks.Voed;
import net.pzdcrp.Aselia.world.elements.entities.Entity;
import net.pzdcrp.Aselia.world.elements.entities.ItemEntity;
import net.pzdcrp.Aselia.world.elements.generators.DefaultWorldGenerator;
import net.pzdcrp.Aselia.world.elements.inventory.items.DirtItem;
import net.pzdcrp.Aselia.world.elements.inventory.items.Item;
import net.pzdcrp.Aselia.world.elements.storages.ChestItemStorage;

public class ServerWorld implements World {
	public Map<Session, ServerPlayer> players = new ConcurrentHashMap<>();
	public Map<Vector2I,Region> memoriedRegions = new ConcurrentHashMap<>();
	public Map<Vector2I,Column> loadedColumns = new ConcurrentHashMap<>();
	public int time = 0;
	private static final int DAY_LENGTH = 60000;
	private static final float DISTANCE_FROM_CENTER = 2000f;
	private JsonObject worlddata;
	private String save;

	/**
	 * @param savefolder ("saves/save1" or "save1")
	 */
	public ServerWorld(String savefolder) {
		this.save = savefolder;
	}

	public void packetReceived(Session s, Packet p) {
		if (!(p instanceof ClientPlayerLocationDataPacket)) {
			GameU.log("client -> server "+p.getClass().getSimpleName());
		}
		if (p instanceof ClientPlayerConnectionPacket) {
			ClientPlayerConnectionPacket packet = (ClientPlayerConnectionPacket) p;
			UUID id = UUID.randomUUID();
			ServerPlayer player = new ServerPlayer(id,
					packet.name,
					packet.renderDistance,
					s,
					this);
			//players.put(s, player);
			s.send(new ServerSuccessConnectPacket(id));
			Vector2I playercol = readPlayerPos(packet.name);
			if (playercol == null) {
				GameU.log("player is new on server!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				Vector3D spawnpoint = randomSpawnPoint();
				playercol = VectorU.posToColumn(spawnpoint);
				player.playerEntity = new Player(spawnpoint.x,
						spawnpoint.y,
						spawnpoint.z,
						player.name,
						this,
						Entity.genLocalId());
			} else {
				Column c = this.getColumn(playercol);
				Player enplayer = c.getUnloadedPlayerByName(player.name);
				GameU.log("new player "+enplayer.nickname);
				player.playerEntity = enplayer;
				player.setEntityDataset(enplayer);
				/*if (player.playerEntity == null) {
					GameU.end("cant found entity of player "+player.name+" in "+GameU.arrayString("players", c.unloadedPlayers));
				}*/
			}
			player.playerEntity.serverProfile = player;
			player.playerEntity.tick();
			players.put(s, player);
		} else {
			ServerPlayer player = players.get(s);
			if (player == null) {
				GameU.end("player is not on server, but he sending packets");
			}
			player.onPacket(p);
		}
		//TODO on ClientDisconnectPacket -> write position in worlddata.players
	}

	public ServerPlayer getPlayerByName(String name) {
		for (ServerPlayer p : players.values()) {
			if (p.name.equals(name)) return p;
		}
		GameU.end("unknown player");
		return null;
	}

	public void broadcast(String text) {
		for (ServerPlayer player : players.values()) {
			player.sendmsg(text);
		}
	}

	public Vector3D randomSpawnPoint() {
		Vector3I pos = new Vector3I(MathU.rndi(-100, 100), 0, MathU.rndi(-100, 100));

		Column c = getColumn(VectorU.posToColumn(pos));
		int nx = pos.x&15, nz = pos.z&15;
		//GameU.log(nx+" - "+nz);
		c.recalculateSLMD(nx,nz);
		pos.y = c.getSLMD(nx,nz) + 1;

		return new Vector3D(pos.x,pos.y,pos.z);
	}

	public void start() {
		try {
			JsonReader reader = new JsonReader(new FileReader(save+"/wdata.dat"));
			worlddata = JsonParser.parseReader(reader).getAsJsonObject();
		} catch (Exception e) {
			GameU.log("new world!!!!!!!!!!!!!!!!!!!!!!!!!!!1111");
			worlddata = new JsonObject();
			worlddata.add("players", new JsonArray());
			//e.printStackTrace();
		}
		startTickLoop();
		Thread chunkUpdateThread = new Thread(()->{
			while (true) {
				if (Hpb.exit) return;
				updateChunkLights();
				for (ServerPlayer sp : players.values()) {
					sp.updateLoadedColumns();
				}
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

	@Override
	public void tick() {
		if (Hpb.exit) return;
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
		//GameU.log("---------");
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

	@Override
	public void broadcastByColumn(Vector2I pos, Packet p) {
		for (ServerPlayer player : players.values()) {
			if (player.columnsAroundPlayer.containsKey(pos)) {
				player.session.send(p);
			}
		}
	}

	public boolean isCycleFree = true;
	public void updateChunkLights() {
		for (Column col : loadedColumns.values()) {
			for (Chunk chunk : col.chunks) {
				if (chunk.inlightupd) {
					chunk.updateLightMain();
					chunk.inlightupd = false;
					chunk.outlightupd = true;
					isCycleFree = false;
				}
			}
		}
		if (isCycleFree) {
			for (Column col : loadedColumns.values()) {
				for (Chunk chunk : col.chunks) {
					if (chunk.outlightupd) {
						chunk.updateLightFromOutbounds();
						broadcastByColumn(col.pos, new ServerChunkLightPacket(chunk.getLightStorage(), chunk.getPos()));
						isCycleFree = false;
						chunk.outlightupd = false;
						//chunk.updateModel();
					}
				}
			}
		}
		isCycleFree = true;
	}

	static final int tickrate = 50;
	public void startTickLoop() {
		new Thread(() -> {
			long timeone;
			while (true) {
				if (Hpb.exit) return;
	        	timeone = System.nanoTime();
	    	    tick();
	    	    //PlayerWorld.deltaTime();
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
			Hpb.displayInfo("build limit reached");//TODO изменить на ServerInfoBarPacket
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
		//broadcastByColumn(col.pos, new ServerSetblockPacket(block.pos, block.getId(), author));
		for (ServerPlayer player : players.values()) {
			if (player.columnsAroundPlayer.containsKey(col.pos)) {
				player.session.send(new ServerSetblockPacket(block.pos, block.getId(), author));
				player.chunkLightOrder.add(VectorU.posToChunk(block.pos));
			}
		}
		for (Block block1 : block.getSides(this)) {
			block1.onNeighUpdate(this);
			block1.callChunkUpdate(this);
		}
		return true;
	}

	@Override
	public void spawnEntity(Entity entity) {
		Vector2I pos = VectorU.posToColumn(entity.pos);
		//if (loadedColumns.containsKey(pos)) { //хз зачем эта проверка
		Column spawnin = getColumn(pos);
		if (entity.isPlayer) {
			//TODO ServerSpawnUnplayablePlayerPacket xz
			GameU.err("игрока не спавним для других игроков");
		} else {
			this.broadcastByColumn(spawnin.pos, new ServerSpawnEntityPacket(entity));
		}
		spawnin.entites.add(entity);
		//}
	}

	@Override
	public void breakBlock(Vector3D pos) {
		if (pos.y < 0 || pos.y >= buildheight) return;
		Block before = getBlock(pos);
		before.onBreak(this);
		setBlock(new Air(pos), ActionAuthor.player);
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
		if (loadedColumns.containsKey(cc)) {
			return loadedColumns.get(cc);
		} else {
			Region r = genOrLoadRegion(VectorU.ColumnToRegion(cc));
			Column c = r.getColumn(cc);
			addLC(c);
			return c;
		}
	}
	@Override
	public int getLight(int x, int y, int z) {
		if (y < 0 || y >= maxheight) return 14;
		Column col = loadedColumns.get(new Vector2I(x>>4, z>>4));
		if (col == null) return -1;
		return col.chunks[y/16].rawGetLight(x&15, y&15, z&15);
	}
	@Override
	public void setLight(int x, int y, int z, int num) {
		if (y < 0 || y >= maxheight) return;
		Column col = loadedColumns.get(new Vector2I(x>>4, z>>4));
		if (col == null) return;
		col.chunks[y/16].rawSetLight(x&15, y&15, z&15, num);
	}
	@Override
	public Block getBlock(float x, float y, float z) {
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

	private static final boolean saveit = false;
	public boolean save() {
		if (!saveit) return true;
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
			JsonArray jplayers = worlddata.get("players").getAsJsonArray();
			for (ServerPlayer player : players.values()) {
				String name = player.name;
				boolean gotIt = false;
				for (JsonElement jppos : jplayers) {
					JsonObject jp = jppos.getAsJsonObject();
					if (jp.get("name").getAsString().equals(name)) {
						jp.remove("pos");
						jp.addProperty("pos", player.playerEntity.curCol.pos.toString());
						gotIt = true;
						break;
					}
				}
				if (!gotIt) {
					JsonObject njp = new JsonObject();
					njp.addProperty("name", player.name);
					njp.addProperty("pos", player.playerEntity.echc.toString());
					jplayers.add(njp);
				}
			}
			writer.write(worlddata.toString());
			writer.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			Hpb.exit = false;
			//System.exit(0);
			return false;
		}
	}

	public Region genOrLoadRegion(Vector2I regpos) {
		if (memoriedRegions.containsKey(regpos)) {
			return memoriedRegions.get(regpos);
		}
		String need = regpos.x+"_"+regpos.z+".reg";
		Region reg;
		File[] files = new File(save).listFiles();
		if (files == null) {
			reg = new Region(regpos, this);
			memoriedRegions.put(regpos, reg);
			return reg;
		}
	    for (final File fileEntry : files) {
	        if (!fileEntry.isDirectory()) {
	        	if (fileEntry.getName().equals(need)) {
	        		reg = readRegion(regpos);
	        		memoriedRegions.put(regpos, reg);
	        		return reg;
	        	}
	        }
	    }
	    reg = new Region(regpos, this);
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
			Region r = new Region(regpos, this);
			r.fromJson(obj);
			return r;
		} catch (ClassCastException e) {
			return new Region(regpos, this);
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
	public List<Entity> getEntities(Vector3D pos, float radius) {
		ArrayList<Entity> e = new ArrayList<>();
		for (Column column : loadedColumns.values()) {
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

	@Override
	public boolean containColumn(Vector2I pos) {
		return loadedColumns.containsKey(pos);
	}

	@Override
	public Column getWithoutLoad(Vector2I cc) {
		return loadedColumns.get(cc);
	}

	@Override
	public Map<Vector2I, Column> getLoadedColumns() {
		return this.loadedColumns;
	}

	@Override
	public void setBlock(int block, Vector3D pos, ActionAuthor author) {
		GameU.end("under construction");
	}

	@Override
	public boolean posDostupna(Vector3D pos) {
		GameU.end("under construction");
		return true;
	}

	@Override
	public Entity getEntity(int id) {
		GameU.end("under construction");
		return null;
	}

	public void commandExecutor(String text, ServerPlayer player) {
		String[] splitted = text.split(" ");
		String command = splitted[0];
		String[] args = new String[splitted.length - 1];
		System.arraycopy(splitted, 1, args, 0, splitted.length - 1);
		if (command.equals("spawn")) {
			String entity = args[0];
			Entity en = null;
			if (entity.equals("item")) {
				this.spawnEntity(en = new ItemEntity(player.playerEntity.pos.clone(), new DirtItem(1), this, Entity.genLocalId()));
			} else {
				player.sendmsg("не ебу что это "+entity);
				return;
			}
			player.sendmsg("сущность "+en.getClass().getSimpleName()+" создана с айди "+en.localId);
		} else if (command.equals("centities")) {
			int i = 0;
			for (Column c : loadedColumns.values()) {
				i += c.entites.size();
			}
			player.sendmsg("there is "+i+" entities");
		} else if (command.equals("kill")) {
			player.playerEntity.setHp((byte)0);
		} else if (command.equals("rsp")) {
			player.playerEntity.respawn();
		} else if (command.equals("give")) {
			String item = args[0];
			for (Item i : Item.items.values()) {
				if (i.getName().toLowerCase().contains(item.toLowerCase())) {
					Item cloned = i.clone(1);
					this.spawnEntity(new ItemEntity(player.playerEntity.getEyeLocation(), cloned, this, Entity.genLocalId()));
					player.sendmsg("gived 1 "+i.getName());
					return;
				}
			}
			player.sendmsg("unknown item "+item);
		} else if (command.equals("razeb")) {
			new Thread(()->{
				Vector3D spawn = player.playerEntity.getEyeLocation().add(0, 1, 0);
				while (true) {
					int id = MathU.random(new ArrayList<Integer>() {{add(1);add(2);add(3);add(5);add(6);add(7);}});
					Item item = Item.items.get(id).clone(MathU.rndi(1, 60));
					ItemEntity e = new ItemEntity(spawn.clone(), item, this, Entity.genLocalId());
					e.vel.setComponents(MathU.rndf(-0.6f, 0.6f),
							MathU.rndf(0f, 0.6f),
							MathU.rndf(-0.6f, 0.6f));
					this.spawnEntity(e);
					GameU.log("spawning");
					GameU.sleep(50);
				}
			}).start();
		} else if (command.equals("clone")) {
			for (Column c : loadedColumns.values()) {
				for (Entity e : c.entites) {
					if (!(e instanceof Player))
						spawnEntity(e.clone(e.pos.clone(), this, e.consumeData(), Entity.genLocalId()));
				}
			}
		} else if (command.equals("istest")) {
			player.playerEntity.castedInv.open(new ChestItemStorage());
		} else if (command.equals("fire")) {
			setBlock(new Fire(player.playerEntity.pos), ActionAuthor.command);
		} else {
			player.sendmsg("unknown command \""+command+"\"");
		}
	}

	@Override
	public List<Player> getPlayers(Vector3D pos, float radius) {
		ArrayList<Player> e = new ArrayList<>();
		for (ServerPlayer spl : players.values()) {
			Player entity = spl.playerEntity;
			if (VectorU.sqrt(entity.getCenterPoint(), pos) <= radius) {
				e.add(entity);
			}
		}
		return e;
	}
}
