package net.pzdcrp.wildland.world;

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;

import net.pzdcrp.wildland.GameInstance;
import net.pzdcrp.wildland.data.AABB;
import net.pzdcrp.wildland.data.ColCoords;
import net.pzdcrp.wildland.data.EntityType;
import net.pzdcrp.wildland.data.Vector3D;
import net.pzdcrp.wildland.player.Player;
import net.pzdcrp.wildland.utils.MathU;
import net.pzdcrp.wildland.utils.ModelUtils;
import net.pzdcrp.wildland.utils.VectorU;
import net.pzdcrp.wildland.world.elements.Chunk;
import net.pzdcrp.wildland.world.elements.Column;
import net.pzdcrp.wildland.world.elements.blocks.Air;
import net.pzdcrp.wildland.world.elements.blocks.Block;
import net.pzdcrp.wildland.world.elements.blocks.Voed;
import net.pzdcrp.wildland.world.elements.blocks.Block.BlockType;
import net.pzdcrp.wildland.world.elements.entities.Entity;

public class World {// implements RenderableProvider {
	public Player player;
	public Map<ColCoords,Column> loadedColumns = new ConcurrentHashMap<>();
	public Map<ColCoords,Column> memoriedColumns = new ConcurrentHashMap<>();
	public List<ModelInstance> additional = new CopyOnWriteArrayList<>();
	public int time = 0;
	public Environment env;
	public static final int chunks = 6, chunkWidht = 16, maxHeight = chunkWidht*chunks;
	public static boolean ready = false;
	public Matrix4 temp = new Matrix4();
	public Vector3 sunPosition = new Vector3();
	public ModelInstance sun;
	public Environment sunlight;
	public DirectionalLight sundl;
    public Vector3 moonPosition = new Vector3();
    public ModelInstance moon;
    public Vector3 lightDirection = new Vector3();
    

    private static final int DAY_LENGTH = 60000;
    private static final float DISTANCE_FROM_CENTER = 200f;
    public static final boolean load = false;
    int renderRad = 4;
    Material skymaterial;
    ColorAttribute envcolor;
	
	public World() {
		env = new Environment();
		env.set(envcolor = new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f, 1f));
		
		sunlight = new Environment();
		sunlight.set(new ColorAttribute(ColorAttribute.Diffuse, 0.7f,0.7f,0.7f,1f));
		sunlight.add(sundl = new DirectionalLight());
		sundl.set(Color.WHITE,0,0,0);
		sundl.setColor(0.3f,0.3f,0.3f,1);
		
