package net.pzdcrp.Hyperborea.world.elements;

import java.nio.FloatBuffer;
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
import com.badlogic.gdx.utils.BufferUtils;

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.MBIM;
import net.pzdcrp.Hyperborea.data.Pair;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.utils.ModelUtils;
import net.pzdcrp.Hyperborea.world.World;
import net.pzdcrp.Hyperborea.world.elements.blocks.Block;
import net.pzdcrp.Hyperborea.world.elements.blocks.Block.BlockType;

public class Chunk {
	private Block[][][] blocks = new Block[World.chunkWidht][World.chunkWidht][World.chunkWidht];
	public int[][][] light = new int[World.chunkWidht][World.chunkWidht][World.chunkWidht];//0-20
	public ModelInstance allModels;
	public ModelInstance transparent;
	public Column motherCol;
	public BoundingBox box;
	public int height;
	private boolean requestUpdate = false;
	public boolean tickable = false;
	
	public Chunk(Column motherCol, int height) {
		this.height = height;
		this.motherCol = motherCol;
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
					try {
						Block clas = blocks[xx][yy][zz];
						if (clas.tickable()) this.tickable = true;
						if (clas.isRenderable()) {
							boolean n1,n2,n3,n4,n5,n6;
							n1 = antirender(xx,yy+1,zz, clas);
							n2 = antirender(xx,yy-1,zz, clas);
							n3 = antirender(xx-1,yy,zz, clas);//left -x
							n4 = antirender(xx+1,yy,zz, clas);
							n5 = antirender(xx,yy,zz-1, clas);
							n6 = antirender(xx,yy,zz+1, clas);
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
		FloatBuffer buffer = BufferUtils.newFloatBuffer(16 * 16 * 16);
		
		for (int i = 0; i < 16; i++) {
		    for (int j = 0; j < 16; j++) {
		        for (int k = 0; k < 16; k++) {
		            buffer.put(light[i][j][k]);
		        }
		    }
		}
		allModels.userData = new Object[] {"c", "chunk", buffer};
		
		
		transparent = ModelUtils.combineModels(transparents);
		transparent.userData = new Object[] {"c"};
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
			requestUpdate = false;
			Hpb.world.isCycleFree = false;
		}
	}
	
	public boolean antirender(int x, int y, int z, Block current) {//false = рендерится
		if (this.height+y < 0) {
			return true;
		}
		Block b = Hpb.world.getBlock(new Vector3D(motherCol.pos.x*World.chunkWidht+x,this.height+y,motherCol.pos.z*World.chunkWidht+z));
		
		if (b.getType() == BlockType.air) {
			return false;
		}
		if (b.getType() == BlockType.transparent) {
			if (current.getClass() == b.getClass()) return true;
			return false;
		}
		return true;
	}

	public Block getBlock(int x, int y, int z) {
		return blocks[x][y][z];
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
	
	/*public boolean checkCamFrustum() {
		return GameInstance.world.player.cam.cam.frustum.boundsInFrustum(center, dimensions);
	}*/

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