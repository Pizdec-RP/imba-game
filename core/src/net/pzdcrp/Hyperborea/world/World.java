package net.pzdcrp.Hyperborea.world;

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
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.AABB;
import net.pzdcrp.Hyperborea.data.EntityType;
import net.pzdcrp.Hyperborea.data.Vector2I;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.player.Player;
import net.pzdcrp.Hyperborea.utils.MathU;
import net.pzdcrp.Hyperborea.utils.ModelUtils;
import net.pzdcrp.Hyperborea.utils.ThreadU;
import net.pzdcrp.Hyperborea.utils.VectorU;
import net.pzdcrp.Hyperborea.world.elements.Chunk;
import net.pzdcrp.Hyperborea.world.elements.Column;
import net.pzdcrp.Hyperborea.world.elements.Region;
import net.pzdcrp.Hyperborea.world.elements.blocks.Air;
import net.pzdcrp.Hyperborea.world.elements.blocks.Block;
import net.pzdcrp.Hyperborea.world.elements.blocks.Voed;
import net.pzdcrp.Hyperborea.world.elements.blocks.Block.BlockType;
import net.pzdcrp.Hyperborea.world.elements.entities.Entity;
import net.pzdcrp.Hyperborea.world.elements.entities.Particle;

public class World {// implements RenderableProvider {
	public Player player;
	public Map<Vector2I,Column> loadedColumns = new ConcurrentHashMap<>();
	public Map<Vector2I,Region> memoriedRegions = new ConcurrentHashMap<>();
	public List<ModelInstance> additional = new CopyOnWriteArrayList<>();
	public int time = 0;
	public static final int chunks = 16;
	public static boolean ready = false;
	public Matrix4 temp = new Matrix4();
	public Vector3 sunPosition = new Vector3();
	public ModelInstance sun;
    public Vector3 moonPosition = new Vector3();
    public ModelInstance moon;
    public Vector3 lightDirection = new Vector3();
    public int seed = 228;
    
    private static final int DAY_LENGTH = 60000;
    private static final float DISTANCE_FROM_CENTER = 200f;
    public static int renderRad = 2;
    Material skymaterial;
    
    public List<Particle> particles = new CopyOnWriteArrayList<>();
	
	public World() {
		Block.world = this;
		Block.init();
	}
	
	public void load() throws Exception {
		
		System.out.println("подгружаем мир");
		try {
			JsonReader reader = new JsonReader(new FileReader("save/wdata.dat"));
			JsonObject obj = (JsonObject) new JsonParser().parse(reader);
			
			this.time = obj.get("time").getAsInt();
			Vector2I cpos = Vector2I.fromString(obj.get("playercol").getAsString());
			Vector2I rpos = Vector2I.fromString(obj.get("playerreg").getAsString());
			
			this.loadedColumns.put(cpos, genOrLoadRegion(rpos).getColumn(cpos));
			player.beforeechc = cpos;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("данных о мире нет");
			player = new Player(5,chunks*16,5);
		}
		updateLoadedColumns();
		player.tick();
		System.out.println("подгружаем окружение");
		loadEnvironment();
		World.ready = true;
		System.out.println("все заебок");
		//particles.add(new Particle(Hpb.getTexture("dirt"), player.pos.translate().add(0, 1.5f,0), new Vector3(0.02f,0,0), 99999));
	}

