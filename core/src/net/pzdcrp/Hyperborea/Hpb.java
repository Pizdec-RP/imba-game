package net.pzdcrp.Hyperborea;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.player.ControlListener;
import net.pzdcrp.Hyperborea.utils.ThreadU;
import net.pzdcrp.Hyperborea.world.World;
import net.pzdcrp.Hyperborea.world.elements.Column;
import net.pzdcrp.Hyperborea.world.elements.blocks.Block;
import net.pzdcrp.Hyperborea.world.elements.blocks.Dirt;

public class Hpb extends ApplicationAdapter {
	Texture img;
	public static int renderDistance = 3;
	public static ModelBatch modelBatch;
	public static SpriteBatch spriteBatch;
	public static BitmapFont font;
	private Label label;
	private static final int tickrate = 50;
	public static boolean exit = false;
	public static ControlListener controls;
	public static Stage stage;
	private SuperPizdatiyShader shaderprovider;
	public static OrthographicCamera mCamera;
	
	public static Map<String, Texture> textures = new HashMap<>();
	public static final Vector3 forAnyReason = new Vector3();
	public static World world;
	//public static SpriteBatch gui = new SpriteBatch();
	
	public static Label infoLabel;
	public static InputMultiplexer multiplexer;
	
	@Override
	public void create() {
		System.out.println("loading textures");
		loadTextures();
		System.out.println("lessgo");
		modelBatch = new ModelBatch(shaderprovider = new SuperPizdatiyShader("test"));
		//modelBatch.getShaderProvider().setDefaultShader();
		
		spriteBatch = new SpriteBatch();
		
		BitmapFont font = new BitmapFont(Gdx.files.classpath("com/badlogic/gdx/utils/lsans-15.fnt"), Gdx.files.classpath("com/badlogic/gdx/utils/lsans-15.png"), false);
		//font.getRegion().getTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		label = new Label(" ", new Label.LabelStyle(font, Color.WHITE));
		label.setVisible(true);
		//chat = new Label("", new Label.LabelStyle(font, Color.WHITE));
		
		stage = new Stage();
		
		infoLabel = new Label(" ", new Label.LabelStyle(font, Color.WHITE));
		infoLabel.setPosition(Gdx.graphics.getWidth() / 2, 100);
		infoLabel.setFontScale(1.5f);
		
		stage.addActor(label);
		stage.addActor(infoLabel);
		
		multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(controls = new ControlListener(this));
		Gdx.input.setInputProcessor(multiplexer);
		
		Gdx.input.setCursorCatched(true);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glTexParameterf(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MIN_FILTER, GL20.GL_LINEAR_MIPMAP_LINEAR);
		
		mCamera = new OrthographicCamera();
		mCamera.far = 500;
	    mCamera.setToOrtho(false, 720, 720);
		
		world = new World();
		world.load();
		
		tickLoop();
		//chunkGenLoop();
		
	}
	
	public void tick() {
		world.tick();
	}
	
	static long startdisplay = 0L;
	static boolean disp = false;
	public static void displayInfo(String text) {
		infoLabel.setText(text);
		infoLabel.setAlignment(Align.center);
		startdisplay = System.currentTimeMillis();
		disp = true;
		infoLabel.setVisible(true);
	}
	
	long last = System.currentTimeMillis();
	//public static ModelInstance modelInstance;
	private long delta, now;
	private int en;
	
	public static void render(RenderableProvider obj) {
		modelBatch.render(obj);
	}
	
	public static void render(RenderableProvider obj, Environment env) {
		modelBatch.render(obj, env);
	}
	
	public static float lerp(float a, float b) {
	    float t = (float)(System.currentTimeMillis() - timeone) / tickrate;
	    return a * (1.0f - t) + b * t;
	}
	
	public static double lerp(double a, double b) {
		double t = (System.currentTimeMillis() - timeone) / tickrate;
	    return a * (1.0d - t) + b * t;
	}
	
