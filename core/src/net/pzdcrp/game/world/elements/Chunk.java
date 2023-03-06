package net.pzdcrp.game.world.elements;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
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
		//Map<Integer, Model> modelsById = new HashMap<>();
		
		ModelBuilder modelBuilder = new ModelBuilder();
		modelBuilder.begin();
		for(int xx = 0; xx < World.chunkWidht; xx++) {
			for(int yy = 0; yy < World.chunkWidht; yy++) {
				for(int zz = 0; zz < World.chunkWidht; zz++) {
					int id = blocks[xx][yy][zz];
					//Model m = 
					/*for (Entry<Integer, Model> entry : modelsById.entrySet()) {
						if ()
					}*/
					Class<? extends Block> block = Block.blocks.get(id);
					try {
						Block clas = (Block)block.getConstructor(Vector3D.class).newInstance(new Vector3D(motherCol.coords.columnX*16+xx,height+yy,motherCol.coords.columnZ*16+zz));
						if (clas.isRenderable()) {
							boolean n1,n2,n3,n4,n5,n6;
							n1 = antirender(xx,yy+1,zz, clas.getType());
							n2 = antirender(xx,yy-1,zz, clas.getType());
							n3 = antirender(xx-1,yy,zz, clas.getType());//left -x
							n4 = antirender(xx+1,yy,zz, clas.getType());
							n5 = antirender(xx,yy,zz-1, clas.getType());
							n6 = antirender(xx,yy,zz+1, clas.getType());
							/*if (clas.pos.VecToInt().equals(new Vector3D(10,19,11).VecToInt())) {
								System.out.println(n1+" "+n2+" "+n3+" "+n4+" "+n5+" "+n6);
								System.out.println(new Vector3D(motherCol.coords.columnX*World.chunkWidht+xx,this.height+yy,motherCol.coords.columnX*World.chunkWidht+zz));
								System.out.println(GameInstance.world.getBlock(new Vector3D(motherCol.coords.columnX*World.chunkWidht+xx,this.height+yy+1,motherCol.coords.columnX*World.chunkWidht+zz)).getType());
								System.out.println(GameInstance.world.getBlock(new Vector3D(motherCol.coords.columnX*World.chunkWidht+xx,this.height+yy-1,motherCol.coords.columnX*World.chunkWidht+zz)).getType());
								System.out.println(GameInstance.world.getBlock(new Vector3D(motherCol.coords.columnX*World.chunkWidht+xx-1,this.height+yy,motherCol.coords.columnX*World.chunkWidht+zz)).getType());
								System.out.println(GameInstance.world.getBlock(new Vector3D(motherCol.coords.columnX*World.chunkWidht+xx+1,this.height+yy,motherCol.coords.columnX*World.chunkWidht+zz)).getType());
								System.out.println(GameInstance.world.getBlock(new Vector3D(motherCol.coords.columnX*World.chunkWidht+xx,this.height+yy,motherCol.coords.columnX*World.chunkWidht+zz-1)).getType());
								System.out.println(GameInstance.world.getBlock(new Vector3D(motherCol.coords.columnX*World.chunkWidht+xx,this.height+yy,motherCol.coords.columnX*World.chunkWidht+zz+1)).getType());
							}*/
							if (!n1 || !n2 || !n3 || !n4 || !n5 || !n6) {
								Model model;
								model = ModelUtils.createCubeModel(n1,n2,n3,n4,n5,n6,
									clas.texture,
									clas.getType().equals(BlockType.glass)
								);
								
								Matrix4 transform = new Matrix4();
								transform.translate((float)(clas.pos.x+clas.xsize/2),(float)clas.pos.y,(float)(clas.pos.z+clas.zsize/2));
								for (Mesh mesh : model.meshes) {
								    mesh.transform(transform);
								}
								
								int i = 0;
								for (MeshPart mesh : model.meshParts) {
									//System.out.println("mesh: "+i);
								    modelBuilder.part(mesh, model.materials.get(i));
								    i++;
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						System.exit(0);
					}
				}
			}
		}
		allModels = new ModelInstance(modelBuilder.end());
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
