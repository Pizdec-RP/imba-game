package net.pzdcrp.Hyperborea.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.extended.SexyMeshBuilder;
import net.pzdcrp.Hyperborea.utils.MathU;
import net.pzdcrp.Hyperborea.utils.ModelUtils;
import net.pzdcrp.Hyperborea.utils.RenderaU;
import net.pzdcrp.Hyperborea.utils.GameU;
import net.pzdcrp.Hyperborea.world.elements.blocks.Block;
import net.pzdcrp.Hyperborea.world.elements.inventory.items.Item;
import net.pzdcrp.Hyperborea.world.elements.inventory.items.NoItem;

import com.badlogic.gdx.graphics.Texture;

public class Mutex {
	private Texture comp;
	private Map<String, Texture> ar;
	private Map<String, float[]> razmetka;
	private Map<String, Texture> itemtextures = new HashMap<>();
	private Map<String, Texture> otherTextures = new HashMap<>();
	private FreeTypeFontGenerator generator;
	private Map<Integer, BitmapFont> fonts = new ConcurrentHashMap<>();
	
	public Mutex() {
		this.generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Underdog.ttf"));
	}
	
	public Texture getComplex() {
		return comp;
	}
	
	public void begin() {
		ar = new HashMap<>();
		razmetka = new HashMap<>();
	}
	
	public void end() {
		int height = 0;
		int width = 0;
		for (Texture tex : ar.values()) {
			if (tex.getHeight() > height) height = tex.getHeight();
			width += tex.getWidth();
		}
		int max = Math.max(height, width);
		int aligned = MathU.alignPower(max);
		width = aligned;
		height = aligned;
		Pixmap pixmap = new Pixmap(width, height, Format.RGBA8888);
		System.out.println("generated texture image: "+width+"x"+height);
		int twidth = 0;
		TextureData dt;
		for (Entry<String, Texture> tex : ar.entrySet()) {
			//tex.getValue().setFilter(TextureFilter.MipMap,TextureFilter.Nearest);
			dt = tex.getValue().getTextureData();
			dt.prepare();
			//comp.draw(dt.consumePixmap(), twidth, 0);
			pixmap.drawPixmap(dt.consumePixmap(), twidth, 0);
			float[] razm = new float[4];
			razm[1] = 0;
			float hep = (float)tex.getValue().getHeight() / (float)height;
			razm[3] = hep;
			razm[0] = (float)twidth / (float)width;
			twidth+=tex.getValue().getWidth();
			razm[2] = (float)twidth / (float)width;
			razmetka.put(tex.getKey(), razm);
		}
		comp = new Texture(new PixmapTextureData(pixmap, Format.RGBA8888, false, false));
		comp.setAnisotropicFilter(GL30.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT);
		comp.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
	}
	
	public void addOtherTexture(Texture t, String name) {
		otherTextures.put(name, t);
	}
	
	public Texture getOTexture(String name) {
		return otherTextures.get(name);
	}
	
	public void addTexture(Texture t, String name) {
		ar.put(name,t);
	}
	
	public Texture getBlockTexture(String name) {
		return ar.get(name);
	}
	
	public void hookuvr(SexyMeshBuilder mpb, String name, float u1, float v1, float u2, float v2) {
		float[] r = razmetka.get(name);
		if (r == null) {
			GameU.end("unregistered texture: "+name);
		}
		float width = r[2] - r[0];
		float height = r[3] - r[1];
		
		float nu1 = r[0]+width*u1, nv1 = r[1]+height*v1, nu2 = r[0]+width*u2, nv2 = r[1]+height*v2;
		
		mpb.setUVRange(nu1, nv1,nu2,nv2);
	}
	
	public static Texture getBlockTexture(Model model) {
	    int width = 500, height = 500;

	    ModelBatch modelBatch = new ModelBatch();
	    Environment environment = new Environment();
	    environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
	    environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

	    OrthographicCamera camera = new OrthographicCamera();
	    camera.near = 0.1f;
	    camera.far = 300f;
	    camera.position.set(2f, 2f, 2f);
	    camera.lookAt(0f, 0f, 0f);
	    camera.update();

	    ModelInstance instance = new ModelInstance(model);

	    FrameBuffer buffer = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, true);
	    buffer.begin();

	    Gdx.gl.glClearColor(0, 0, 0, 0);
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
	    Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

