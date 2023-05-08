package net.pzdcrp.Hyperborea.world.elements;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
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
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.BufferUtils;

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.MBIM;
import net.pzdcrp.Hyperborea.data.Pair;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.utils.MathU;
import net.pzdcrp.Hyperborea.utils.ModelUtils;
import net.pzdcrp.Hyperborea.world.World;
import net.pzdcrp.Hyperborea.world.elements.blocks.Air;
import net.pzdcrp.Hyperborea.world.elements.blocks.Block;
import net.pzdcrp.Hyperborea.world.elements.blocks.Block.BlockType;
import net.pzdcrp.Hyperborea.world.elements.blocks.Stone;

public class Chunk {
	private Block[][][] blocks = new Block[16][16][16];//TODO о хорошему бы вернуть систему когда блоки хранились по айди а не классу
	public int[][][] light = new int[16][16][16];//0-19
	public ModelInstance allModels;
	//public ModelInstance transparent;
	public Column motherCol;
	public BoundingBox box;
	public int height;
	private boolean requestUpdate = false;
	public boolean tickable = false;
	public static final int worldSunLight = 10;//3-20 в зависимости от времени
	public boolean changed = true;//TODO remove
	public MBIM m;
	private boolean updateLight;
	
	public Chunk(Column motherCol, int height) throws Exception {
		this.height = height;
		this.motherCol = motherCol;
		for (int y = 0; y < 16; y++) {
	    	for(int x = 0; x < 16; x++) {
	    		for(int z = 0; z < 16; z++) {
	    			setBlock(x,y,z,0);
	    			light[x][y][z] = 0;
	    		}
	    	}
		}
	}
	
	public void updateModel() {
		requestUpdate = true;
	}
	
	//координаты внутри чанка
	public int rawGetLight(int x, int y, int z) throws Exception {
		if (x < 0 || x > 15 || y < 0 || y > 15 || z < 0 || z > 15) {
			/*x = motherCol.pos.x*16+x;
			y = this.height+y;
			z = motherCol.pos.z*16+z;
			return Hpb.world.getLight(x, y, z);*/
			return 0;
		}
		return light[x][y][z];
	}
	public void rawSetLight(int x, int y, int z, int num) throws Exception {
		if (x < 0 || x > 15 || y < 0 || y > 15 || z < 0 || z > 15) {
			/*x = motherCol.pos.x*16+x;
			y = this.height+y;
			z = motherCol.pos.z*16+z;
			Hpb.world.setLight(x, y, z, num);*/
			return;
		}
		if (light[x][y][z] != num) {
			light[x][y][z] = num;
			//обновление близжайших
		}
	}
	
	private void updateLight() throws Exception {
		if (!Thread.currentThread().getName().equals("main thd")) {
			throw new Exception("Wrong thread");
		}
	    for (int y = 15; y >= 0; y--) {
	    	for(int x = 0; x < 16; x++) {
	    		for(int z = 0; z < 16; z++) {
	    			//если блок свеху это свет неба то и этот блок будет светом неба и дальше ничо не делаем
	    			if (blocks[x][y][z] instanceof Stone) {
	    				light[x][y][z] = 20;
	    				continue;
	    			}
	    			//System.out.println(motherCol.skylightmaxdown[x][z]);
	    			if (y >= motherCol.skylightmaxdown[x][z]) {
	    				light[x][y][z] = worldSunLight;
	    				//System.out.println(light[x][y][z]);
	    				continue;
	    			}
	    			int[] near = new int[] {
	    				rawGetLight(x+1,y,z),
	    				rawGetLight(x-1,y,z),
	    				rawGetLight(x,y+1,z),
	    				rawGetLight(x,y-1,z),
	    				rawGetLight(x,y,z+1),
	    				rawGetLight(x,y,z-1),
	    			};
	    			//берем самый яркий и устанавливаем на 1 значение меньше от него
	    			int max = 0;
	    			for (int cur : near) {
	    				if (cur > max) max = cur;
	    			}
	    			if (max == 0) {
	    				light[x][y][z] = 0;
	    				continue;
	    			}
	    			light[x][y][z] = max-1;
	    			//устанавливаем на 1 уровень света ниже от сейчашнего для всех кто тусклее чем этот блок
	    			int current = light[x][y][z];
	    			if (near[0] < current-1) rawSetLight(x+1,y,z, current-1);
	    			if (near[1] < current-1) rawSetLight(x-1,y,z, current-1);
	    			if (near[2] < current-1) rawSetLight(x,y+1,z, current-1);
	    			if (near[3] < current-1) rawSetLight(x,y-1,z, current-1);
	    			if (near[4] < current-1) rawSetLight(x,y,z+1, current-1);
	    			if (near[5] < current-1) rawSetLight(x,y,z-1, current-1);
		    	}
	    	}
	    }
	}

