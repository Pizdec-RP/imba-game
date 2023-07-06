package net.pzdcrp.Hyperborea.world;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.ActionAuthor;
import net.pzdcrp.Hyperborea.data.MBIM;
import net.pzdcrp.Hyperborea.data.Settings;
import net.pzdcrp.Hyperborea.data.Vector2I;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.player.Player;
import net.pzdcrp.Hyperborea.utils.MathU;
import net.pzdcrp.Hyperborea.utils.VectorU;
import net.pzdcrp.Hyperborea.world.elements.Chunk;
import net.pzdcrp.Hyperborea.world.elements.Column;
import net.pzdcrp.Hyperborea.world.elements.Particle;
import net.pzdcrp.Hyperborea.world.elements.Region;
import net.pzdcrp.Hyperborea.world.elements.Weather;
import net.pzdcrp.Hyperborea.world.elements.blocks.Air;
import net.pzdcrp.Hyperborea.world.elements.blocks.Block;
import net.pzdcrp.Hyperborea.world.elements.blocks.Glass;
import net.pzdcrp.Hyperborea.world.elements.blocks.Voed;
import net.pzdcrp.Hyperborea.world.elements.entities.Entity;
import net.pzdcrp.Hyperborea.world.elements.generators.ColumnGenerator;
import net.pzdcrp.Hyperborea.world.elements.generators.DefaultWorldGenerator;

public class World {// implements RenderableProvider {
	public Player player;
	public Map<Vector2I,Column> loadedColumns = new ConcurrentHashMap<>();
	public Map<Vector2I,Region> memoriedRegions = new ConcurrentHashMap<>();
	public List<ModelInstance> additional = new CopyOnWriteArrayList<>();
	public int time = 0;
	public static final int chunks = 16, maxheight = chunks * 16, buildheight = maxheight-1;
	public static boolean ready = false;
	public Matrix4 temp = new Matrix4();
	
	public Vector3 sunPosition = new Vector3();
	public ModelInstance sun;
	
    public Vector3 moonPosition = new Vector3();
    public ModelInstance moon;
    public Vector3 lightDirection = new Vector3();
    public static int seed = 228;
    
    private static final int DAY_LENGTH = 60000;
    private static final float DISTANCE_FROM_CENTER = 200f;
    Material skymaterial;
    
    public ModelInstance particlesModel = new ModelInstance(new Model());
    public List<Particle> particles = new CopyOnWriteArrayList<>();
	public Weather weather;
	public ColumnGenerator generator;
	
	public boolean needToUpdateLoadedColumns = true;
	public int skylight = 13;
    
	public World() {
		Block.world = this;
		this.weather = new Weather(this);
		generator = new DefaultWorldGenerator();
	}
	
	public void load() throws Exception {
		System.out.println("подгружаем мир");
		boolean newworld = false;
		try {
			JsonReader reader = new JsonReader(new FileReader("save/wdata.dat"));
			JsonObject obj = (JsonObject) new JsonParser().parse(reader);
			
			this.time = obj.get("time").getAsInt();
			this.skylight = obj.get("skylight").getAsInt();
			Vector2I cpos = Vector2I.fromString(obj.get("playercol").getAsString());
			Vector2I rpos = Vector2I.fromString(obj.get("playerreg").getAsString());
			
			this.loadedColumns.put(cpos, genOrLoadRegion(rpos).getColumn(cpos));
			player.beforeechc = cpos;
			weather.fromJson(obj.get("weather").getAsJsonObject());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("данных о мире нет");
			player = new Player(5,maxheight/2,5);
			weather.initAsNew();
			newworld = true;
		}
		updateLoadedColumns(VectorU.posToColumn(player.pos));
		player.tick();
		if (newworld) player.teleport(new Vector3D(player.pos.x+0.5d, loadedColumns.get(player.echc).getSLMD((int)player.pos.x, (int)player.pos.z)+2, player.pos.z+0.5d));
		System.out.println("подгружаем окружение");
		loadEnvironment();
		World.ready = true;
		System.out.println("все заебок");
	}

