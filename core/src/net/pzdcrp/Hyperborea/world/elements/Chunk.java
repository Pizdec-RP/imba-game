package net.pzdcrp.Hyperborea.world.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.collision.BoundingBox;
import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.BitStorage;
import net.pzdcrp.Hyperborea.data.MBIM;
import net.pzdcrp.Hyperborea.data.Vector2I;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.data.Vector3I;
import net.pzdcrp.Hyperborea.utils.MathU;
import net.pzdcrp.Hyperborea.world.World;
import net.pzdcrp.Hyperborea.world.elements.blocks.Air;
import net.pzdcrp.Hyperborea.world.elements.blocks.Block;
import net.pzdcrp.Hyperborea.world.elements.blocks.Block.BlockType;

public class Chunk {
	private Block[][][] blocks = new Block[16][16][16];
	public BitStorage light;
	
	
	//не хранимые данные
	public ModelInstance allModels;
	//public ModelInstance transparent;
	private Column column;
	public int height;
	public boolean reqmodelupd = true;
	public boolean inlightupd = true, outlightupd = true;
	private boolean tickable = false;
	private MBIM m;
	
	public Chunk(Column motherCol, int height) throws Exception {
		this.height = height;
		this.column = motherCol;
		this.light = new BitStorage(4, 4096);
		for(int xx = 0; xx < 16; xx++) {
			for(int yy = 0; yy < 16; yy++) {
				for(int zz = 0; zz < 16; zz++) {
					blocks[xx][yy][zz] = new Air(new Vector3D(normx(xx),normy(yy),normz(zz)));
				}
			}
		}
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
		return light.get(index(x,y,z));
	}
	public void rawSetLight(int x, int y, int z, int num) {
		if (x < 0 || x > 15 || y < 0 || y > 15 || z < 0 || z > 15) {
			Hpb.world.setLight(normx(x), normy(y), normz(z), num);
			return;
		}
		if (light.get(index(x,y,z)) != num) {
			light.set(index(x,y,z), num);
		}
	}
	
	public Vector3I norm(int x, int y, int z) {
		return new Vector3I(normx(x),normy(y),normz(z));
	}
	
	private int normy(int ref) {
		return ref+this.height;
	}
	
	private int normx(int ref) {
		return column.pos.x*16+ref;
	}
	
	private int normz(int ref) {
		return column.pos.z*16+ref;
	}
	public void updateLightFromOutbounds() {
		//System.out.println("OBlightupd: "+getPos().toString());
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
	    updateLightStack(stack, false);
	    //System.out.println("updated 2");
	}
	
	public void updateLightMain() {
	    //System.out.println("lightupd: "+getPos().toString());
	    List<Vector3I> stack = new ArrayList<>();
	    for(int x = 0; x < 16; x++) {
	        for(int z = 0; z < 16; z++) {
	            for(int y = 0; y < 16; y++) {
	            	rawSetLight(x,y,z, 0);
	            }
	        }
	    }
	    for(int x = 0; x < 16; x++) {
	        for(int z = 0; z < 16; z++) {
	            int slmd = column.skylightlenght[x][z];
	            for(int y = 0; y < 16; y++) {
	            	if (normy(y) >= slmd || blocks[x][y][z].emitLight()) {
	                    Vector3I vector = new Vector3I(x,y,z);
	                    stack.add(vector);
	                    rawSetLight(x,y,z, 14);
	                }
	            }
	        }
	    }
	    updateLightStack(stack, true);
	}
	
	private void updateLightStack(List<Vector3I> stack, boolean regnewupd) {
		while (!stack.isEmpty()) {
	        Vector3I l = stack.remove(stack.size() - 1);//он не будет больше 16 или меньше -1!!!
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
	    //обновление стороны от блока который мб на координатах +-1 от 0 или 15
		if (x < 0 || x > 15 || y < 0 || y > 15 || z < 0 || z > 15) return;
		int neighborLight = rawGetLight(x, y, z);
	    int newlight = currentLight - 1;
	    Vector3I vec = new Vector3I(x, y, z);
	    if (newlight > neighborLight) {
	    	rawSetLight(x, y, z, currentLight - 1);
	        stack.add(vec);
			/*if (x < 0 || x > 15 || y < 0 || y > 15 || z < 0 || z > 15) {
		    	if (regnewupd && newlight > 0 && rawGetLight(x,y,z) != newlight) {
		    		for (Chunk c : this.norm(x, y, z).getSidesChunks()) {
		    			if (c.updatePropogation.contains(c)) return;
		    			c.updatePropogation.add(this);
		    			c.inlightupd=true;
		    			c.outlightupd=true;
		    		}
		    	}
		    } else {
		    	rawSetLight(x, y, z, currentLight - 1);
		        stack.add(vec);
		    }*/
	    }
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
		
		//updateModel();
		inlightupd = true;
		//System.out.println("placed in: "+this.getPos());
		if (World.ready) {//TODO этот код полная хуйня
			for (Chunk c : sides()) {
				//System.out.println("side: "+c.getPos());
				c.inlightupd = true;
			}
		}
	}
	
	private void lUpdateModel() throws Exception {
		if (!Thread.currentThread().getName().equals("main thd")) {
			throw new Exception("Wrong thread exception");
		}
		
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
		allModels = m.end();//ModelUtils.combineModels(models);
		allModels.userData = new Object[] {"c "+column.pos.toString()+" y:"+height, "chunk", "ithaslight"};
	}
	
	public void callFromRenderThread() throws Exception {
		if (reqmodelupd) {
			lUpdateModel();
			reqmodelupd = false;
			Hpb.world.isCycleFree = false;
		}
	}
	
	private boolean wr(int x, int y, int z, Block current) {//false = рендерится
		if (this.height+y < 0) {
			return true;
		}
		Block b = Hpb.world.getBlock(column.pos.x*16+x,this.height+y,column.pos.z*16+z);
		
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
		blocks[x][y][z] = Block.blockById(i, new Vector3D(column.pos.x*16+x,height+y,column.pos.z*16+z));
	}
	
	/*public boolean checkCamFrustum() {
		return GameInstance.world.player.cam.cam.frustum.boundsInFrustum(center, dimensions);
	}*/
	
	public Vector3I getPos() {
		return new Vector3I(column.pos.x, height/16,column.pos.z);
	}
	
	private static int index(int x, int y, int z) {
        return y << 8 | z << 4 | x;
    }

	public void tick() {//TODO оптимизировать до хешсета хранящего векторы блоков для тика
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