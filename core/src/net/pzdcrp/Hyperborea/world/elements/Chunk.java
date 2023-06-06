package net.pzdcrp.Hyperborea.world.elements;

import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.collision.BoundingBox;
import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.MBIM;
import net.pzdcrp.Hyperborea.data.Vector2I;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.data.Vector3I;
import net.pzdcrp.Hyperborea.world.World;
import net.pzdcrp.Hyperborea.world.elements.blocks.Air;
import net.pzdcrp.Hyperborea.world.elements.blocks.Block;
import net.pzdcrp.Hyperborea.world.elements.blocks.Block.BlockType;

public class Chunk {
	private Block[][][] blocks = new Block[16][16][16];
	private int[][][] light = new int[16][16][16];//0-14
	public ModelInstance allModels;
	//public ModelInstance transparent;
	public Column column;
	public BoundingBox box;
	public int height;
	private boolean reqmodelupd = false, reqlightupd = false;;
	public boolean tickable = false;
	public MBIM m;
	
	public Chunk(Column motherCol, int height) throws Exception {
		this.height = height;
		this.column = motherCol;
		for(int xx = 0; xx < 16; xx++) {
			for(int yy = 0; yy < 16; yy++) {
				for(int zz = 0; zz < 16; zz++) {
					blocks[xx][yy][zz] = new Air(new Vector3D(normx(xx),normy(yy),normz(zz)));
					
				}
			}
		}
	}
	
	public void updateLight() {
		reqlightupd = true;
	}
	
	public void updateModel() {
		reqmodelupd = true;
	}
	
	//корды внутри чанка
	public int rawGetLight(int x, int y, int z) {
		if (x < 0 || x > 15 || y < 0 || y > 15 || z < 0 || z > 15) {
			return Hpb.world.getLight(normx(x), normy(y), normz(z));
			//return 0;
		}
		return light[x][y][z];
	}
	public void rawSetLight(int x, int y, int z, int num) {
		if (x < 0 || x > 15 || y < 0 || y > 15 || z < 0 || z > 15) {
			Hpb.world.setLight(normx(x), normy(y), normz(z), num);
			return;
		}
		if (light[x][y][z] != num) {
			light[x][y][z] = num;
		}
	}
	
	public int normy(int ref) {
		return ref+this.height;
	}
	
	public int normx(int ref) {
		return column.pos.x*16+ref;
	}
	
	public int normz(int ref) {
		return column.pos.z*16+ref;
	}
	
	private void updateLightFromOutbounds() {
	    //System.out.println("updating outbound light");
	    List<Vector3I> stack = new ArrayList<>();
	    for(int x = -1; x <= 16; x++) {
	        for(int z = -1; z <= 16; z++) {
	            for(int y = -1; y <= 16; y++) {
	            	if (x < 0 || x > 15 || y < 0 || y > 15 || z < 0 || z > 15) {
	            		if (rawGetLight(x,y,z) != 0) {
	            			stack.add(new Vector3I(x,y,z));
	            		}
	            	}
	            }
	        }
	    }
	    updateLightStack(stack);
	    //System.out.println("updated 2");
	}
	
	private void updateLight(boolean clearnotused) {
	    //System.out.println("updating light");
	    List<Vector3I> stack = new ArrayList<>();
	    //тут
	    for(int x = 0; x < 16; x++) {
	        for(int z = 0; z < 16; z++) {
	            int slmd = column.skylightlenght[x][z];
	            for(int y = 0; y < 16; y++) {
	            	if (normy(y) >= slmd || blocks[x][y][z].emitLight()) {
	                    Vector3I vector = new Vector3I(x,y,z);
	                    stack.add(vector);
	                    rawSetLight(x,y,z, 14);
	                } else if (clearnotused) {
	                	rawSetLight(x,y,z, 0);
	                }
	            }
	        }
	    }
	    updateLightStack(stack);
	    //System.out.println("updated");
	}
	
	public void updateLightStack(List<Vector3I> stack) {
		while (!stack.isEmpty()) {
	        Vector3I l = stack.remove(stack.size() - 1);
	        boolean b0 = false;
	        b0 = l.x < 0 || l.x > 15 || l.y < 0 || l.y > 15 || l.z < 0 || l.z > 15;
	        
	        Block b;
	        if (b0) {
	        	b = Hpb.world.getBlock(normx(l.x),normy(l.y),normz(l.z));
	        } else {
	        	b = blocks[l.x][l.y][l.z];
	        }
	        if (b == null) {
	        	System.out.println("nullblock: "+l.toString());
	        	System.exit(0);
	        }
	        if (b.isTransparent()) {
	            int cur = rawGetLight(l.x,l.y,l.z);
	            updateLight(l.x+1,l.y,l.z, cur, stack);
	            updateLight(l.x-1,l.y,l.z, cur, stack);
	            updateLight(l.x,l.y+1,l.z, cur, stack);
	            updateLight(l.x,l.y-1,l.z, cur, stack);
	            updateLight(l.x,l.y,l.z+1, cur, stack);
	            updateLight(l.x,l.y,l.z-1, cur, stack);
	        }
	    }
	}

	private void updateLight(int x, int y, int z, int currentLight, List<Vector3I> stack) {
	    if (x < 0 || x > 15 || y < 0 || y > 15 || z < 0 || z > 15) return;

	    int neighborLight = rawGetLight(x, y, z);
	    if (currentLight - 1 > neighborLight) {
	    	rawSetLight(x, y, z, currentLight - 1);
	        stack.add(new Vector3I(x, y, z));
	    }
	}
	
