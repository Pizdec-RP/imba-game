package net.pzdcrp.game.world.elements;

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

import net.pzdcrp.game.GameInstance;
import net.pzdcrp.game.data.Vector3D;
import net.pzdcrp.game.utils.ModelUtils;
import net.pzdcrp.game.world.World;
import net.pzdcrp.game.world.elements.blocks.Block;
import net.pzdcrp.game.world.elements.blocks.Block.BlockType;

public class Chunk {
	public int[][][] blocks = new int[World.chunkWidht][World.chunkWidht][World.chunkWidht];
	public ModelInstance allModels;
	public Column motherCol;
	public BoundingBox box;
	public int height;
	public boolean requestUpdate = false;
	//public Mesh mesh;
	public Vector3 min;
	public Vector3 max;
	
	public Chunk(Column motherCol, int height) {
		this.height = height;
		this.motherCol = motherCol;
		min = new Vector3(motherCol.coords.columnX*16+8,height+8,motherCol.coords.columnZ*16+8);
		max = new Vector3(16, 16, 16);
		/*box = new BoundingBox(
			new Vector3(motherCol.coords.columnX*16+8,height+8,motherCol.coords.columnZ*16+8),
			new Vector3(motherCol.coords.columnX*16+16, height+16, motherCol.coords.columnX*16+16)
		);*/
		//System.out.println("colcords: "+motherCol.coords.toString()+" y: "+height+" "+box.toString());
	}
	
	public void updateModel() {
		requestUpdate = true;
	}

	private void lUpdateModel() {
		Map<Integer, Pair> modelsById = new HashMap<>();
		for(int xx = 0; xx < World.chunkWidht; xx++) {
			for(int yy = 0; yy < World.chunkWidht; yy++) {
				for(int zz = 0; zz < World.chunkWidht; zz++) {
					int id = blocks[xx][yy][zz];
					Class<? extends Block> block = Block.blocks.get(id);
					try {
						Block clas = (Block)block.getConstructor(Vector3D.class).newInstance(new Vector3D(motherCol.coords.columnX*16+xx,height+yy,motherCol.coords.columnZ*16+zz));
						if (clas.isRenderable()) {
							if (!modelsById.containsKey(id)) {
								ModelBuilder mb = new ModelBuilder();
								mb.begin();
								modelsById.put(
										id,
										new Pair(
												mb.part(
													"cube", 
													GL20.GL_TRIANGLES, 
													VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates,
													new Material(
												    		TextureAttribute.createDiffuse(GameInstance.getTexture(clas.texture)),
											    			IntAttribute.createCullFace(GL20.GL_FRONT),
											    			new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
												    )
												),
												mb
										)
								);
							}
							
							boolean n1,n2,n3,n4,n5,n6;
							n1 = antirender(xx,yy+1,zz, clas.getType());
							n2 = antirender(xx,yy-1,zz, clas.getType());
							n3 = antirender(xx-1,yy,zz, clas.getType());//left -x
							n4 = antirender(xx+1,yy,zz, clas.getType());
							n5 = antirender(xx,yy,zz-1, clas.getType());
							n6 = antirender(xx,yy,zz+1, clas.getType());
							if (!n1 || !n2 || !n3 || !n4 || !n5 || !n6) {
								Pair pair = modelsById.get(id);
								ModelUtils.addCubeModel(n1, n2, n3, n4, n5, n6, pair.mpb, clas.pos.add(0.5).translate());
								//System.out.println(clas.pos.translate().toString());
								//System.out.println(clas.pos.toString());
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						System.exit(0);
					}
				}
			}
		}
		List<Model> models = new ArrayList<>();
		for (Pair pair : modelsById.values()) {
			models.add(pair.mb.end());
		}
		allModels = ModelUtils.combineModels(models);
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
		Block b = GameInstance.world.getBlock(new Vector3D(motherCol.coords.columnX*World.chunkWidht+x,this.height+y,motherCol.coords.columnZ*World.chunkWidht+z));
		
		if (b.getType() == BlockType.air || b.getType() == BlockType.Void) {
			return false;
		}
		return true;
	}

	public int getBlock(int x, int y, int z) {
		return blocks[x][y][z];
	}

	public void setBlock(int x, int y, int z, int i) {
		blocks[x][y][z] = i;
	}
	public boolean test = false;
	public boolean checkCamFrustum() {
		return World.player.cam.cam.frustum.boundsInFrustum(min, max);
		//return World.player.cam.cam.frustum.sphereInFrustum((min.x + max.x) / 2f, (min.y + max.y) / 2f, (min.z + max.z) / 2f, box.getDimensions(GameInstance.forAnyReason).len() / 2f);
		//return test;
	}
}

class Pair {
	public final MeshPartBuilder mpb;
	public final ModelBuilder mb;
	public Pair(MeshPartBuilder one, ModelBuilder two) {
		this.mpb = one;
		this.mb = two;
	}
}
