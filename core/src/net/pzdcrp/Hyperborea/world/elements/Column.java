package net.pzdcrp.Hyperborea.world.elements;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.math.Vector3;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.AABB;
import net.pzdcrp.Hyperborea.data.EntityType;
import net.pzdcrp.Hyperborea.data.Vector2I;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.player.Player;
import net.pzdcrp.Hyperborea.utils.GameU;
import net.pzdcrp.Hyperborea.world.PlayerWorld;
import net.pzdcrp.Hyperborea.world.elements.blocks.Air;
import net.pzdcrp.Hyperborea.world.elements.blocks.Block;
import net.pzdcrp.Hyperborea.world.elements.entities.Entity;
import net.pzdcrp.Hyperborea.world.elements.entities.ItemEntity;
import net.pzdcrp.Hyperborea.world.elements.generators.DefaultWorldGenerator;
import net.pzdcrp.Hyperborea.world.elements.generators.Noise;

public class Column {
	public List<Entity> entites = new CopyOnWriteArrayList<>();
	public Vector2I pos;
	public Chunk[] chunks = new Chunk[PlayerWorld.chunks];
	private Vector3 center;
	private Vector3 dimensions;
	protected int[][] skylightlenght;
	
	public Column(int x, int z, boolean gen) {
		this(new Vector2I(x,z), gen);
	}
	
	public Column(Vector2I cords, boolean gen) {
		this.pos = cords;
		//System.out.println("new col: "+cords.toString());
		skylightlenght = new int[16][16];
		
		center = new Vector3(pos.x*16+8,PlayerWorld.maxheight/2,pos.z*16+8);
		dimensions = new Vector3(16, PlayerWorld.maxheight, 16);
		for (int y = 0; y < PlayerWorld.chunks; y++) {
			chunks[y] = new Chunk(this, y*16);
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
		for (int y = PlayerWorld.buildheight; y >= 0; y--) {
			if (!(getBlock(x,y,z) instanceof Air)) {
				skylightlenght[x][z] = y;
				return;
			}
		}
	}
	
	public Block getBlock(int x, int y, int z) {
		Chunk c = chunks[y/16];
		return c.getBlock(x,y&15,z);
	}
	
	public int getBlocki(int x, int y, int z) {
		Chunk c = chunks[y/16];
		return c.getBlocki(x,y&15,z);
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
	
	public void setBlock(Block b) {
		//System.out.println(y/16+" "+y);
		int x = (int)b.pos.x&15, z = (int)b.pos.z&15, y = (int)b.pos.y;
		Chunk c = chunks[y/16];
		c.setBlock(x,y&15,z, b);
		int before = skylightlenght[x][z];
		//System.out.println("n: "+b.pos.y+"b: "+before);
		if (b.pos.y >= before) {
			//System.out.println("updating Multipile chunks");
			recalculateSLMD(x,z);
			/*int after = skylightlenght[x][z];
			
			int miny = Math.min(after, before);
		    int maxy = Math.max(after, before);
		    
		    int cmin = miny / 16;
		    int cmax = maxy / 16;
		    
		    for (int i = cmin; i <= cmax; i++) {
		    	System.out.println("updating chunk: "+i*16+"-"+(i*16+16));
				chunks[i].updateLight();
		    }*/
		}
	}
	
	private void updateModel() {
		for (int i = 0; i < PlayerWorld.chunks; i++) {
			chunks[i].updateModel();
		}
	}
	
	public void tick() {
		for (Entity entity : this.entites) {
			entity.tick();
		}
		for (Chunk chunk : chunks) {
			chunk.tick();
		}
	}
	
	public void renderEntites() {
		for (Entity entity : this.entites) {
			if (entity.type != EntityType.player) {
				entity.render();
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
		//blocks
		JsonArray blocks = new JsonArray();
		for (int px = 0; px < 16; px++) {
	        for (int py = 0; py < PlayerWorld.maxheight; py++) {
	            for (int pz = 0; pz < 16; pz++) {
	            	blocks.add(getBlocki(px,py,pz));
	            }
	        }
	    }
		jcol.add("blocks", blocks);
		//entities
		jcol.add("entities", new JsonArray());
		for (Entity entity : entites) {
			JsonObject jen = new JsonObject();
			entity.getJson(jen);
			jcol.get("entities").getAsJsonArray().add(jen);
		}
		
		return jcol;
	}
	
	public void fromJson(JsonObject jcol) {
		Vector2I cc = Vector2I.fromString(jcol.get("pos").getAsString());
		if (!this.pos.equals(cc)) {
			GameU.end("корды не сходятся удаляй регион");
		}
		
		//blocks
		int i = 0;
		JsonArray blocks = jcol.get("blocks").getAsJsonArray();
		for (int px = 0; px < 16; px++) {
			for (int py = 0; py < PlayerWorld.maxheight; py++) {
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
					Hpb.world.player = new Player(pos.x,pos.y,pos.z,jen.get("name").getAsString());
					entity = Hpb.world.player;
					System.out.println("loading player");
				} else if (type == 2) {
					entity = new ItemEntity(pos);
				} else {
					throw new Exception("unknown entity id");
				}
				entity.fromJson(jen);
				entites.add(entity);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("ignore entity error");
			}
		}
		updateSLMDForAll();
		
	}
}