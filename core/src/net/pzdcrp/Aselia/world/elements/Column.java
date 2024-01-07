package net.pzdcrp.Aselia.world.elements;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.math.Vector3;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.pzdcrp.Aselia.Hpb;
import net.pzdcrp.Aselia.data.EntityType;
import net.pzdcrp.Aselia.data.Vector2I;
import net.pzdcrp.Aselia.data.Vector3D;
import net.pzdcrp.Aselia.player.Player;
import net.pzdcrp.Aselia.utils.GameU;
import net.pzdcrp.Aselia.world.PlayerWorld;
import net.pzdcrp.Aselia.world.World;
import net.pzdcrp.Aselia.world.elements.blocks.Air;
import net.pzdcrp.Aselia.world.elements.blocks.Block;
import net.pzdcrp.Aselia.world.elements.entities.Entity;
import net.pzdcrp.Aselia.world.elements.generators.DefaultWorldGenerator;
import net.pzdcrp.Aselia.world.elements.storages.ItemStorage;

public class Column {
	public List<Entity> entites = new CopyOnWriteArrayList<>();
	public List<Player> unloadedPlayers = new CopyOnWriteArrayList<>();
	public Map<Vector3D, ItemStorage> blockData = new LinkedHashMap<>();
	public Vector2I pos;
	public Chunk[] chunks = new Chunk[PlayerWorld.chunks];
	private Vector3 center;
	private Vector3 dimensions;
	protected int[][] skylightlenght;

	public World world;

	public Column(int x, int z, boolean gen, World world) {
		this(new Vector2I(x,z), gen, world);
	}

	public Column(Vector2I cords, boolean gen, World world) {
		if (world == null) GameU.end("null world");
		this.pos = cords;
		this.world = world;
		//System.out.println("new col: "+cords.toString());
		skylightlenght = new int[16][16];

		center = new Vector3(pos.x*16+8,World.maxheight/2,pos.z*16+8);
		dimensions = new Vector3(16, World.maxheight, 16);
		for (int y = 0; y < World.chunks; y++) {
			chunks[y] = new Chunk(this, y*16, world);
		}
		if (gen)
			DefaultWorldGenerator.gen(this);
	}

	private void updateSLMDForAll() {
		for (int px = 0; px < 16; px++) {
	        for (int pz = 0; pz < 16; pz++) {
	        	recalculateSLMD(px,pz);
	        }
		}
	}

	public int getSLMD(int x, int z) {
		return skylightlenght[x][z];
	}

	public void recalculateSLMD(int x, int z) {
		for (int y = World.buildheight; y >= 0; y--) {
			if (!(getBlock(x,y,z) instanceof Air)) {
				skylightlenght[x][z] = y;
				return;
			}
		}
	}

	public Block getBlock(int x, int y, int z) {
		Chunk c = chunks[y/16];
		//GameU.log(x+" "+y+" "+z);
		return c.getBlock(x,y&15,z);
	}

	public int getBlocki(int x, int y, int z) {
		Chunk c = chunks[y/16];
		return c.getBlocki(x,y&15,z);
	}

	public int getInternalLight(int x, int y, int z) {
		Chunk c = chunks[y/16];
		return c.getInternalLight(x,y&15,z);
	}

	public void setInternalLight(int x, int y, int z, int val) {
		Chunk c = chunks[y/16];
		c.setInternalLight(x,y&15,z,val);
	}

	public void fastSetBlock(int x ,int y,int z, int id) {
		Chunk c = chunks[y/16];
		c.setBlock(x,y&15,z, id);
	}

	public int normx(int ref) {
		return pos.x*16+ref;
	}

	public int normz(int ref) {
		return pos.z*16+ref;
	}

	public void setBlock(int id, Vector3D pos) {
		int x = (int)pos.x&15, z = (int)pos.z&15, y = (int)pos.y;
		Chunk c = chunks[y/16];
		c.setBlock(x,y&15,z, id);
		int before = skylightlenght[x][z];
		if (pos.y >= before) {
			recalculateSLMD(x,z);
		}
	}

	public void setBlock(Block b) {
		int x = (int)b.pos.x&15, z = (int)b.pos.z&15, y = (int)b.pos.y;
		Chunk c = chunks[y/16];
		c.setBlock(x,y&15,z, b);
		int before = skylightlenght[x][z];
		if (b.pos.y >= before) {
			recalculateSLMD(x,z);
		}
	}

	public void tick() {
		if (world.isLocal()) {
			for (Entity entity : this.entites) {
				if (!entity.isPlayer)
					entity.tick();
			}
		} else {
			for (Entity entity : this.entites) {
				entity.tick();
			}
			for (Chunk c : chunks) {
				c.randomTick();
			}
		}
	}

	public void renderEntites(float delta) {
		for (Entity entity : this.entites) {
			if (entity.type != EntityType.player) {
				entity.render(delta);
			}
		}
	}

	public boolean isInFrustum() {
		return Hpb.world.player.cam.cam.frustum.boundsInFrustum(center, dimensions);
	}

	public void renderNormal() {
		if (!isInFrustum()) return;
		for (Chunk chunk : chunks) {
			if (chunk.allModels != null && chunk.boundsInFrustum()) {
				Hpb.render(chunk.allModels);
			}
		}
	}

