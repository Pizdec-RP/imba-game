package net.pzdcrp.wildland.world.elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import net.pzdcrp.wildland.GameInstance;
import net.pzdcrp.wildland.data.MBIM;
import net.pzdcrp.wildland.data.Pair;
import net.pzdcrp.wildland.data.Vector3D;
import net.pzdcrp.wildland.utils.ModelUtils;
import net.pzdcrp.wildland.world.World;
import net.pzdcrp.wildland.world.elements.blocks.Block;
import net.pzdcrp.wildland.world.elements.blocks.Block.BlockType;

public class Chunk {
	private Block[][][] blocks = new Block[World.chunkWidht][World.chunkWidht][World.chunkWidht];
	public int[][][] light = new int[World.chunkWidht][World.chunkWidht][World.chunkWidht];
	public ModelInstance allModels;
	public ModelInstance transparent;
	public Column motherCol;
	public BoundingBox box;
	public int height;
	public boolean requestUpdate = false;
	//public Mesh mesh;
	public Vector3 min;
	public Vector3 max;
	public boolean tickable = false;
	
	public Chunk(Column motherCol, int height) {
		this.height = height;
		this.motherCol = motherCol;
		min = new Vector3(motherCol.pos.x*16+8,height+8,motherCol.pos.z*16+8);
		max = new Vector3(16, 16, 16);
	}
	
	public void updateModel() {
		requestUpdate = true;
	}

	private void lUpdateModel() {
		Map<String, Pair> modelsById = new HashMap<>();
		MBIM m = new MBIM(modelsById);
		for(int xx = 0; xx < World.chunkWidht; xx++) {
			for(int yy = 0; yy < World.chunkWidht; yy++) {
				for(int zz = 0; zz < World.chunkWidht; zz++) {
					int id = getBlockId(xx,yy,zz);
					try {
						Block clas = Block.blockById(id, new Vector3D(motherCol.pos.x*16+xx,height+yy,motherCol.pos.z*16+zz));
						if (clas.isRenderable()) {
							boolean n1,n2,n3,n4,n5,n6;
							n1 = antirender(xx,yy+1,zz, clas.getType());
							n2 = antirender(xx,yy-1,zz, clas.getType());
							n3 = antirender(xx-1,yy,zz, clas.getType());//left -x
							n4 = antirender(xx+1,yy,zz, clas.getType());
							n5 = antirender(xx,yy,zz-1, clas.getType());
							n6 = antirender(xx,yy,zz+1, clas.getType());
							clas.addModel(n1,n2,n3,n4,n5,n6,m);
						}
					} catch (Exception e) {
						e.printStackTrace();
						System.exit(0);
					}
				}
			}
		}
		List<Model> models = new ArrayList<>();
		List<Model> transparents = new ArrayList<>();
		for (Entry<String, Pair> entry : modelsById.entrySet()) {
			if (entry.getKey().startsWith("tr:")) {
				transparents.add(entry.getValue().mb.end());
			} else {
				models.add(entry.getValue().mb.end());
			}
		}
		allModels = ModelUtils.combineModels(models);
		transparent = ModelUtils.combineModels(transparents);
		if (allModels.model.meshes.size > 1) {
			MeshBuilder builder = new MeshBuilder();
			builder.begin(VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
			int i = 0;
			for (Mesh mesh : allModels.model.meshes) {
				builder.addMesh(mesh);
				allModels.model.meshes.removeIndex(i);
				i++;
			}
			Mesh m1 = builder.end();
			allModels.model.meshes.add(m1);
		}
		
		
	}
	
	public void render() {
		if (requestUpdate) {
			lUpdateModel();
			//System.out.println("updating");
			requestUpdate = false;
		}
	}
	
	public boolean antirender(int x, int y, int z, BlockType current) {//false - need
		if (this.height+y < 0) {
			return true;
		}
		Block b = GameInstance.world.getBlock(new Vector3D(motherCol.pos.x*World.chunkWidht+x,this.height+y,motherCol.pos.z*World.chunkWidht+z));
		
		if (b.getType() == BlockType.air || b.getType() == BlockType.Void) {
			return false;
		}
		if (b.getType() == BlockType.glass) {
			if (current == BlockType.glass) return true;
			return false;
		}
		return true;
	}

	public Block getBlock(int x, int y, int z) {
		return blocks[x][y][z];
	}
	
	public int getBlockId(int x, int y, int z) {
		Block b = blocks[x][y][z];
		if (b == null) return 5;//Voed
		return Block.idByBlock(b);
	}

	public void setBlock(int x, int y, int z, int i) {//xyz = 0-15
		setBlock(x,y,z,Block.blockById(i, new Vector3D(motherCol.pos.x*16+x,height+y,motherCol.pos.z*16+z)));
	}
	
	public void setBlock(int x, int y, int z, Block i) {
		blocks[x][y][z] = i;
		if (i.tickable()) {
			this.tickable = true;
		}
	}
	
	public boolean checkCamFrustum() {
		return GameInstance.world.player.cam.cam.frustum.boundsInFrustum(min, max);
	}

	public void tick() {
		if (!this.tickable) return;
		for (Block[][] blocka : blocks) {
			for (Block[] blockaa : blocka) {
				for (Block block : blockaa) {
					block.tick();
				}
			}
		}
	}
}