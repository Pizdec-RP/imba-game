package net.pzdcrp.Hyperborea.world;

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
import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.ActionAuthor;
import net.pzdcrp.Hyperborea.data.Settings;
import net.pzdcrp.Hyperborea.data.Vector2I;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.multiplayer.packets.ClientWorldSuccLoadPacket;
import net.pzdcrp.Hyperborea.player.Player;
import net.pzdcrp.Hyperborea.utils.MathU;
import net.pzdcrp.Hyperborea.utils.VectorU;
import net.pzdcrp.Hyperborea.world.elements.Chunk;
import net.pzdcrp.Hyperborea.world.elements.Column;
import net.pzdcrp.Hyperborea.world.elements.Particle;
import net.pzdcrp.Hyperborea.world.elements.blocks.Air;
import net.pzdcrp.Hyperborea.world.elements.blocks.Block;
import net.pzdcrp.Hyperborea.world.elements.blocks.Voed;
import net.pzdcrp.Hyperborea.world.elements.entities.Entity;
import net.pzdcrp.Hyperborea.world.elements.generators.DefaultWorldGenerator;

public class PlayerWorld implements World {// implements RenderableProvider {
	public Player player;
	public Map<Vector2I,Column> loadedColumns = new ConcurrentHashMap<>();
	public List<ModelInstance> additional = new CopyOnWriteArrayList<>();
	public int time = 0;
	public static boolean ready = false;
	public Matrix4 temp = new Matrix4();
	private ModelInstance sun;
    private ModelInstance moon;
    //private ModelInstance shield;
    public Vector3 lightDirection = new Vector3();
    public static int seed = 228;
    
    private static final int DAY_LENGTH = 60000;
    private static final float DISTANCE_FROM_CENTER = 2000f;
    Material skymaterial;
    
    public ModelInstance particlesModel = new ModelInstance(new Model());
    public List<Particle> particles = new CopyOnWriteArrayList<>();
	
	public boolean needToUpdateLoadedColumns = true;
    
	public PlayerWorld() {
		Block.world = this;
		particlesModel.userData = new Object[] {"particles"};
	}
	
	public void load() {
		System.out.println("подгружаем окружение");
		loadEnvironment();
		PlayerWorld.ready = true;
		System.out.println("все заебок");
		Hpb.session.send(new ClientWorldSuccLoadPacket(Hpb.playerId));
	}