	    modelBatch.begin(camera);
	    modelBatch.render(instance, environment);
	    modelBatch.end();

	    Pixmap pixmap = ScreenUtils.getFrameBufferPixmap(0, 0, width, height);
	    buffer.end();
	    buffer.dispose();
	    modelBatch.dispose();

	    Texture texture = new Texture(pixmap);
	    pixmap.dispose();

	    return texture;
	}
	
	ModelBatch batch;
	private Camera camera;
	private FrameBuffer fbo;
	private SpriteBatch sbatch;
	
	public void prepare() {
    	batch = new ModelBatch();
    	
    	camera = new OrthographicCamera(2*0.75f, 2.3f*0.75f);
		camera.far = 500;
		camera.near = 0.1f;
	    camera.position.set(-4.28f, 6.18f, 0.96f);
	    camera.direction.set(0.54f,-0.646f,0.52f);
	    
	    fbo = new FrameBuffer(Format.RGBA8888, 500, 500, true);
	    
	    Hpb.loadTextures();
	    
	    sbatch = new SpriteBatch();
    }

    public void render() {
    	MBIM m = new MBIM(null);
    	for (Item item : Item.items.values()) {
    		System.out.println("rendered 2d image of block projection "+item.getClass().getName());
	    	if (item.isModel()) {
	    		Block block = Block.blockByItem(item).clone(new Vector3D(0,0,5));
	    		if (block == null) continue;
	    		block.addModel(false, false, false, false, false, false, m);
	    		ModelInstance model;
	    		if (block.isTransparent()) {
	    			model = m.endTransparent();
	    		    m.sortTransparent(camera.position);
	    		} else
	    			model = m.endSolid();
	    	    
	    	    fbo.begin();
	            Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	            Gdx.gl.glClearColor(0, 0, 0, 0);
	            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
	            
	            camera.update();
	            
	            batch.begin(camera);
	            batch.render(model);
	            batch.end();
	            
	            Pixmap pixmap = Pixmap.createFromFrameBuffer(0, 0, fbo.getWidth(), fbo.getHeight());
	            
	            Texture texture = new Texture(pixmap);
	            itemtextures.put(item.getName(), RenderaU.flip(texture, false, true));
	            
	            fbo.end();
	            
	            sbatch.begin();
	            sbatch.draw(texture, 0, 0);
	            sbatch.end();
	            m.clear();
	    	}
	    }
    	GameU.log("rendered items: "+itemtextures.size());
    	Vector3D pos = new Vector3D(0, 0, 0);
    	ModelUtils.setScale(0.3f);
    	NotMBIM mm = new NotMBIM();
    	for (Entry<Integer, Block> b : Block.blocks.entrySet()) {
    		Item blockItem = Block.itemByBlockId(b.getKey());
    		if (blockItem == null || blockItem instanceof NoItem) continue;
    		Block b1 = b.getValue().clone(pos);
    		b1.addModel(false, false, false, false, false, false, mm);
    		ModelInstance model = mm.end();
    		model.userData = new Object[] {"item", 0f};
    		Block.blockModels.put(b.getKey(), model);
    		GameU.log("added block model: "+b.getKey());
    		mm.clear();
    	}
    	ModelUtils.setScale(1f);
    }
    
    public Texture getItemTexture(String key) {
    	Texture tex = this.itemtextures.get(key);
    	if (tex == null) {
    		GameU.end("нема текстуры! "+this.getClass().getName());
    	}
    	return tex;
    }
    
    public void addItemTexture(Texture t, String key) {
    	itemtextures.put(key, t);
    }
    
    public void endrender() {
    	batch.dispose();
    	camera = null;
    	fbo.dispose();
    	sbatch.dispose();
    }
    
	public BitmapFont getFont(int i) {
		if (fonts.containsKey(i)) {
			return fonts.get(i);
		} else {
			try {
				FreeTypeFontParameter parameter = new FreeTypeFontParameter();
				parameter.size = i;
				parameter.minFilter = TextureFilter.Linear;
				parameter.magFilter = TextureFilter.Linear;
				parameter.genMipMaps = true;
				parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "йцукенгшщзхъфывапролджэячсмитьбюЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮ";
				BitmapFont font = generator.generateFont(parameter);
				fonts.put(i, font);
				return font;
			} catch (Exception e) {
				GameU.end("error while generating font: "+e.getMessage());
				return null;
			}
		}
	}
}
