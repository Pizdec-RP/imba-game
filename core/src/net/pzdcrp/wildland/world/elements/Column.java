package net.pzdcrp.wildland.world.elements;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Mesh;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.pzdcrp.wildland.GameInstance;
import net.pzdcrp.wildland.data.Vector2I;
import net.pzdcrp.wildland.data.Vector3D;
import net.pzdcrp.wildland.player.Player;
import net.pzdcrp.wildland.data.AABB;
import net.pzdcrp.wildland.data.EntityType;
import net.pzdcrp.wildland.utils.MathU;
import net.pzdcrp.wildland.world.World;
import net.pzdcrp.wildland.world.elements.blocks.Block;
import net.pzdcrp.wildland.world.elements.deGenerator.Noise;
import net.pzdcrp.wildland.world.elements.entities.Entity;

public class Column {
	public List<Entity> entites = new CopyOnWriteArrayList<>();
	public Vector2I pos;
	public Chunk[] chunks = new Chunk[World.chunks];
	public boolean flat = false;
	
	public Column(int x, int z, boolean gen) {
		this(new Vector2I(x,z), gen);
	}
	
	public Column(Vector2I cords, boolean gen) {
		this.pos = cords;
		for (int y = 0; y < World.chunks; y++) {
			chunks[y] = new Chunk(this, y*16);
		}
		if (gen) generate();
		else {
			System.out.println("nogen "+cords.toString());
		}
		//if (coords.columnX != 0 && coords.columnZ != 0) genrandom();
		updateModel();
	}
	
	public void generate() {
	    for (int px = 0; px < World.chunkWidht; px++) {
	        for (int pz = 0; pz < World.chunkWidht; pz++) {
	        	double noise = Noise.get((World.chunkWidht*pos.x+px)*0.05f, 10, (World.chunkWidht*pos.z+pz)*0.05f);
	        	int maxy = (int) (noise * (World.maxHeight*0.5));
	        	for (int py = 0; py < World.maxHeight; py++) {
	        		if (flat) {
	        			if (py < 20) {
		                	fastSetBlock(px,py,pz,1);
		                } else {
		                	fastSetBlock(px,py,pz,0);
		                }
	        		} else {
		        		if (py == 0) {
		        			fastSetBlock(px,py,pz,6);
		        		} else if (py < maxy) {
		        			fastSetBlock(px,py,pz,1);
		        		} else if (py == maxy) {
		        			fastSetBlock(px,py,pz,6);
		        		} else {
		        			fastSetBlock(px,py,pz,0);
		        		}
	        		}
	            }
	        }
	    }
	}
	
	public Block getBlock(int x, int y, int z) {
		Chunk c = chunks[y/World.chunkWidht];
		return c.getBlock(x,y&World.chunkWidht-1,z);
	}
	
	public void fastSetBlock(int x ,int y,int z, int id) {
		Chunk c = chunks[y/World.chunkWidht];
		c.setBlock(x,y&World.chunkWidht-1,z, id);
	}
	
	public void setBlock(int x ,int y,int z, Block b) {
		Chunk c = chunks[y/World.chunkWidht];
		c.setBlock(x,y&World.chunkWidht-1,z, b);
		c.updateModel();
	}
	
	public void updateModel()  {
		for (int i = 0; i < World.chunks; i++) {
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
				entity.render(GameInstance.modelBatch);
			}
		}
	}
	
	public void renderNormal() {
		if (chunks.length != 0) {
			for (Chunk chunk : chunks) {
				if (!chunk.checkCamFrustum()) return;
				chunk.render();
				if (chunk.allModels != null) {
					GameInstance.modelBatch.render(chunk.allModels, GameInstance.world.env);
				}
			}
		}
	}
	
	public void renderTransparent() {
		if (chunks.length != 0) {
			for (Chunk chunk : chunks) {
				if (!chunk.checkCamFrustum()) return;
				if (chunk.transparent != null) {
					GameInstance.modelBatch.render(chunk.transparent, GameInstance.world.env);
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
		for (int px = 0; px < World.chunkWidht; px++) {
	        for (int py = 0; py < World.maxHeight; py++) {
	            for (int pz = 0; pz < World.chunkWidht; pz++) {
	                blocks.add(Block.idByBlock(getBlock(px,py,pz)));
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
			jen.addProperty("vel", entity.velX+" "+entity.velY+" "+entity.velZ);
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
		System.out.println("loading blocks: "+blocks.size()+" pos: "+pos.toString());
		for (int px = 0; px < World.chunkWidht; px++) {
	        for (int py = 0; py < World.maxHeight; py++) {
	            for (int pz = 0; pz < World.chunkWidht; pz++) {
	            	try {
		                fastSetBlock(px, py, pz, blocks.get(i).getAsInt());
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
				GameInstance.world.player = new Player(pos.x,pos.y,pos.z);
				entity = GameInstance.world.player;
				System.out.println("loading player");
			} else {
				AABB hb = AABB.fromString(jen.get("hitbox").getAsString());
				entity = new Entity(pos, hb, type);
			}
			String[] jvel = jen.get("vel").getAsString().split(" ");
			entity.velX = Double.parseDouble(jvel[0]);
			entity.velY = Double.parseDouble(jvel[1]);
			entity.velZ = Double.parseDouble(jvel[2]);
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
	}
}