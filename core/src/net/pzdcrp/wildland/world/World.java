package net.pzdcrp.wildland.world;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
import net.pzdcrp.wildland.data.Vector2I;
import net.pzdcrp.wildland.data.EntityType;
import net.pzdcrp.wildland.data.Vector3D;
import net.pzdcrp.wildland.player.Player;
import net.pzdcrp.wildland.utils.MathU;
import net.pzdcrp.wildland.utils.ModelUtils;
import net.pzdcrp.wildland.utils.VectorU;
import net.pzdcrp.wildland.world.elements.Chunk;
import net.pzdcrp.wildland.world.elements.Column;
import net.pzdcrp.wildland.world.elements.ParticleManager;
import net.pzdcrp.wildland.world.elements.Region;
import net.pzdcrp.wildland.world.elements.blocks.Air;
import net.pzdcrp.wildland.world.elements.blocks.Block;
import net.pzdcrp.wildland.world.elements.blocks.Voed;
import net.pzdcrp.wildland.world.elements.blocks.Block.BlockType;
import net.pzdcrp.wildland.world.elements.entities.Entity;

public class World {// implements RenderableProvider {
	public Player player;
	public Map<Vector2I,Column> loadedColumns = new ConcurrentHashMap<>();
	public Map<Vector2I,Region> memoriedRegions = new ConcurrentHashMap<>();
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
    public ParticleManager pm = new ParticleManager(this);
    
    private static final int DAY_LENGTH = 60000;
    private static final float DISTANCE_FROM_CENTER = 200f;
    public static final boolean load = true;
    public static int renderRad = 5;
    Material skymaterial;
    ColorAttribute envcolor;
	
	public World() {
		
	}
	
	public void load() {
		if (load) {
			System.out.println("подгружаем мир");
			try {
				JsonReader reader = new JsonReader(new FileReader("save/wdata.dat"));
				JsonObject obj = (JsonObject) new JsonParser().parse(reader);
				
				this.time = obj.get("time").getAsInt();
				Vector2I cpos = Vector2I.fromString(obj.get("playercol").getAsString());
				Vector2I rpos = Vector2I.fromString(obj.get("playerreg").getAsString());
				
				this.loadedColumns.put(cpos, genOrLoadRegion(rpos).getColumn(cpos));
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("данных о мире нет");
				System.exit(0);
			}
		} else {
			System.out.println("спавнимся");
			player = new Player(5,chunks*16,5);
		}
		if (player == null) {
			System.out.println("резерв спавн");
			player = new Player(5,chunks*16,5);
		}
		updateLoadedColumns();
		player.tick();
		System.out.println("подгружаем окружение");
		loadEnvironment();
		World.ready = true;
		System.out.println("все заебок");
	}

	public void loadEnvironment() {
		env = new Environment();
		env.set(envcolor = new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f, 1f));
		
