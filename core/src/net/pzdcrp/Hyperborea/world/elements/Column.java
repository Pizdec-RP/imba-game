package net.pzdcrp.Hyperborea.world.elements;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.AABB;
import net.pzdcrp.Hyperborea.data.EntityType;
import net.pzdcrp.Hyperborea.data.Vector2I;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.player.Player;
import net.pzdcrp.Hyperborea.utils.MathU;
import net.pzdcrp.Hyperborea.world.World;
import net.pzdcrp.Hyperborea.world.elements.blocks.Air;
import net.pzdcrp.Hyperborea.world.elements.blocks.Block;
import net.pzdcrp.Hyperborea.world.elements.deGenerator.Noise;
import net.pzdcrp.Hyperborea.world.elements.entities.Entity;
import net.pzdcrp.Hyperborea.world.elements.blocks.Water;
import noisy.Noisy;

public class Column {
	public List<Entity> entites = new CopyOnWriteArrayList<>();
	public Vector2I pos;
	public Chunk[] chunks = new Chunk[World.chunks];
	private Vector3 center;
	private Vector3 dimensions;
	static boolean flat = true;
	protected int[][] skylightmaxdown;
	
	public Column(int x, int z, boolean gen) throws Exception {
		this(new Vector2I(x,z), gen);
	}
	
	public Column(Vector2I cords, boolean gen) throws Exception {
		this.pos = cords;
		System.out.println("new col: "+cords.toString());
		//fill default
		skylightmaxdown = new int[16][16];
		//TODO remove
		for (int px = 0; px < 16; px++) {
	        for (int pz = 0; pz < 16; pz++) {
	        	skylightmaxdown[px][pz] = 16*World.chunks-1;
	        }
		}
		
		center = new Vector3(pos.x*16+8,16/2,pos.z*16+8);
		dimensions = new Vector3(16, 16, 16);
		for (int y = 0; y < World.chunks; y++) {
			chunks[y] = new Chunk(this, y*16);
		}
		
		if (gen) generate();
	}
	
	public void generate() {
	    for (int px = 0; px < 16; px++) {
	        for (int pz = 0; pz < 16; pz++) {
	        	if (flat) {
	        		for (int py = 0; py < 16; py++) {
	        			if (py < 10) {
	        				fastSetBlock(px,py,pz,6);
	        			} else {
	        				fastSetBlock(px,py,pz,0);
	        			}
	        		}
	        	} else {
	        		double noise = Noise.get((16*pos.x+px)*Hpb.world.player.x, Hpb.world.player.y, (16*pos.z+pz)*Hpb.world.player.x);
		        	//System.out.println(noise);
		        	int maxy = (int) ((noise+Hpb.world.player.z) * (16*0.5));
		        	for (int py = 0; py < 16; py++) {
		        		if (py == 0) {
		        			fastSetBlock(px,py,pz,6);
		        		} else if (py < maxy) {
		        			fastSetBlock(px,py,pz,1);
		        		} else if (py == maxy) {
		        			fastSetBlock(px,py,pz,6);
		        		} else {
		        			if (py < 3) {
		        				fastSetBlock(px,py,pz,18);
		        				Block block = getBlock(px,py,pz);
		        				((Water) block).ableToTick = false;
		        			} else {
		        				fastSetBlock(px,py,pz,0);
		        			}
		        		}
	        		}
	        	}
	        	recalculateSLMD(px,pz);
	        }
	    }
	    updateModel();
	}
	
	private void updateSLMDForAll() {
		for (int px = 0; px < 16; px++) {
	        for (int pz = 0; pz < 16; pz++) {
	        	recalculateSLMD(px,pz);
	        }
		}
	}
	
	private void recalculateSLMD(int x, int z) {
		//this.skylightmaxdown[x][z] = 10;
		for (int y = 16*World.chunks-1; y >= 0; y--) {
			if (!(getBlock(x,y,z) instanceof Air)) this.skylightmaxdown[x][z] = y+1; 
		}
	}
	
	public Block getBlock(int x, int y, int z) {
		Chunk c = chunks[y/16];
		return c.getBlock(x,y&15,z);
	}
	
	public void fastSetBlock(int x ,int y,int z, int id) {
		Chunk c = chunks[y/16];
		c.setBlock(x,y&15,z, id);
	}
	
	/*
	 *	x = 0-15
	 *	y = 0-World.chunkWidht
	 *	z = 0-15
	 */
	public void setBlock(int x ,int y,int z, Block b) {
		Chunk c = chunks[y/16];
		c.setBlock(x,y&15,z, b);
		c.updateModel();
		recalculateSLMD(x,z);
	}
	
	private void updateModel() {//TODO выключено хз зачем
		for (int i = 0; i < World.chunks; i++) {
			chunks[i].updateModel();
		}
	}
	
