package net.pzdcrp.wildland.world.elements;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import net.pzdcrp.wildland.GameInstance;
import net.pzdcrp.wildland.data.ColCoords;
import net.pzdcrp.wildland.data.Vector3D;
import net.pzdcrp.wildland.utils.ModelUtils;
import net.pzdcrp.wildland.world.World;
import net.pzdcrp.wildland.world.elements.blocks.Air;
import net.pzdcrp.wildland.world.elements.blocks.Block;
import net.pzdcrp.wildland.world.elements.blocks.Dirt;
import net.pzdcrp.wildland.world.elements.blocks.Stone;
import net.pzdcrp.wildland.world.elements.blocks.Block.BlockType;
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
		updateModel();
	}
	
	public void generate() {
	    for (int px = 0; px < World.chunkWidht; px++) {
	        for (int pz = 0; pz < World.chunkWidht; pz++) {
	        	double noise = Noise.get((World.chunkWidht*coords.columnX+px)*0.01f, 0, (World.chunkWidht*coords.columnZ+pz)*0.01f);
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
		Chunk c = chunks[y/16];
		return c.getBlock(x,y&15,z);
	}
	
	public void setBlock(int x ,int y,int z, int id) {
		Chunk c = chunks[y/16];
		c.setBlock(x,y&15,z, id);
		c.updateModel();
	}
	
	public void fastSetBlock(int x ,int y,int z, int id) {
		Chunk c = chunks[y/16];
		c.setBlock(x,y&15,z, id);
	}
	
	public void updateModel()  {
		for (int i = 0; i < World.chunks; i++) {
			chunks[i].updateModel();
		}
	}
	
	public void tick() {
		
	}
	
	public void render() {
		if (chunks.length != 0) {
			//List<Model> models = new ArrayList<>();
			for (Chunk chunk : chunks) {
				chunk.render();
				if (chunk.allModels != null && chunk.checkCamFrustum()) GameInstance.modelBatch.render(chunk.allModels, GameInstance.world.env);
			}
			//return models;
		}
		//return null;
	}
}