	@Override
	public void render() {
		if (exit) return;
		try {
			now = System.currentTimeMillis();
			delta = now - last;
			last = now;
			Gdx.gl.glViewport(0, 0, 1280, 720);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
			//world
			modelBatch.begin(world.player.cam.cam);
			world.render();
			modelBatch.end();
			//gui
			modelBatch.begin(mCamera);
			spriteBatch.begin();
			world.player.inventory.render();
			spriteBatch.end();
			modelBatch.end();
			
			StringBuilder builder = new StringBuilder();
			builder.append("onGround: ").append(world.player.onGround);
			builder.append(" time: ").append(world.time);
			builder.append(" col: ").append(world.loadedColumns.size());
			builder.append(" | pos: ").append("x:"+String.format("%.2f",world.player.pos.x)+" y:"+String.format("%.2f",world.player.pos.y)+" z:"+String.format("%.2f",world.player.pos.z));
			en = 0;
			for (Column col : world.loadedColumns.values()) {
				en += col.entites.size();
			}
			builder.append(" | ent: ").append(en);
			builder.append(" | fps: ").append(Gdx.app.getGraphics().getFramesPerSecond());
			builder.append(" | youInCol: ").append(world.player.beforeechc.toString());
			label.setText(builder);
			stage.draw();
			
			if (disp) {
				float dispdelta = (float) (now - startdisplay);
				if (dispdelta < 4000) {//TODO надо сделать чтоб 2 секунды горело а потом плавно исчезало
					infoLabel.setColor(1, 1, 1, 1f-dispdelta/4000);
				} else {
					disp = false;
					infoLabel.setVisible(false);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("render error");
			exit = true;
			world.save();
			System.exit(0);
		}
	}
	
	
	
	@Override
	public void dispose() {
		exit = true;
	}
	
	public static Texture getTexture(String s) {
		return textures.get(s);
	}
	
	@SuppressWarnings("deprecation")
	public static void loadTextures() {
		System.out.println(Gdx.files.getLocalStoragePath());
		JsonObject obj = (JsonObject) new JsonParser().parse(Gdx.files.internal("textureData.json").readString());
		System.out.println("грузим "+obj.keySet().size()+" тектур");
		for (String key : obj.keySet()) {
			Texture texture;
			textures.put(key, texture = new Texture(Gdx.files.internal("textures/"+obj.get(key).getAsString()),true));
			texture.setFilter(TextureFilter.MipMap,TextureFilter.Nearest);
		}
	}
	
	static long timeone = System.currentTimeMillis();
	static int curcomp = 0;
	public void tickLoop() {
		/*new Thread(() -> {
	        int tickRate = 50;
	        long prevTime = System.currentTimeMillis();

	        while (true) {
	            try {
	                currentTime = System.currentTimeMillis();
	                long elapsedTime = currentTime - prevTime;
	                tick();
	                long timeToSleep = tickRate - elapsedTime;
	                if (timeToSleep > 0) {
	                    Thread.sleep(timeToSleep);
	                }
	                prevTime = System.currentTimeMillis();
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	    }).start();*/
		new Thread(()->{
			curcomp = 0;
			int needtocompensate = 0;
			while (true) {
				try {
					if (exit) Thread.currentThread().stop();
					timeone = System.currentTimeMillis();
					tick();
					long timetwo = System.currentTimeMillis();
					int raznica = (int) (timetwo - timeone);
					if (needtocompensate > 5000) {
						needtocompensate = 0;
						//System.out.println("client overloaded, skiped "+needtocompensate/tickrate+" ticks");
					}
					if (raznica > 0 && raznica < tickrate) {
						curcomp = tickrate-raznica;
						//System.out.println("comp "+raznica+"ms");
						if (needtocompensate <= 0) {
							ThreadU.sleep(curcomp);
						} else {
							needtocompensate-=curcomp;
						}
					} else if (raznica == 0){
						if (needtocompensate <= 0) {
							ThreadU.sleep(tickrate);
						} else {
							needtocompensate-=tickrate;
						}
					} else {
						//System.out.println("pass "+raznica+"ms");
						needtocompensate += raznica-tickrate;
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(2);
				}
			}
		}).start();
	}
}
