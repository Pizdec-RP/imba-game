package net.pzdcrp.Hyperborea.world.elements;

import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.BitStorage;
import net.pzdcrp.Hyperborea.data.MBIM;
import net.pzdcrp.Hyperborea.data.Vector2I;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.data.Vector3I;
import net.pzdcrp.Hyperborea.server.InternalServer;
import net.pzdcrp.Hyperborea.utils.GameU;
import net.pzdcrp.Hyperborea.world.PlayerWorld;
import net.pzdcrp.Hyperborea.world.World;
import net.pzdcrp.Hyperborea.world.elements.blocks.Block;
import net.pzdcrp.Hyperborea.world.elements.blocks.Block.BlockType;

public class Chunk {
	//private Block[][][] blocks = new Block[16][16][16];
	//private int[][][] blocks = new int[16][16][16];
	private BitStorage blocks;
	
	//не хранимые данные
	public ModelInstance allModels, transparent;
	public Column column;
	public int height;
	public boolean reqmodelupd = true;
	public boolean inlightupd = true, outlightupd = true;
	private MBIM m;
	public Vector3 center;
	private static final Vector3 dimensions = new Vector3(16,16,16);
	public Vector3D pos;
	
	//не хранятся, но должны
	private BitStorage light;
	
	//ссылки
	public World world;
	
	public Chunk(Column motherCol, int height, World world) {
		this.height = height;
		this.column = motherCol;
		this.world = world;
		this.light = new BitStorage(4, 4096);
		this.blocks = new BitStorage(8, 4096);
		for(int xx = 0; xx < 16; xx++) {
			for(int yy = 0; yy < 16; yy++) {
				for(int zz = 0; zz < 16; zz++) {
					int index = index(xx,yy,zz);
					blocks.set(index, 0);
					light.set(index, 14);
				}
			}
		}
		this.pos = new Vector3D(column.pos.x, height/16, column.pos.z);
		this.center = new Vector3(column.pos.x*16+8, height+8, column.pos.z*16+8);
		m = new MBIM(this);
	}
	
	public void updateModel() {
		reqmodelupd = true;
	}
	
	//корды внутри чанка
	public int rawGetLight(int x, int y, int z) {
		if (x < 0 || x > 15 || y < 0 || y > 15 || z < 0 || z > 15) {
			return world.getLight(normx(x), normy(y), normz(z));
			//return 0;
		}
		return light.get(index(x,y,z));
	}
	public void rawSetLight(int x, int y, int z, int num) {
		if (x < 0 || x > 15 || y < 0 || y > 15 || z < 0 || z > 15) {
			world.setLight(normx(x), normy(y), normz(z), num);
			return;
		}
		if (light.get(index(x,y,z)) != num) {
			light.set(index(x,y,z), num);
		}
	}
	
	public int getInternalLight(int x, int y, int z) {
		return light.get(index(x,y,z));
	}
	