	private void lUpdateModel() throws Exception {
		updateLight();
		Map<String, Pair> modelsById = new HashMap<>();
		m = new MBIM(modelsById, this);
		for(int xx = 0; xx < 16; xx++) {
			for(int yy = 0; yy < 16; yy++) {
				for(int zz = 0; zz < 16; zz++) {
					try {
						Block clas = blocks[xx][yy][zz];
						if (clas == null) {
							System.out.println("pizdec: "+xx+" "+yy+" "+zz);
							throw new Exception("test "+changed);
						}
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
		//List<Model> transparents = new ArrayList<>();
		for (Entry<String, Pair> entry : modelsById.entrySet()) {
			if (entry.getKey().startsWith("tr:")) {
				//transparents.add(entry.getValue().mb.end());
			} else {
				models.add(entry.getValue().mb.end());
			}
		}
		allModels = ModelUtils.combineModels(models);
		
		//light array
		
		allModels.userData = new Object[] {"c "+motherCol.pos.toString()+" y:"+height, "chunk", createTextureFromArray(m.getLightArray())};
		
		//transparent = ModelUtils.combineModels(transparents);
		//transparent.userData = new Object[] {"c"," transparent"};
		
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
	
	public static Texture createTextureFromArray(int[] array) {
	    // Создаем из массива Pixmap с форматом RGB888
	    Pixmap pixmap = new Pixmap(array.length, 1, Format.RGBA8888);
		//int buffersize = array.length;
		//int ost = buffersize%4;
		//if (ost != 0) buffersize+=4-ost;
	    for (int i = 0; i < array.length; i++) {
	    	int num = Color.rgba8888(256-array[i], 0, 0, 255);
	        pixmap.drawPixel(i, 0, num);
	    }
	    
	    // Создаем из Pixmap текстуру
	    Texture texture = new Texture(array.length, 1, Format.RGBA8888);
	    texture.draw(pixmap, 0, 0);

	    // Освобождаем Pixmap
	    pixmap.dispose();

	    return texture;
	}
	
	public void callFromRenderThread() throws Exception {
		if (requestUpdate) {
			lUpdateModel();
			requestUpdate = false;
			Hpb.world.isCycleFree = false;
		}
		if (updateLight) {//TODO remove
			Texture t = Chunk.createTextureFromArray(m.getLightArray());
			allModels.userData = new Object[] {"c "+motherCol.pos.toString()+" y:"+height, "chunk", t};
			updateLight = false;
		}
	}
	
	
	public boolean antirender(int x, int y, int z, Block current) {//false = рендерится
		if (this.height+y < 0) {
			return true;
		}
		Block b = Hpb.world.getBlock(new Vector3D(motherCol.pos.x*16+x,this.height+y,motherCol.pos.z*16+z));
		
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
		changed = true;
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
		changed = true;
	}
	
	/*public boolean checkCamFrustum() {
		return GameInstance.world.player.cam.cam.frustum.boundsInFrustum(center, dimensions);
	}*/

	public void tick() {//TODO clear all shit
		if (!this.tickable) return;
		byte x=0,y=0,z=0;
		for (Block[][] blocka : blocks) {
			for (Block[] blockaa : blocka) {
				for (Block block : blockaa) {
					try {
						block.tick();
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("pizdec at: "+x+" "+y+" "+z);
						//System.exit(0);
					}
					x++;
				}
				y++;
			}
			z++;
		}
	}

	public void callLightUpdate() {
		updateLight=true;
	}
}