		sunlight = new Environment();
		sunlight.set(new ColorAttribute(ColorAttribute.Diffuse, 0.7f,0.7f,0.7f,1f));
		sunlight.add(sundl = new DirectionalLight());
		sundl.set(Color.WHITE,0,0,0);
		sundl.setColor(0.3f,0.3f,0.3f,1);
		
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
	}
	
	public void setBlock(Block block) {
		if (block.pos.y < 0 || block.pos.y >= maxHeight) return;
		Column col = getColumn(block.pos.x,block.pos.z);
		for (Entry<Vector2I, Column> tcol : loadedColumns.entrySet()) {
			for (Entity en : tcol.getValue().entites) {
				if (block.collide(en.getHitbox())) return;
			}
		}
		col.setBlock((int)block.pos.x&15,(int)block.pos.y,(int)block.pos.z&15, block);
	}
	
	public Column getColumn(double x, double z) {
		int cx = (int)Math.floor(x) >> 4;
		int cz = (int)Math.floor(z) >> 4;
		for (Entry<Vector2I, Column> col : loadedColumns.entrySet()) {
			if (col.getValue().pos.x == cx && col.getValue().pos.z == cz) {
				return col.getValue();
			}
		}
		return null;
	}
	public Column getColumn(Vector2I cc) {
		for (Entry<Vector2I, Column> col : loadedColumns.entrySet()) {
			if (col.getValue().pos.equals(cc)) {
				return col.getValue();
			}
		}
		return null;
	}
	public Column getUnloadedColumn(double x, double z) {
		int cx = (int)Math.floor(x) >> 4;
		int cz = (int)Math.floor(z) >> 4;
		for (Entry<Vector2I, Column> col : loadedColumns.entrySet()) {
			if (col.getValue().pos.x == cx && col.getValue().pos.z == cz) {
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
			Block c = col.getBlock((int)v.x&15,(int)v.y,(int)v.z&15);
			if (c == null) return new Voed(v,null);
			return c;
		} catch (Exception e) {
			System.out.println("pizdec s getblock v worlde");
			e.printStackTrace();
			System.exit(0);
		}
		return new Voed(v,null);
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
		if (!ready || GameInstance.exit) {
			return;
		}
		time++;
		if (time > DAY_LENGTH) time = 0;
		for (Entry<Vector2I, Column> column : loadedColumns.entrySet()) {
			column.getValue().tick();
		}
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
		List<Vector2I> cl = new ArrayList<>();
	    for (int x = -renderRad; x < renderRad;x++) {
	    	for (int z = -renderRad; z < renderRad;z++) {
		    	cl.add(new Vector2I(player.beforeechc.x+x,player.beforeechc.z+z));
		    }
	    }
	    //чекаем какие есть и убирает из cl то что есть
	    for (Entry<Vector2I, Column> col : loadedColumns.entrySet()) {
	    	if (cl.contains(col.getValue().pos)) {
	    		cl.remove(col.getValue().pos);
	    	} else {//отгружаем чанки которых нет в cl
	    		loadedColumns.remove(col.getKey());
	    		//memoriedColumns.put(col.getKey(), col.getValue());
	    	}
	    }
	    //подгружаем недостдавшиеся
	    for (Vector2I c : cl) {
	    	//System.out.println("managing columns: "+i+++"/"+cl.size());
	    	loadedColumns.put(c,genOrLoadRegion(VectorU.ColumnToRegion(c)).getColumn(c));
	    	
	    }
	}
	
	Vector3D ltemp = new Vector3D();
	public void lerp(Vector3D toset, Vector3D before, Vector3D now) {
		ltemp = new Vector3D(now.x-before.x,now.y-before.y,now.z-before.z);
		float mul = GameInstance.curCBT / GameInstance.renderCallsBetweenTicks;
		toset.setComponents(before.x + ltemp.x*mul, before.y + ltemp.y*mul, before.z + ltemp.z*mul);
	}
	
	
	public void render() {
		if (GameInstance.exit) return;
		renderSky();
		//List<Model> world = new ArrayList<>();
		//int i = 0;
		for (Entry<Vector2I, Column> col : loadedColumns.entrySet()) {
			col.getValue().renderNormal();
			//System.out.println(i+++"/"+loadedColumns.size()+" rendering");
		}
		for (Entry<Vector2I, Column> col : loadedColumns.entrySet()) {
			col.getValue().renderTransparent();
		}
		for (Entry<Vector2I, Column> col : loadedColumns.entrySet()) {
			col.getValue().renderEntites();
		}
		for (ModelInstance a : additional) {
			GameInstance.modelBatch.render(a/*, env*/);
		}
		player.render();
	}
	
	public boolean save() {
		try {
			GameInstance.exit = true;
			System.out.println("saving");
			loadedColumns.clear();
			for (Entry<Vector2I, Region> region : memoriedRegions.entrySet()) {
				writeRegion(region.getValue());
			}
			JsonObject jwd = new JsonObject();
			
			jwd.addProperty("playerreg", memoriedRegions.get(VectorU.posToRegion(player.pos)).pos.toString());
			jwd.addProperty("playercol", player.curCol.pos.toString());
			jwd.addProperty("time", this.time);
			
			FileWriter writer = new FileWriter("save/wdata.dat");
			writer.write(jwd.toString());
			writer.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
			return false;
		}
	}
	
	public Region genOrLoadRegion(Vector2I regpos) {
		if (memoriedRegions.containsKey(regpos)) {
			return memoriedRegions.get(regpos);
		}
		String need = regpos.x+"_"+regpos.z+".reg";
	    for (final File fileEntry : new File("save").listFiles()) {
	        if (!fileEntry.isDirectory()) {
	        	if (fileEntry.getName().equals(need)) {
	        		Region reg = readRegion(regpos);
	        		memoriedRegions.put(regpos, reg);
	        		return reg;
	        	}
	        }
	    }
	    Region reg = new Region(regpos);
		memoriedRegions.put(regpos, reg);
		return reg;
	}
	
	public void writeRegion(Region reg) throws IOException {
		FileWriter writer = new FileWriter("save/"+reg.pos.x+"_"+reg.pos.z+".reg");
		writer.write(reg.toJson().toString());
		writer.close();
	}
	
	@SuppressWarnings("deprecation")
	public Region readRegion(Vector2I regpos) {
		JsonReader reader;
		try {
			reader = new JsonReader(new FileReader("save/"+regpos.x+"_"+regpos.z+".reg"));
			JsonObject obj = (JsonObject) new JsonParser().parse(reader);
			Region r = new Region(regpos);
			r.fromJson(obj);
			return r;
		} catch (ClassCastException e) {
			return new Region(regpos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("nema regiona :"+regpos.toString());
			System.exit(0);
			return null;
		}
	}

	public void chunkShit() {
		
	}
}