	public void renderTransparent() {
		if (!isInFrustum()) return;
		if (chunks.length != 0) {
			for (Chunk chunk : chunks) {
				if (chunk.transparent != null && chunk.boundsInFrustum()) {
					Hpb.render(chunk.transparent);
				}
			}
		}
	}

	public JsonObject toJson() {
		JsonObject jcol = new JsonObject();
		//pos
		jcol.addProperty("pos", pos.toString());
		//blockdata
		JsonArray jblockdata = new JsonArray();
		for (Entry<Vector3D, ItemStorage> entry : blockData.entrySet()) {
			JsonObject jobj = new JsonObject();
			jobj.addProperty("p", entry.getKey().toString());
			jobj.addProperty("id", ItemStorage.toId(entry.getValue()));
			jobj.addProperty("data", entry.getValue().toJson());
			jblockdata.add(jobj);
		}
		jcol.add("blockData", jblockdata);
		//blocks
		JsonArray blocks = new JsonArray();
		for (int px = 0; px < 16; px++) {
	        for (int py = 0; py < World.maxheight; py++) {
	            for (int pz = 0; pz < 16; pz++) {
	            	blocks.add(getBlocki(px,py,pz));
	            }
	        }
	    }
		jcol.add("blocks", blocks);
		//entities
		jcol.add("entities", new JsonArray());
		jcol.add("players", new JsonArray());
		for (Entity entity : entites) {
			if (entity instanceof Player) {
				Player newestPlayer = (Player)entity;
				boolean y = false;
				for (Player unloadedPlayer : unloadedPlayers) {
					if (unloadedPlayer.nickname.equals(newestPlayer.nickname)) {
						unloadedPlayers.remove(unloadedPlayer);
						unloadedPlayers.add(newestPlayer);
						y = true;
						break;
					}
				}
				if (y) continue;
				else unloadedPlayers.add(newestPlayer);
			} else {
				JsonObject jen = new JsonObject();
				entity.getJson(jen);
				jcol.get("entities").getAsJsonArray().add(jen);
			}
		}

		for (Player player : unloadedPlayers) {
			JsonObject jen = new JsonObject();
			player.getJson(jen);
			jcol.get("players").getAsJsonArray().add(jen);
		}


		return jcol;
	}

	public Player getUnloadedPlayerByName(String name) {
		for (Player p : unloadedPlayers) {
			if (p.nickname.equals(name)) {
				unloadedPlayers.remove(p);
				return p;
			}
		}
		GameU.end("cant found entity of player "+name+" in "+GameU.arrayString("players", unloadedPlayers));
		return null;
	}

	/**
	 * Server side
	 * @param jcol
	 */
	public void fromJson(JsonObject jcol) {
		Vector2I cc = Vector2I.fromString(jcol.get("pos").getAsString());
		if (!this.pos.equals(cc)) {
			GameU.end("корды не сходятся удаляй регион");
		}
		JsonArray jblockdata = jcol.get("blockData").getAsJsonArray();
		for (JsonElement el : jblockdata) {
			JsonObject jobj = el.getAsJsonObject();
			Vector3D pos = Vector3D.fromString(jobj.get("p").getAsString());
			byte storageid = jobj.get("id").getAsByte();
			ItemStorage storage = ItemStorage.storageTable.get(storageid).sclone();
			storage.fromJson(jobj.get("data").getAsString());
			blockData.put(pos, storage);
		}
		//blocks
		int i = 0;
		JsonArray blocks = jcol.get("blocks").getAsJsonArray();
		for (int px = 0; px < 16; px++) {
			for (int py = 0; py < World.maxheight; py++) {
				for (int pz = 0; pz < 16; pz++) {
	            	fastSetBlock(px, py, pz, blocks.get(i).getAsInt());
	            	i++;
	            }
	        }
	    }
		//entities
		JsonArray entities = jcol.get("entities").getAsJsonArray();
		for (JsonElement jene : entities) {
			try {
				JsonObject jen = jene.getAsJsonObject();
				//System.out.println(jen.toString());
				Entity entity = null;
				int type = jen.get("type").getAsInt();
				Vector3D pos = Vector3D.fromString(jen.get("pos").getAsString());
				if (type == 1) {
					GameU.end("player in entities!");
					//GameU.tracer();
				} else {
					entity = Entity.entities.get(type).cloneOnColumnLoad(pos, world, Entity.genLocalId());
					entity.fromJson(jen);
				}
				//world.spawnEntity(entity);
				entites.add(entity);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("ignoring entity error");
			}
		}

		JsonArray players = jcol.get("players").getAsJsonArray();
		//unloadedPlayers
		for (JsonElement jene : players) {
			try {
				JsonObject jen = jene.getAsJsonObject();
				Player player = null;
				Vector3D pos = Vector3D.fromString(jen.get("pos").getAsString());
				player = new Player(pos.x,pos.y,pos.z,jen.get("name").getAsString(), world, Entity.genLocalId());
				player.fromJson(jen);
				GameU.log("fetched player "+player.nickname);
				unloadedPlayers.add(player);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("ignore entity error");
			}
		}

		updateSLMDForAll();

	}

	public void recheckcanrender() {
		for (Chunk c : chunks) {
			if (!c.canrender) return;
		}
		canrender = true;
	}

	private boolean canrender = false;
	public boolean canrender() {
		return canrender;
	}
}