	public void loadEnvironment() {
		
		ModelBuilder modelBuilder = new ModelBuilder();
		skymaterial = new Material(ColorAttribute.createDiffuse(0, 0, 255, 255), IntAttribute.createCullFace(GL20.GL_NONE));
		Model model = modelBuilder.createBox(DISTANCE_FROM_CENTER*5, DISTANCE_FROM_CENTER*5, DISTANCE_FROM_CENTER*5, skymaterial, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
		sky = new ModelInstance(model);
		sky.userData = new Object[] {"sky"};
		Matrix4 transform = new Matrix4();
		transform.translate((float)player.pos.x,(float)player.pos.y,(float)player.pos.z);
		sky.transform.set(transform);
		
		modelBuilder = new ModelBuilder();
		Material material = new Material(TextureAttribute.createDiffuse(Hpb.mutex.getOTexture("sun")));
		model = modelBuilder.createBox(60f, 60f, 60f, material, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
		sun = new ModelInstance(model);
		sun.userData = new Object[] {"sun"};
		
		modelBuilder = new ModelBuilder();
		material = new Material(ColorAttribute.createDiffuse(Color.DARK_GRAY));
		model = modelBuilder.createSphere(5f, 5f, 5f, 5, 5, material, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		moon = new ModelInstance(model);
		moon.userData = new Object[] {"moon"};
		
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
	
	public boolean posDostupna(int x, int y, int z) {
		if (y < 0 || y > maxheight-1) {
			return false;
		}
		return loadedColumns.containsKey(VectorU.xzToColumn(x,z));
	}
	
	static final float bs = 0.2f;
	public void setBlock(Block block, ActionAuthor author) {
		if (block.pos.y < 0 || block.pos.y >= buildheight) {
			Hpb.displayInfo("build limit reached");
			return;
		}
		
		if (author == ActionAuthor.player) {
			for (Entry<Vector2I, Column> tcol : loadedColumns.entrySet()) {
				for (Entity en : tcol.getValue().entites) {
					if (block.isCollide() && block.collide(en.getHitbox())) return;
				}
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
		Column col = getColumn(block.pos.x,block.pos.z);
		col.setBlock(block);
		for (Block block1 : block.getSides()) {
			block1.onNeighUpdate();
			block1.callChunkUpdate();
		}
	}
	
	static final int maxpart = 1000;
	public void spawnParticle(String tname, Vector3 pos, Vector3 vel, int lifetime) {
		if (particles.size() >= maxpart) {
			return;
		} else if (particles.size()/2 >= maxpart) {
			if (Math.random() > 0.5) return;
		}
		particles.add(new Particle(Hpb.mutex.getBlockTexture(tname), pos, vel, lifetime));
	}
	
	public void breakBlock(Vector3D pos) {
		if (pos.y < 0 || pos.y >= buildheight) return;
		//спавн партиклов тут по идее
		setBlock(new Air(pos), ActionAuthor.player);
	}
	
	public Column getColumn(double x, double z) {
		try {
			return getColumn(new Vector2I((int)Math.floor(x) >> 4, (int)Math.floor(z) >> 4));
		} catch (Exception e) {
			return null;
		}
	}
	
	public Column getColumn(Vector2I cc) throws Exception {
		if (loadedColumns.containsKey(cc))
			return loadedColumns.get(cc);
		else return this.genOrLoadRegion(VectorU.ColumnToRegion(cc)).getColumn(cc);
	}
	
	public int getLight(int x, int y, int z) {
		if (y < 0 || y >= maxheight) return 14;
		try {
			Column col = loadedColumns.get(new Vector2I(x>>4, z>>4));
			if (col == null) return 0;
			return col.chunks[y/16].rawGetLight(x&15, y&15, z&15);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
			return 0;
		}
	}
	
	public void setLight(int x, int y, int z, int num) {
		if (y < 0 || y >= maxheight) return;
		try {
			getColumn(x,z).chunks[y/16].rawSetLight(x&15, y&15, z&15, num);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public Block getBlock(double x, double y, double z) {
		if (y < 0 || y >= maxheight) return new Voed(new Vector3D(x,y,z));
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
		if (v.y < 0 || v.y >= maxheight) return new Voed(v);
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
	
	public int calculateSkylight(double abstracty) {
	    if (abstracty < -50) {
	    	Hpb.shaderprovider.skylightlevel = 0f;
	    	return skylight;
	    } else if (abstracty > 50) {
	    	Hpb.shaderprovider.skylightlevel = 1f;
	        return skylight;
	    }
	    double norm = (abstracty - (-50)) / (50 - (-50));
	    int skylight = (int) (minSkylight + norm * (maxSkylight - minSkylight));
	    Hpb.shaderprovider.skylightlevel = (float)norm;
	    return skylight;
	}
	
	final int minSkylight = 1;
    final int maxSkylight = 13;
	public ModelInstance sky;
	public void renderSky() {
	    time = (int) time % DAY_LENGTH;
	    float px = (float)player.pos.x, py = (float)player.pos.y, pz = (float)player.pos.z;
	    float angle = 2f * (float) Math.PI * time / DAY_LENGTH;
	    float x = (DISTANCE_FROM_CENTER * MathU.cos(angle) + px);
	    float y = (DISTANCE_FROM_CENTER * MathU.sin(angle) + py);

	    sky.transform.setToTranslation(px,py,pz);

	    Vector3 translation = new Vector3();
	    sun.transform.getTranslation(translation);
	    sun.transform.rotate(1, 1, 1, 0.01f);
	    sun.transform.setTranslation(x, y, pz);
	    
	    double abstracty = y - player.pos.y;
	    int newSkylight = calculateSkylight(abstracty);
	    if (newSkylight != skylight) {
	        skylight = newSkylight;
	        for (Region r : memoriedRegions.values()) {
		        for (Column col : r.columns.values()) {
		            for (Chunk chunk : col.chunks) {
		                chunk.inlightupd = true;//TODO желательно сделать чтобы они обновлялись не зависимо от фрустрации
		            }
		        }
	        }
	    }

	    Hpb.render(sun);
	    Hpb.render(sky);
	}
	
	public void addLC(Column c) {
		loadedColumns.put(c.pos, c);
		if (DefaultWorldGenerator.toadd.containsKey(c.pos)) {
    		for (Block b : DefaultWorldGenerator.toadd.get(c.pos)) {
    			c.setBlock(b);
    		}
    		DefaultWorldGenerator.toadd.remove(c.pos);
    	}
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
	
	//Set<Vector2I> updcol = new HashSet<>();
	public void updateLoadedColumns(Vector2I echc) throws Exception {
		System.out.println("needToUpdateLoadedColumns");
	    Set<Vector2I> cl = new HashSet<>();
	    for (int x = -Settings.renderDistance; x <= Settings.renderDistance; x++) {
            for (int z = -Settings.renderDistance; z <= Settings.renderDistance; z++) {
                Vector2I vector = new Vector2I(echc.x + x, echc.z + z);
                cl.add(vector);
            }
        }
	    //updcol.addAll(VectorU.generateVectorsInRadius(player.echc, Settings.updatingDistance));
	    
	    // Хранит ключи столбцов, которые нужно удалить
	    List<Vector2I> toRemove = new ArrayList<>();
	    // Проверяем какие столбцы уже загружены и убираем из cl то что есть
	    for (Entry<Vector2I, Column> col : loadedColumns.entrySet()) {
	        if (cl.contains(col.getValue().pos)) {
	            cl.remove(col.getValue().pos);
	        } else { // отмечаем для удаления столбцы, которых нет в cl
	            toRemove.add(col.getKey());
	        }
	    }
	    // Удаляем столбцы
	    for (Vector2I key : toRemove) {
	        loadedColumns.remove(key);
	    }
	    // подгружаем недоставшиеся
	    for (Vector2I c : cl) {
	    	addLC(genOrLoadRegion(VectorU.ColumnToRegion(c)).getColumn(c));
	    }
	}
	Comparator<Chunk> chunkComparator = new Comparator<Chunk>() {
	    @Override
	    public int compare(Chunk chunk1, Chunk chunk2) {
	        Vector3 chunk1Pos = new Vector3(chunk1.column.pos.x, chunk1.height, chunk1.column.pos.z);
	        Vector3 chunk2Pos = new Vector3(chunk2.column.pos.x, chunk2.height, chunk2.column.pos.z);
	        float distance1 = chunk1Pos.dst2(player.cam.cam.position); // Расстояние до первого чанка
	        float distance2 = chunk2Pos.dst2(player.cam.cam.position); // Расстояние до второго чанка
	        return Float.compare(distance2, distance1); // Сортировка в обратном порядке
	    }
	};
	
	public void fromChunkUpdateThread() {
		for (Column col : loadedColumns.values()) {
			for (Chunk chunk : col.chunks) {
				if (chunk.inlightupd) {
					chunk.updateLightMain();
					chunk.inlightupd = false;
					chunk.outlightupd = true;
					Hpb.world.isCycleFree = false;
				}
			}
		}
		if (Hpb.world.isCycleFree) {
			for (Column col : loadedColumns.values()) {
				for (Chunk chunk : col.chunks) {
					if (chunk.outlightupd) {
						chunk.updateLightFromOutbounds();
						Hpb.world.isCycleFree = false;
						chunk.outlightupd = false;
						chunk.updateModel();
					}
				}
			}
		}
	}
	
	public boolean isCycleFree = true;
	public void render() throws Exception {
		if (Hpb.exit) return;
		byte upd = 0;
		for (Column col : loadedColumns.values()) {
			for (Chunk chunk : col.chunks) {
				if (chunk.reqmodelupd && chunk.boundsInFrustum()) {
					upd++;
					chunk.lUpdateModel();
					chunk.reqmodelupd=false;
					if (upd > 3) break;
				}
			}
			if (upd > 3) break;
		}
		renderSky();
		for (Column col : loadedColumns.values()) {
			col.renderNormal();
			col.renderEntites();
			/*if (!player.pos.equals(player.beforepos)) {
				for (Chunk c : col.chunks) {
					c.rebuildTransparent();
				}
			}
			col.renderTransparent();*/
		}
		
		Set<Chunk> notSorted = new HashSet<>();
		for (Column col : loadedColumns.values()) {
			if (col.isInFrustum()) {
				for (Chunk chunk : col.chunks) {
					if (chunk.boundsInFrustum()) notSorted.add(chunk);
				}
			}
		}
		List<Chunk> sortedChunks = new ArrayList<>(notSorted);
		Collections.sort(sortedChunks, chunkComparator);
		for (Chunk c : sortedChunks) {
			if (!Vector3D.ZERO.equals(player.vel) && VectorU.sqrt(new Vector3D(player.echc.x, player.pos.y/16, player.echc.z), c.pos) <= Settings.updatingDistance) c.rebuildTransparent();
			if (c.transparent != null) Hpb.render(c.transparent);
		}
		//System.out.println(Vector3D.ZERO.equals(player.vel));
		for (ModelInstance a : additional) {
			Hpb.render(a);
		}
		for (Particle p : particles) {
			p.render();
		}
		//particlesModel.calculateTransforms();
		Hpb.render(particlesModel);
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
			jwd.add("weather", weather.toJson());
			jwd.addProperty("skylight", this.skylight);
			
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