	public void loadEnvironment() {
		
		ModelBuilder modelBuilder = new ModelBuilder();
		skymaterial = new Material(ColorAttribute.createDiffuse(0, 0, 255, 255), IntAttribute.createCullFace(GL20.GL_NONE));
		Model model = modelBuilder.createSphere(DISTANCE_FROM_CENTER*2, DISTANCE_FROM_CENTER*2, DISTANCE_FROM_CENTER*2, 20, 20,skymaterial, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		sky = new ModelInstance(model);
		sky.userData = new Object[] {"c", "sky"};
		Matrix4 transform = new Matrix4();
		transform.translate((float)player.pos.x,(float)player.pos.y,(float)player.pos.z);
		sky.transform.set(transform);
		
		modelBuilder = new ModelBuilder();
		Material material = new Material(ColorAttribute.createDiffuse(Color.YELLOW));
		model = modelBuilder.createSphere(20f, 20f, 20f, 15, 15, material, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		sun = new ModelInstance(model);
		sun.userData = new Object[] {"c", "sun"};
		
		modelBuilder = new ModelBuilder();
		material = new Material(ColorAttribute.createDiffuse(Color.DARK_GRAY));
		model = modelBuilder.createSphere(5f, 5f, 5f, 5, 5, material, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		moon = new ModelInstance(model);
		moon.userData = new Object[] {"c", "moon"};
		
		model = modelBuilder.createBox(1, 1, 1, material, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		
	}
	
	public List<Entity> getEntities(Vector3D pos, double radius) {
		ArrayList<Entity> e = new ArrayList<Entity>();
		for (Column column : Hpb.world.loadedColumns.values()) {
			for (Entity en : column.entites) {
				if (VectorU.sqrt(en.pos, pos) <= radius) {
					e.add(en);
				}
			}
		}
		return e;
	}
	static final float bs = 0.2f;
	public void setBlock(Block block) {
		if (block.pos.y < 0 || block.pos.y >= chunks*16) return;
		Column col = getColumn(block.pos.x,block.pos.z);
		for (Entry<Vector2I, Column> tcol : loadedColumns.entrySet()) {
			for (Entity en : tcol.getValue().entites) {
				if (block.isCollide() && block.collide(en.getHitbox())) return;
			}
		}
		if (block instanceof Air) {
			Block before = getBlock(block.pos);
			if (before.isRenderable()) {
				for (int i = 0; i < MathU.rnd(10, 20); i++) {
					spawnParticle(before.texture, block.pos.translate().add(MathU.rndf(0.3f, 0.7f), MathU.rndf(0.3f, 0.7f), MathU.rndf(0.3f, 0.7f)), new Vector3(MathU.rndf(-bs, bs),MathU.rndf(-bs, bs),MathU.rndf(-bs, bs)), MathU.rnd(8, 16));
				}
			}
		}
		col.setBlock((int)block.pos.x&15,(int)block.pos.y,(int)block.pos.z&15, block);
		for (Block block1 : block.getSides()) {
			block1.onNeighUpdate();
			block1.callChunkUpdate();//лаганая хуйня
		}
	}
	
	public void spawnParticle(String tname, Vector3 pos, Vector3 vel, int lifetime) {
		particles.add(new Particle(Hpb.getTexture(tname), pos, vel, lifetime));
	}
	
	public void breakBlock(Vector3D pos) {
		if (pos.y < 0 || pos.y >= chunks*16) return;
		//спавн партиклов тут по идее
		setBlock(new Air(pos));
	}
	
	public Column getColumn(double x, double z) {
		try {
			return getColumn(new Vector2I((int)Math.floor(x) >> 4, (int)Math.floor(z) >> 4));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
			return null;
		}
	}
	
	public Column getColumn(Vector2I cc) throws Exception {
		if (loadedColumns.containsKey(cc))
			return loadedColumns.get(cc);
		else return this.genOrLoadRegion(VectorU.ColumnToRegion(cc)).getColumn(cc);
	}
	
	public int getLight(int x, int y, int z) throws Exception {
		int cx = (int)Math.floor(x) >> 4;
		int cz = (int)Math.floor(z) >> 4;
		int cy = y&15;
		return getColumn(new Vector2I(cx,cz)).chunks[cy].rawGetLight(x&15, cy, z&15);
	}
	
	public void setLight(int x, int y, int z, int num) throws Exception {
		int cx = (int)Math.floor(x) >> 4;
		int cz = (int)Math.floor(z) >> 4;
		int cy = y&15;
		getColumn(new Vector2I(cx,cz)).chunks[cy].rawSetLight(x&15, cy, z&15, num);
	}
	
	public Block getBlock(double x, double y, double z) {
		if (y < 0 || y >= chunks*16) return new Voed(new Vector3D(x,y,z));
		Column col = getColumn(x,z);
		if (col == null) return new Voed(new Vector3D(x,y,z));
		try {
			Block c = col.getBlock((int)x&15,(int)y,(int)z&15);
			if (c == null) return new Voed(new Vector3D(x,y,z));
			return c;
		} catch (Exception e) {
			System.out.println("pizdec s getblock v worlde");
			e.printStackTrace();
			System.exit(0);
		}
		return new Voed(new Vector3D(x,y,z));
	}
	
	public Block getBlock(Vector3D v) {
		if (v.y < 0 || v.y >= chunks*16) return new Voed(v);
		Column col = getColumn(v.x,v.z);
		if (col == null) return new Voed(v);
		try {
			Block c = col.getBlock((int)v.x&15,(int)v.y,(int)v.z&15);
			if (c == null) return new Voed(v);
			return c;
		} catch (Exception e) {
			System.out.println("pizdec s getblock v worlde");
			e.printStackTrace();
			System.exit(0);
		}
		return new Voed(v);
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
		Hpb.render(sun);
		//GameInstance.modelBatch.render(moon);
		//sundl.direction.set(-x, -y, (float) player.pos.z);
		Hpb.render(sky);
        //lightDirection.set(player.pos.translate()).sub(sunPosition).nor();
	}
	
	public void tick() throws Exception {
		if (!ready || Hpb.exit) {
			return;
		}
		//System.out.println("tick-------------------");
		//deltaTime();
		time++;
		if (time > DAY_LENGTH) time = 0;
		//deltaTime();
		for (Entry<Vector2I, Column> column : loadedColumns.entrySet()) {
			column.getValue().tick();
		}
		for (Particle p : particles) {
			p.update();
		}
	}
	static long beforetime = System.currentTimeMillis();
	static long now;
	public void deltaTime() {
		now = System.currentTimeMillis();
		System.out.println(now - beforetime);
		beforetime = now;
	}
	
	public void updateLoadedColumns() throws Exception {
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
	    		//((Disposable) col.getValue().chunks[0].allModels).dispose();
	    		//memoriedColumns.put(col.getKey(), col.getValue());
	    	}
	    }
	    //подгружаем недостдавшиеся
	    for (Vector2I c : cl) {
	    	//System.out.println("managing columns: "+i+++"/"+cl.size());
	    	loadedColumns.put(c,genOrLoadRegion(VectorU.ColumnToRegion(c)).getColumn(c));
	    }
	}
	
	public boolean isCycleFree = true;
	public void render() throws Exception {
		if (Hpb.exit) return;
		renderSky();
		for (Entry<Vector2I, Column> col : loadedColumns.entrySet()) {
			col.getValue().renderNormal();
		}
		/*for (Entry<Vector2I, Column> col : loadedColumns.entrySet()) {
			col.getValue().renderTransparent();
		}*/
		for (Entry<Vector2I, Column> col : loadedColumns.entrySet()) {
			col.getValue().renderEntites();
		}
		for (ModelInstance a : additional) {
			Hpb.render(a);
		}
		for (Particle p : particles) {
			p.render();
		}
		player.render();
		isCycleFree = true;
	}
	
	public boolean save() {
		try {
			Hpb.exit = true;
			System.out.println("saving");
			loadedColumns.clear();
			int i = 0;
			for (Entry<Vector2I, Region> region : memoriedRegions.entrySet()) {
				i++;
				System.out.println("сохраняем "+i+" регион из "+memoriedRegions.size());
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
			//System.exit(0);
			return false;
		}
	}
	
	public Region genOrLoadRegion(Vector2I regpos) throws Exception {
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
	public Region readRegion(Vector2I regpos) throws Exception {
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
}