	public void tick() throws Exception {
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
	
	public void renderNormal() throws Exception {
		if (!Hpb.world.player.cam.cam.frustum.boundsInFrustum(center, dimensions)) return;
		if (chunks.length != 0) {
			if (Hpb.world.isCycleFree) {
				for (Chunk chunk : chunks) {
					chunk.callFromRenderThread();
				}
			}
			for (Chunk chunk : chunks) {
				if (chunk.allModels != null) {
					Hpb.render(chunk.allModels);
				} else {
					chunk.updateModel();
				}
			}
		}
	}
	
	/*public void renderTransparent() {
		if (!Hpb.world.player.cam.cam.frustum.boundsInFrustum(center, dimensions)) return;
		if (chunks.length != 0) {
			for (Chunk chunk : chunks) {
				if (chunk.transparent != null) {
					Hpb.render(chunk.transparent);
				}
			}
		}
	}*/
	
	public JsonObject toJson() {
		JsonObject jcol = new JsonObject();
		//pos
		jcol.addProperty("pos", pos.toString());
		//blocks
		JsonArray blocks = new JsonArray();
		for (int px = 0; px < 16; px++) {
	        for (int py = 0; py < 16; py++) {
	            for (int pz = 0; pz < 16; pz++) {
	            	Block block = getBlock(px,py,pz);
	            	JsonObject data = block.toJson();
	            	if (data != null) {
	            		blocks.add(data);
	            	} else {
	            		blocks.add(Block.idByBlock(block));
	            	}
	            }
	        }
	    }
		jcol.add("blocks", blocks);
		//entities
		jcol.add("entities", new JsonArray());
		for (Entity entity : entites) {
			JsonObject jen = new JsonObject();
			
			jen.addProperty("pos", entity.pos.toString());
			jen.addProperty("hitbox", entity.hitbox.toString());
			jen.addProperty("vel", entity.vel.toString());
			jen.addProperty("coldata", entity.colx+" "+entity.coly+" "+entity.colz);
			jen.addProperty("onGround", entity.onGround);
			jen.addProperty("yawpitch", entity.yaw+" "+entity.pitch);
			jen.addProperty("beforeechc", entity.beforeechc.toString());
			jen.addProperty("type", entity.type.toString());
			
			//addtolist
			jcol.get("entities").getAsJsonArray().add(jen);
			jen.add("custom", entity.getCustomProp());
		}
		
		return jcol;
	}
	
	public void fromJson(JsonObject jcol) {
		Vector2I cc = Vector2I.fromString(jcol.get("pos").getAsString());
		if (!this.pos.equals(cc)) {
			System.out.println("корды не сходятся удаляй регион");
			System.exit(0);
		}
		
		//blocks
		int i = 0;
		JsonArray blocks = jcol.get("blocks").getAsJsonArray();
		for (int px = 0; px < 16; px++) {
			for (int py = 0; py < 16; py++) {
				for (int pz = 0; pz < 16; pz++) {
	            	try {
		            	JsonElement rawblock = blocks.get(i);
		            	if (rawblock.isJsonPrimitive()) {
		            		fastSetBlock(px, py, pz, blocks.get(i).getAsInt());
		            	} else if (rawblock.isJsonObject()) {
		            		JsonObject data = blocks.get(i).getAsJsonObject();
		            		fastSetBlock(px, py, pz, data.get("id").getAsInt());
		            		getBlock(px, py, pz).fromJson(data);
		            	}
	            	} catch (Exception e) {
	            		e.printStackTrace();
	            		System.exit(0);
	            	}
	            	i++;
	            }
	        }
	    }
		//entities
		JsonArray entities = jcol.get("entities").getAsJsonArray();
		for (JsonElement jene : entities) {
			JsonObject jen = jene.getAsJsonObject();
			
			Vector3D pos = Vector3D.fromString(jen.get("pos").getAsString());
			EntityType type = EntityType.valueOf(jen.get("type").getAsString());
			Entity entity;
			if (type == EntityType.player) {
				Hpb.world.player = new Player(pos.x,pos.y,pos.z);
				entity = Hpb.world.player;
				System.out.println("loading player");
			} else {
				AABB hb = AABB.fromString(jen.get("hitbox").getAsString());
				entity = new Entity(pos, hb, type);
			}
			String jvel = jen.get("vel").getAsString();
			entity.vel = Vector3D.fromString(jvel);
			String[] jcoll = jen.get("coldata").getAsString().split(" ");
			entity.colx = Boolean.parseBoolean(jcoll[0]);
			entity.coly = Boolean.parseBoolean(jcoll[1]);
			entity.colz = Boolean.parseBoolean(jcoll[2]);
			entity.onGround = jen.get("onGround").getAsBoolean();
			String[] rot = jen.get("yawpitch").getAsString().split(" ");
			entity.setYaw(Float.parseFloat(rot[0]));
			entity.setPitch(Float.parseFloat(rot[1]));
			entity.beforeechc = Vector2I.fromString(jen.get("beforeechc").getAsString());
			entity.readCustomProp(jen.get("custom").getAsJsonObject());
			entites.add(entity);
		}
		updateSLMDForAll();
	}
}