	private void lUpdateModel() throws Exception {
		if (!Thread.currentThread().getName().equals("main thd")) {
			throw new Exception("Wrong thread exception");
		}
		updateLight(true);
		updateLightFromOutbounds();
		//Map<String, Pair> modelsById = new HashMap<>();
		m = new MBIM(this);
		for(int xx = 0; xx < 16; xx++) {
			for(int yy = 0; yy < 16; yy++) {
				for(int zz = 0; zz < 16; zz++) {
					try {
						Block clas = blocks[xx][yy][zz];
						if (clas == null) {
							System.out.println("pizdec: "+normx(xx)+" "+normy(yy)+" "+normz(zz));
						}
						if (clas.tickable()) this.tickable = true;
						if (clas.isRenderable()) {
							boolean n1,n2,n3,n4,n5,n6;
							n1 = wr(xx,yy+1,zz, clas);
							n2 = wr(xx,yy-1,zz, clas);
							n3 = wr(xx-1,yy,zz, clas);//left -x
							n4 = wr(xx+1,yy,zz, clas);
							n5 = wr(xx,yy,zz-1, clas);
							n6 = wr(xx,yy,zz+1, clas);
							clas.addModel(n1,n2,n3,n4,n5,n6,m);
						}
					} catch (Exception e) {
						e.printStackTrace();
						System.exit(0);
					}
				}
			}
		}
		/*List<Model> models = new ArrayList<>();
		//List<Model> transparents = new ArrayList<>();
		for (Entry<String, Pair> entry : mbim) {
			if (entry.getKey().startsWith("tr:")) {
				//transparents.add(entry.getValue().mb.end());
			} else {
				models.add(entry.getValue().mb.end());
			}
		}*/
		allModels = m.end();//ModelUtils.combineModels(models);
		
		
		
		allModels.userData = new Object[] {"c "+column.pos.toString()+" y:"+height, "chunk", "ithaslight"};
		
		//transparent = ModelUtils.combineModels(transparents);
		//transparent.userData = new Object[] {"c"," transparent"};
		
		/*if (allModels.model.meshes.size > 1) {
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
		}*/
	}
	
	public void callFromRenderThread() throws Exception {
		if (reqmodelupd) {
			lUpdateModel();
			reqmodelupd = false;
			Hpb.world.isCycleFree = false;
		} else if (reqlightupd) {
			updateLight(true);
			updateLightFromOutbounds();
			reqlightupd = false;
			Hpb.world.isCycleFree = false;
		}
	}
	
	public boolean wr(int x, int y, int z, Block current) {//false = рендерится
		if (this.height+y < 0) {
			return true;
		}
		Block b = Hpb.world.getBlock(new Vector3D(column.pos.x*16+x,this.height+y,column.pos.z*16+z));
		
		if (b.getType() == BlockType.air) {
			return false;
		}
		if (b.getType() == BlockType.transparent) {
			if (current.getClass() == b.getClass()) return true;
			return false;
		}
		return true;
	}
	
	public Chunk[] sides() {
		List<Chunk> l = new ArrayList<>();
		if (height != 0) {
			l.add(column.chunks[height/16-1]);
		}
		if (height != 240) {
			l.add(column.chunks[height/16+1]);
		}
		Column temp;
		
		temp = Hpb.world.loadedColumns.get(new Vector2I(column.pos.x+1, column.pos.z));
		if (temp != null) {
			l.add(temp.chunks[height/16]);
		}
		
		temp = Hpb.world.loadedColumns.get(new Vector2I(column.pos.x-1, column.pos.z));
		if (temp != null) {
			l.add(temp.chunks[height/16]);
		}
		
		temp = Hpb.world.loadedColumns.get(new Vector2I(column.pos.x, column.pos.z+1));
		if (temp != null) {
			l.add(temp.chunks[height/16]);
		}
		
		temp = Hpb.world.loadedColumns.get(new Vector2I(column.pos.x, column.pos.z-1));
		if (temp != null) {
			l.add(temp.chunks[height/16]);
		}
		return l.toArray(new Chunk[0]);
	}

	public Block getBlock(int x, int y, int z) {
		return blocks[x][y][z];
	}

	public void setBlock(int x, int y, int z, int i) {//xyz = 0-15
		setBlock(x,y,z,Block.blockById(i, new Vector3D(column.pos.x*16+x,height+y,column.pos.z*16+z)));
	}
	
	public void setBlock(int x, int y, int z, Block i) {
		if (i == null) {
			System.out.println("pizdec null v setbloke");
			System.exit(0);
		}
		blocks[x][y][z] = i;
		if (i.tickable()) {
			this.tickable = true;
		}
	}
	
	/*public boolean checkCamFrustum() {
		return GameInstance.world.player.cam.cam.frustum.boundsInFrustum(center, dimensions);
	}*/

	public void tick() {//TODO clear all shit
		if (!this.tickable) return;
		for (Block[][] blocka : blocks) {
			for (Block[] blockaa : blocka) {
				for (Block block : blockaa) {
					try {
						block.tick();
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("pizdec at: "+block.pos.x+" "+block.pos.y+" "+block.pos.z);
						//System.exit(0);
					}
				}
			}
		}
	}
}