	public void setInternalLight(int x, int y, int z, int val) {
		light.set(index(x,y,z), val);
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
		if (!Thread.currentThread().getName().equals("server chunk update thread"))
	    	GameU.end("метод не должен вызываться из мира сервера");
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
	    if (!Thread.currentThread().getName().equals("server chunk update thread"))
	    	GameU.end("метод не должен вызываться из мира сервера");
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
	            	if (normy(y) >= slmd /* || blocks[x][y][z].emitLight()*/) {
	                    Vector3I vector = new Vector3I(x,y,z);
	                    stack.add(vector);
	                    rawSetLight(x,y,z, InternalServer.world.skylight);
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
	        	b = world.getBlock(normx(l.x),normy(l.y),normz(l.z));
	        } else {
	        	b = this.getBlock(l.x,l.y,l.z);
	        }
	        
	        if (b == null) {
	        	GameU.end("nullblock: "+l.toString());
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
	
	public void setBlock(int x, int y, int z, int id) {
		blocks.set(index(x,y,z), id);
	}
	@Deprecated
	public void setBlock(int x, int y, int z, Block i) {
		if (i == null) {
			GameU.end("null block exception");
		}
		blocks.set(index(x,y,z), i.getId());
		
		//updateModel();
		inlightupd = true;
		//System.out.println("placed in: "+this.getPos());
		if (PlayerWorld.ready) {//TODO этот код полная хуйня
			for (Chunk c : sides()) {
				//System.out.println("side: "+c.getPos());
				c.inlightupd = true;
			}
		}
	}
	
	public void lUpdateModel() {
		if (!Thread.currentThread().getName().equals("main thd")) {
			GameU.end("метод должен вызываться только со стороны клиента");
		}
		m.clear();
		//System.out.println("upd model");
		//Map<String, Pair> modelsById = new HashMap<>();
		for(int xx = 0; xx < 16; xx++) {
			for(int yy = 0; yy < 16; yy++) {
				for(int zz = 0; zz < 16; zz++) {
					Block clas = Block.getRaw(getBlocki(xx,yy,zz));
					if (clas.isRenderable()) {
						clas.pos.setComponents(normx(xx), normy(yy), normz(zz));
						boolean n1,n2,n3,n4,n5,n6;
						n1 = wr(xx,yy+1,zz, clas);
						n2 = wr(xx,yy-1,zz, clas);
						n3 = wr(xx-1,yy,zz, clas);//left -x
						n4 = wr(xx+1,yy,zz, clas);
						n5 = wr(xx,yy,zz-1, clas);
						n6 = wr(xx,yy,zz+1, clas);
						clas.addModel(n1,n2,n3,n4,n5,n6,m);
						clas.pos.setComponents(0,0,0);
					}
				}
			}
		}
		if (bbpos != null) {
			Block.bbmodel(m, bbpos, bbstage);
		}
		allModels = m.endSolid();
		transparent = m.endTransparent();
		allModels.userData = new Object[] {"chunk", column.pos.toString()+" y:"+height, "ithaslight", "solid"};
		transparent.userData = new Object[] {"chunk", column.pos.toString()+" y:"+height, "ithaslight", "transparent"};
	}
	
	public int bbstage = -1;
	private Vector3D bbpos = null;
	public void addBlockBreakStage(Vector3D pos, int stage) {
		this.bbpos = pos;
		this.bbstage = stage;
		this.reqmodelupd = true;
	}
	
	public void endBlockBreakStage() {
		this.bbpos = null;
		this.bbstage = -1;
		this.reqmodelupd = true;
	}
	/**
	 * Client side only
	 */
	public void rebuildTransparent() {
		if (this.m.transparentmodel != null) {
			m.sortTransparent(Hpb.world.player.cam.cam.position);
		}
	}
	
	private boolean wr(int x, int y, int z, Block current) {//false = рендерится
		if (this.height+y < 0) {
			return true;
		}
		Block b = world.getBlock(column.pos.x*16+x,this.height+y,column.pos.z*16+z);
		
		if (b.getType() == BlockType.air) {
			return false;
		}
		if (b.getType() == BlockType.transparent) {
			if (current.getClass() == b.getClass()) return true;
			return false;
		} else if (b.getType() == BlockType.noncollideabe) {
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
		
		temp = world.getWithoutLoad(new Vector2I(column.pos.x+1, column.pos.z));
		if (temp != null) {
			l.add(temp.chunks[height/16]);
		}
		
		temp = world.getWithoutLoad(new Vector2I(column.pos.x-1, column.pos.z));
		if (temp != null) {
			l.add(temp.chunks[height/16]);
		}
		
		temp = world.getWithoutLoad(new Vector2I(column.pos.x, column.pos.z+1));
		if (temp != null) {
			l.add(temp.chunks[height/16]);
		}
		
		temp = world.getWithoutLoad(new Vector2I(column.pos.x, column.pos.z-1));
		if (temp != null) {
			l.add(temp.chunks[height/16]);
		}
		return l.toArray(new Chunk[0]);
	}
	
	@Deprecated
	public Block getBlock(int x, int y, int z) {
		return Block.blockById(blocks.get(index(x,y,z)), new Vector3D(normx(x),normy(y),normz(z)));
	}
	
	public int getBlocki(int x, int y, int z) {
		return blocks.get(index(x,y,z));
	}

	/*public Block setBlock(int x, int y, int z, int i) {//xyz = 0-15
		Block block = Block.blockById(i, new Vector3D(column.pos.x*16+x,height+y,column.pos.z*16+z));
		blocks[x][y][z] = block;
		return block;
	}*/
	
	/*public boolean checkCamFrustum() {
		return GameInstance.world.player.cam.cam.frustum.boundsInFrustum(center, dimensions);
	}*/
	
	public Vector3I getPos() {
		return new Vector3I(column.pos.x, height/16,column.pos.z);
	}
	
	public static int index(int x, int y, int z) {
        return y << 8 | z << 4 | x;
    }
	
	/**
	 * Server side only
	 */
	public void tick() {//TODO оптимизировать до хешсета хранящего объекты блоков для тика
	}
	
	/**
	 * Client side only
	 */
	public boolean boundsInFrustum() {
		return Hpb.world.player.cam.cam.frustum.boundsInFrustum(center, dimensions);
	}

	public BitStorage getLightStorage() {
		return light;
	}
	
	public boolean canrender = false;
	/**
	 * должно вызываться только при получении пакета со светом
	 * @param light
	 */
	public void setLightStorage(BitStorage light) {
		canrender = true;
		column.recheckcanrender();
		this.light = light;
	}
}