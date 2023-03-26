package net.pzdcrp.wildland.world.elements;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Mesh;

import net.pzdcrp.wildland.GameInstance;
import net.pzdcrp.wildland.data.ColCoords;
import net.pzdcrp.wildland.utils.MathU;
import net.pzdcrp.wildland.world.World;
import net.pzdcrp.wildland.world.elements.deGenerator.Noise;
import net.pzdcrp.wildland.world.elements.entities.Entity;

public class Column {
	public List<Entity> entites = new CopyOnWriteArrayList<>();
	public ColCoords coords;
	public Chunk[] chunks = new Chunk[World.chunks];
	public boolean flat = false;
	
	public Column(int x, int z, boolean gen) {
		this(new ColCoords(x,z), gen);
	}
	
	public Column(ColCoords cords, boolean gen) {
		this.coords = cords;
		for (int y = 0; y < World.chunks; y++) {
			chunks[y] = new Chunk(this, y*16);
		}
		if (gen) generate();
		//if (coords.columnX != 0 && coords.columnZ != 0) genrandom();
		updateModel();
	}
	
	public void genrandom() {
		for (int px = 0; px < World.chunkWidht; px++) {
	        for (int pz = 0; pz < World.chunkWidht; pz++) {
	        	for (int py = 0; py < World.maxHeight; py++) {
	        		fastSetBlock(px,py,pz,MathU.rnd(0, 3));
	        	}
	        }
		}
	}
	
	public void generate() {
	    for (int px = 0; px < World.chunkWidht; px++) {
	        for (int pz = 0; pz < World.chunkWidht; pz++) {
	        	double noise = Noise.get((World.chunkWidht*coords.columnX+px)*0.05f, 10, (World.chunkWidht*coords.columnZ+pz)*0.05f);
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
	
	public int getBlock(int x, int y, int z) {
		Chunk c = chunks[y/World.chunkWidht];
		return c.getBlock(x,y&World.chunkWidht-1,z);
	}
	
	public void setBlock(int x ,int y,int z, int id) {
		Chunk c = chunks[y/World.chunkWidht];
		c.setBlock(x,y&World.chunkWidht-1,z, id);
		c.updateModel();
	}
	
	public void fastSetBlock(int x ,int y,int z, int id) {
		Chunk c = chunks[y/World.chunkWidht];
		c.setBlock(x,y&World.chunkWidht-1,z, id);
	}
	
	public void updateModel()  {
		for (int i = 0; i < World.chunks; i++) {
			chunks[i].updateModel();
		}
	}
	
	public void tick() {
		
	}
	
	
	public static final boolean testshader = false;
	public void renderNormal() {
		if (chunks.length != 0) {
			for (Chunk chunk : chunks) {
				if (!chunk.checkCamFrustum()) return;
				chunk.render();
				if (chunk.allModels != null) {
					if (testshader) {
						GameInstance.fboDLight.begin();
						
						Gdx.gl.glViewport(0, 0, 1024, 1024);
						Gdx.gl.glClearColor(0, 0, 0, 0);
						Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT|GL30.GL_DEPTH_BUFFER_BIT);
						GameInstance.modelBatch.begin(GameInstance.dLightCam);
						GameInstance.modelBatch.render(chunk.allModels, GameInstance.dLightSP);
						GameInstance.modelBatch.end();
						
						GameInstance.fboDLight.end();
						
						
						GameInstance.depthMap = GameInstance.fboDLight.getColorBufferTexture();
						
						GameInstance.SP.setDepthMap(GameInstance.depthMap);
						GameInstance.SP.setLightViewProj(GameInstance.dLightCam);
						
						GameInstance.modelBatch.begin(GameInstance.world.player.cam.cam);
						GameInstance.modelBatch.render(chunk.allModels, GameInstance.SP);
						GameInstance.modelBatch.end();
					} else {
						GameInstance.modelBatch.render(chunk.allModels, GameInstance.world.env);
						GameInstance.modelBatch.render(chunk.allModels, GameInstance.world.env);
					}
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
}