	public void loadEnvironment() {
		
		ModelBuilder modelBuilder = new ModelBuilder();
		skymaterial = new Material(IntAttribute.createCullFace(GL20.GL_FRONT));
		Model model = modelBuilder.createBox(DISTANCE_FROM_CENTER*10, DISTANCE_FROM_CENTER*10, DISTANCE_FROM_CENTER*10, skymaterial, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
		sky = new ModelInstance(model);
		sky.userData = new Object[] {"sky"};
		Matrix4 transform = new Matrix4();
		transform.translate((float)player.pos.x,(float)player.pos.y,(float)player.pos.z);
		sky.transform.set(transform);
		
		modelBuilder = new ModelBuilder();
		Material material = new Material(TextureAttribute.createDiffuse(Hpb.mutex.getOTexture("sun")));
		model = modelBuilder.createBox(700f, 700f, 700f, material, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
		sun = new ModelInstance(model);
		sun.userData = new Object[] {"sun"};
		
		modelBuilder = new ModelBuilder();
		material = new Material(ColorAttribute.createDiffuse(Color.DARK_GRAY));
		model = modelBuilder.createSphere(5f, 5f, 5f, 5, 5, material, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		moon = new ModelInstance(model);
		moon.userData = new Object[] {"moon"};
	}
	
	@Override
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
	
	@Override
	public boolean posDostupna(int x, int y, int z) {
		if (y < 0 || y > maxheight-1) {
			return false;
		}
		return loadedColumns.containsKey(VectorU.xzToColumn(x,z));
	}
	
	static final float bs = 0.2f;
	@Override
	public boolean setBlock(Block block, ActionAuthor author) {
		if (block.pos.y < 0 || block.pos.y >= buildheight) {
			Hpb.displayInfo("build limit reached");
			return false;
		}
		
		if (author == ActionAuthor.player) {
			for (Entry<Vector2I, Column> tcol : loadedColumns.entrySet()) {
				for (Entity en : tcol.getValue().entites) {
					if (block.isCollide() && block.collide(en.getHitbox())) return false;
				}
			}
		}
		Column col = getColumn(block.pos.x,block.pos.z);
		col.setBlock(block);
		for (Block block1 : block.getSides()) {
			block1.onNeighUpdate();
			block1.callChunkUpdate();
		}
		return true;
	}
	
	@Override
	public void spawnEntity(Entity entity) {
		Column spawnin = getColumn(VectorU.posToColumn(entity.pos));
		spawnin.entites.add(entity);
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
	@Override
	public void breakBlock(Vector3D pos) {
		if (pos.y < 0 || pos.y >= buildheight) return;
		Block before = getBlock(pos);
		setBlock(new Air(pos), ActionAuthor.player);
		before.onBreak();
		if (before.isRenderable()) {
			for (int i = 0; i < MathU.rndi(10, 20); i++) {
				spawnParticle(before.texture, pos.translate().add(MathU.rndf(0.3f, 0.7f), MathU.rndf(0.3f, 0.7f), MathU.rndf(0.3f, 0.7f)), new Vector3(MathU.rndf(-bs, bs),MathU.rndf(-bs, bs),MathU.rndf(-bs, bs)), MathU.rndi(8, 16));
			}
		}
	}
	@Override
	public Column getColumn(double x, double z) {
		try {
			return getColumn(new Vector2I((int)Math.floor(x) >> 4, (int)Math.floor(z) >> 4));
		} catch (Exception e) {
			return null;
		}
	}
	@Override
	public Column getColumn(Vector2I cc) {
		return loadedColumns.get(cc);
	}
	@Override
	public int getLight(int x, int y, int z) {
		if (y < 0 || y >= maxheight) return 14;
		Column col = loadedColumns.get(new Vector2I(x>>4, z>>4));
		if (col == null) return 0;
		return col.chunks[y/16].rawGetLight(x&15, y&15, z&15);
	}
	@Override
	public void setLight(int x, int y, int z, int num) {
		if (y < 0 || y >= maxheight) return;
		getColumn(x,z).chunks[y/16].rawSetLight(x&15, y&15, z&15, num);
	}
	@Override
	public Block getBlock(double x, double y, double z) {
		if (y < 0 || y >= maxheight) return new Voed(new Vector3D(x,y,z));
		Column col = getColumn(x,z);
		if (col == null) return new Voed(new Vector3D(x,y,z));
		Block c = col.getBlock((int)x&15,(int)y,(int)z&15);
		if (c == null) return new Voed(new Vector3D(x,y,z));
		return c;
	}
	@Override
	public Block getBlock(Vector3D v) {
		if (v.y < 0 || v.y >= maxheight) return new Voed(v);
		Column col = getColumn(v.x,v.z);
		if (col == null) return new Voed(v);
		Block c = col.getBlock((int)v.x&15,(int)v.y,(int)v.z&15);
		if (c == null) return new Voed(v);
		return c;
	}
	public ModelInstance sky;
	public void renderSky() {
	    //time = (int) time % DAY_LENGTH;//TODO Хз че это надо поправить
	    float px = (float)player.pos.x/*, py = (float)player.pos.y*/, pz = (float)player.pos.z;
	    float angle = 2f * (float) Math.PI * time / DAY_LENGTH;
	    float x = (DISTANCE_FROM_CENTER * MathU.cos(angle) + px) * 2;
	    float y = (DISTANCE_FROM_CENTER * MathU.sin(angle)) * 2;

	    sky.transform.setToTranslation(px,0,pz);
	    //shield.transform.setToTranslation(px,400,pz);

	    Vector3 translation = new Vector3();
	    sun.transform.getTranslation(translation);
	    sun.transform.rotate(1, 1, 1, 0.01f);
	    sun.transform.setTranslation(x, y, pz);

	    Hpb.render(sun);
	    Hpb.render(sky);
	    //Hpb.render(shield);
	}
	
	public void addLC(Column c) {
		loadedColumns.put(c.pos, c);
	}
	@Override
	public void tick() {
		if (!ready || Hpb.exit) {
			return;
		}
		//System.out.println("tick-------------------");
		//deltaTime();
		time++;
		if (time > DAY_LENGTH) time = 0;
		//deltaTime();
		/** now only on server side
		for (Entry<Vector2I, Column> column : loadedColumns.entrySet()) {
			column.getValue().tick();
		}*/
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
	
	private final Comparator<Chunk> chunkComparator = new Comparator<Chunk>() {
	    @Override
	    public int compare(Chunk chunk1, Chunk chunk2) {
	        Vector3 chunk1Pos = new Vector3(chunk1.column.pos.x, chunk1.height, chunk1.column.pos.z);
	        Vector3 chunk2Pos = new Vector3(chunk2.column.pos.x, chunk2.height, chunk2.column.pos.z);
	        float distance1 = chunk1Pos.dst2(player.cam.cam.position); // Расстояние до первого чанка
	        float distance2 = chunk2Pos.dst2(player.cam.cam.position); // Расстояние до второго чанка
	        return Float.compare(distance2, distance1); // Сортировка в обратном порядке
	    }
	};
	public boolean isCycleFree = true;
	public void render() {
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
			if (!Vector3D.ZERO.equals(player.vel)/* || c.needUpdateTransp())*/ && VectorU.sqrt(new Vector3D(player.echc.x, player.pos.y/16, player.echc.z), c.pos) <= Settings.updatingDistance) c.rebuildTransparent();
			if (c.transparent != null) Hpb.render(c.transparent);
		}
		for (ModelInstance a : additional) {
			Hpb.render(a);
		}
		for (Particle p : particles) {
			p.render();
		}
		Hpb.render(particlesModel);
		player.render();
		isCycleFree = true;
	}
	
	@Override
	public boolean isLocal() {
		return true;
	}
}