		if (load) {
			System.out.println("loading world");
			loadSave();
			updateLoadedColumns();
		} else {
			player = new Player(5,chunks*16,5);
			loadedColumns.put(new ColCoords(0,0),new Column(0,0,true));
			for (int y = (int)player.pos.y; y > 0; y--) {
				if (getBlock(new Vector3D((int)Math.floor(player.pos.x), y-1, (int)Math.floor(player.pos.z))).getType() != BlockType.air) {
					player.pos.y = y;
					break;
				}
			}
			updateLoadedColumns();
		}
		System.out.println("loading enviroment");
		ModelBuilder modelBuilder = new ModelBuilder();
		skymaterial = new Material(ColorAttribute.createDiffuse(0, 0, 255, 255), IntAttribute.createCullFace(GL20.GL_NONE));
		Model model = modelBuilder.createSphere(DISTANCE_FROM_CENTER*2, DISTANCE_FROM_CENTER*2, DISTANCE_FROM_CENTER*2, 20, 20,skymaterial, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		sky = new ModelInstance(model);
		Matrix4 transform = new Matrix4();
		transform.translate((float)player.pos.x,(float)player.pos.y,(float)player.pos.z);
		sky.transform.set(transform);
		
		modelBuilder = new ModelBuilder();
		Material material = new Material(ColorAttribute.createDiffuse(Color.YELLOW));
		model = modelBuilder.createSphere(20f, 20f, 20f, 15, 15, material, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		sun = new ModelInstance(model);
		
		modelBuilder = new ModelBuilder();
		material = new Material(ColorAttribute.createDiffuse(Color.DARK_GRAY));
		model = modelBuilder.createSphere(5f, 5f, 5f, 5, 5, material, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		moon = new ModelInstance(model);
		
		
		model = ModelUtils.createCubeModel(false,false,true,false,false,false,"stone",false,new Vector3((float)player.pos.x,(float)player.pos.y,(float)player.pos.z));
		
		//additional.add(new ModelInstance(model));
		System.out.println("ready");
		
		ready = true;
	}
	
	public void setBlock(Block block) {
		if (block.pos.y < 0 || block.pos.y >= maxHeight) return;
		Column col = getColumn(block.pos.x,block.pos.z);
		for (Entry<ColCoords, Column> tcol : loadedColumns.entrySet()) {
			for (Entity en : tcol.getValue().entites) {
				if (block.collide(en.getHitbox())) return;
			}
		}
		col.setBlock((int)block.pos.x&15,(int)block.pos.y,(int)block.pos.z&15, Block.idByBlock(block));
	}
	
	public void setBlock(Vector3D v, int id) {
		if (v.y < 0 || v.y >= maxHeight) return;
		Column col = getColumn(v.x,v.z);
		Block b = Block.blockById(id, v);
		for (Entry<ColCoords, Column> tcol : loadedColumns.entrySet()) {
			for (Entity en : tcol.getValue().entites) {
				if (b.collide(en.getHitbox())) return;
			}
		}
		col.setBlock((int)v.x&15,(int)v.y,(int)v.z&15, id);
	}
	
	public Column getColumn(double x, double z) {
		int cx = (int)Math.floor(x) >> 4;
		int cz = (int)Math.floor(z) >> 4;
		for (Entry<ColCoords, Column> col : loadedColumns.entrySet()) {
			if (col.getValue().coords.columnX == cx && col.getValue().coords.columnZ == cz) {
				return col.getValue();
			}
		}
		return null;
	}
	public Column getColumn(ColCoords cc) {
		for (Entry<ColCoords, Column> col : loadedColumns.entrySet()) {
			if (col.getValue().coords.equals(cc)) {
				return col.getValue();
			}
		}
		return null;
	}
	public Column getUnloadedColumn(double x, double z) {
		int cx = (int)Math.floor(x) >> 4;
		int cz = (int)Math.floor(z) >> 4;
		for (Entry<ColCoords, Column> col : loadedColumns.entrySet()) {
			if (col.getValue().coords.columnX == cx && col.getValue().coords.columnZ == cz) {
				return col.getValue();
			}
		}
		return null;
	}
	
	public Block getBlock(Vector3D v) {
		if (v.y < 0 || v.y >= maxHeight) return new Voed(v,null);
		Column col = getColumn(v.x,v.z);
		if (col == null) col = getUnloadedColumn(v.x,v.z);
		if (col == null) return new Voed(v,null);
		try {
			Block c = Block.blockById(col.getBlock((int)v.x&15,(int)v.y,(int)v.z&15), v);
			return c;
		} catch (Exception e) {
			System.out.println("pizdec s getblock v worlde");
			e.printStackTrace();
			System.exit(0);
		}
		return null;
	}
	
	public Column genOrLoad(int cx, int cz) {
		for (Entry<ColCoords, Column> col : memoriedColumns.entrySet()) {
			if (col.getValue().coords.columnX == cx && col.getValue().coords.columnZ == cz) {
				return col.getValue();
			}
		}
		return new Column(cx,cz,true);
	}
	
	
	public ModelInstance sky;
	public void renderSky() {
		time = (int) time % DAY_LENGTH;

        float angle = 2f * (float) Math.PI * time / DAY_LENGTH;
        float x = (float) ((DISTANCE_FROM_CENTER * (float) Math.cos(angle)) + player.pos.x);
        float y = (float) ((DISTANCE_FROM_CENTER * (float) Math.sin(angle)) + player.pos.y);
        sky.transform.setToTranslation((float)player.pos.x, (float)player.pos.y, (float)player.pos.z);
        sun.transform.setToTranslation(x, y, (float) player.pos.z);
        //moon.transform.setToTranslation(x, y, (float) player.pos.z);
		GameInstance.modelBatch.render(sun);
		float sunpossc = (float) player.pos.y-y;
		if (sunpossc <= 40 && sunpossc >= -40) {
			float a = sunpossc*0.1f;
			envcolor.color.r = 1.1f-a/4;
			envcolor.color.g = 1.1f-a/4;
			envcolor.color.b = 1.1f-a/4;
		} else if (sunpossc >= -40) {
			envcolor.color.r = 0.1f;
			envcolor.color.g = 0.1f;
			envcolor.color.b = 0.1f;
		} else if (sunpossc <= 40) {
			envcolor.color.r = 1.1f;
			envcolor.color.g = 1.1f;
			envcolor.color.b = 1.1f;
		}
		//GameInstance.modelBatch.render(moon);
		sundl.direction.set(-x, -y, (float) player.pos.z);
		GameInstance.modelBatch.render(sky,env);
        //lightDirection.set(player.pos.translate()).sub(sunPosition).nor();
	}
	
	public void tick() {
		if (!ready) return;
		time++;
		if (time > DAY_LENGTH) time = 0;
		for (Entry<ColCoords, Column> column : loadedColumns.entrySet()) {
			column.getValue().tick();
		}
		player.tick();
		updateLoadedColumns();
	}
	static long beforetime = System.currentTimeMillis();
	static long now;
	public void deltaTime() {
		now = System.currentTimeMillis();
		System.out.println(now - beforetime);
		beforetime = now;
	}
	
	public void updateLoadedColumns() {
		List<ColCoords> cl = new ArrayList<>();
	    for (int x = -renderRad; x < renderRad;x++) {
	    	for (int z = -renderRad; z < renderRad;z++) {
		    	cl.add(new ColCoords(player.beforeechc.columnX+x,player.beforeechc.columnZ+z));
		    }
	    }
	    //чекаем какие есть и убирает из cl то что есть
	    for (Entry<ColCoords, Column> col : loadedColumns.entrySet()) {
	    	if (cl.contains(col.getValue().coords)) {
	    		cl.remove(col.getValue().coords);
	    	} else {//отгружаем чанки которых нет в cl
	    		loadedColumns.remove(col.getKey());
	    		memoriedColumns.put(col.getKey(), col.getValue());
	    	}
	    }
	    //подгружаем недостдавшиеся
	    int i = 0;
	    for (ColCoords c : cl) {
	    	//System.out.println("managing columns: "+i+++"/"+cl.size());
	    	loadedColumns.put(c,genOrLoad(c));
	    	
	    }
	}
	
	public Column genOrLoad(ColCoords need) {
		//System.out.println("creatednew");
		for (Entry<ColCoords, Column> col : memoriedColumns.entrySet()) {
			if (col.getValue().coords.equals(need)) {
				return col.getValue();
			}
		}
		return new Column(need, true);
	}
	
	
	public void render() {
		renderSky();
		//List<Model> world = new ArrayList<>();
		//int i = 0;
		for (Entry<ColCoords, Column> col : loadedColumns.entrySet()) {
			col.getValue().render();
			//System.out.println(i+++"/"+loadedColumns.size()+" rendering");
		}
		//GameInstance.modelBatch.render(ModelUtils.combineModels(world), GameInstance.world.env);
		for (ModelInstance a : additional) {
			GameInstance.modelBatch.render(a, env);
		}
		player.render();
	}
	
	@SuppressWarnings("deprecation")
	public void loadSave() {
		try {
			
			JsonReader reader = new JsonReader(new FileReader("world.json"));
			JsonObject obj = (JsonObject) new JsonParser().parse(reader);
			this.time = obj.get("time").getAsInt();
			JsonArray columns = obj.get("columns").getAsJsonArray();
			for (JsonElement jcole : columns) {
				JsonObject jcol = jcole.getAsJsonObject();
				ColCoords cc = ColCoords.fromString(jcol.get("pos").getAsString());
				Column column = new Column(cc.columnX,cc.columnZ,false);
				
				//blocks
				int i = 0;
				JsonArray blocks = jcol.get("blocks").getAsJsonArray();
				for (int px = 0; px < World.chunkWidht; px++) {
			        for (int py = 0; py < World.maxHeight; py++) {
			            for (int pz = 0; pz < World.chunkWidht; pz++) {
			                column.fastSetBlock(px, py, pz, blocks.get(i).getAsInt());
			                i++;
			            }
			        }
			    }
				//entities
				JsonArray entities = jcol.get("entities").getAsJsonArray();
				memoriedColumns.put(column.coords,column);
				for (JsonElement jene : entities) {
					JsonObject jen = jene.getAsJsonObject();
					
					Vector3D pos = Vector3D.fromString(jen.get("pos").getAsString());
					EntityType type = EntityType.valueOf(jen.get("type").getAsString());
					Entity entity;
					if (type == EntityType.player) {
						player = new Player(pos.x,pos.y,pos.z);
						entity = player;
						System.out.println(player);
					} else {
						AABB hb = AABB.fromString(jen.get("hitbox").getAsString());
						entity = new Entity(pos, hb, type);
					}
					String[] jvel = jen.get("vel").getAsString().split(" ");
					entity.velX = Double.parseDouble(jvel[0]);
					entity.velY = Double.parseDouble(jvel[1]);
					entity.velZ = Double.parseDouble(jvel[2]);
					String[] jcoll = jen.get("coldata").getAsString().split(" ");
					entity.colx = Boolean.parseBoolean(jcoll[0]);
					entity.coly = Boolean.parseBoolean(jcoll[1]);
					entity.colz = Boolean.parseBoolean(jcoll[2]);
					entity.onGround = jen.get("onGround").getAsBoolean();
					String[] rot = jen.get("yawpitch").getAsString().split(" ");
					entity.yaw = Float.parseFloat(rot[0]);
					entity.pitch = Float.parseFloat(rot[1]);
					entity.beforeechc = ColCoords.fromString(jen.get("beforeechc").getAsString());
					entity.readCustomProp(jen.get("custom").getAsJsonObject());
					
					column.entites.add(entity);
				}
				column.updateModel();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void save() {
		try {
			JsonObject worldData = new JsonObject();
			
			worldData.add("columns", new JsonArray());
			worldData.add("time", new JsonPrimitive(this.time));
			List<Column> tcols = new ArrayList<>();
			for (Column col : loadedColumns.values()) {
				tcols.add(col);
			}
			for (Column col : memoriedColumns.values()) {
				tcols.add(col);
			}
			int i = 1;
			for (Column column : tcols) {
				System.out.println("saving columns "+i+"/"+tcols.size());
				i++;
				JsonObject jcol = new JsonObject();
				//pos
				jcol.addProperty("pos", column.coords.toString());
				//blocks
				JsonArray blocks = new JsonArray();
				for (int px = 0; px < World.chunkWidht; px++) {
			        for (int py = 0; py < World.maxHeight; py++) {
			            for (int pz = 0; pz < World.chunkWidht; pz++) {
			                blocks.add(column.getBlock(px,py,pz));
			            }
			        }
			    }
				jcol.add("blocks", blocks);
				//entities
				jcol.add("entities", new JsonArray());
				for (Entity entity : column.entites) {
					JsonObject jen = new JsonObject();
					
					jen.addProperty("pos", entity.pos.toString());
					jen.addProperty("hitbox", entity.hitbox.toString());
					jen.addProperty("vel", entity.velX+" "+entity.velY+" "+entity.velZ);
					jen.addProperty("coldata", entity.colx+" "+entity.coly+" "+entity.colz);
					jen.addProperty("onGround", entity.onGround);
					jen.addProperty("yawpitch", entity.yaw+" "+entity.pitch);
					jen.addProperty("beforeechc", entity.beforeechc.toString());
					jen.addProperty("type", entity.type.toString());
					
					//addtolist
					jcol.get("entities").getAsJsonArray().add(jen);
					jen.add("custom", entity.getCustomProp());
				}
				//addtodb
				worldData.get("columns").getAsJsonArray().add(jcol);
			}
			
			System.out.println("writing...");
			FileWriter writer = new FileWriter("world.json");
			writer.write(worldData.toString());
			writer.close();
			System.out.println("done!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static Material t3mp = new Material(
			TextureAttribute.createDiffuse(GameInstance.getTexture("dirt")),
			IntAttribute.createCullFace(GL20.GL_NONE),
			new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
			);
	/*@Override
	public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
		for (Column column : loadedColumns) {
			for (Chunk chunk : column.chunks) {
				if (chunk.requestUpdate || chunk.mesh == null) {
					
				} else {
					Renderable renderable = pool.obtain();
					renderable.material = t3mp;
					renderable.meshPart.mesh = chunk.allModels.model.meshes.get(0);
					renderable.meshPart.offset = 0;
					renderable.meshPart.size = chunk.allModels.model.meshes.get(0).getNumVertices();//numVertices[i];
					renderable.meshPart.primitiveType = GL20.GL_TRIANGLES;
					renderables.add(renderable);
				}
			}
		}
	}*/
}
