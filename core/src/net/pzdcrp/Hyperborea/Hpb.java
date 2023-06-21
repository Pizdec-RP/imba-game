package net.pzdcrp.Hyperborea;

import java.util.HashMap;
import java.util.Map;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Timer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.pzdcrp.Hyperborea.data.Mutex;
import net.pzdcrp.Hyperborea.player.ControlListener;
import net.pzdcrp.Hyperborea.utils.ThreadU;
import net.pzdcrp.Hyperborea.world.World;
import net.pzdcrp.Hyperborea.world.elements.Column;
import net.pzdcrp.Hyperborea.world.elements.blocks.Block;
import net.pzdcrp.Hyperborea.world.elements.blocks.Stone;

public class Hpb extends ApplicationAdapter {
	Texture img;
	public static int renderDistance = 3;
	private static ModelBatch modelBatch;
	public static SpriteBatch spriteBatch;
	public static BitmapFont font;
	private Label label;
	private static final int tickrate = 50;
	public static boolean exit = false;
	public static ControlListener controls;
	public static Stage stage;
	public static SuperPizdatiyShader shaderprovider;
	public static OrthographicCamera mCamera;
	public static Mutex mutex = new Mutex();
	
	public static final Vector3 forAnyReason = new Vector3();
	public static World world;
	//public static SpriteBatch gui = new SpriteBatch();
	
	public static Label infoLabel;
	public static InputMultiplexer multiplexer;
	//private static ShaderProgram sp;
	public static FrameBuffer buffer;
	public static TextureRegion textureRegion;
	
	public State state = State.PREPARE;
	
	public enum State {
		PREPARE, INGAME
	}
	
	@Override
	public void create() {
		System.out.println("loading textures");
		loadTextures();
		System.out.println("lessgo");
		modelBatch = new ModelBatch(shaderprovider = new SuperPizdatiyShader());
		
		spriteBatch = new SpriteBatch();
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Underdog.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 30;
		parameter.minFilter = TextureFilter.Linear;
		parameter.magFilter = TextureFilter.Linear;
		parameter.genMipMaps = true;
		parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "йцукенгшщзфывапролджэячсмитьбюъё";
		font = generator.generateFont(parameter);
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
	    
		
		
		buffer = new FrameBuffer(Pixmap.Format.RGBA8888, 1280, 720, true);
		textureRegion = new TextureRegion();
		textureRegion.flip(false, true);
		Thread.currentThread().setName("main thd");
	}
	
	public void tick() throws Exception {
		world.tick();
	}
	
	long last = System.currentTimeMillis();
	//public static ModelInstance modelInstance;
	private static long /*delta, */now;
	private static int en;
	
	public static float lerp(float a, float b) {
	    float t = (float)(System.nanoTime() - timeone) / (tickrate * 1000000);
	    if (t > 1f) t = 1f;
	    return a * (1.0f - t) + b * t;
	}
	
	static String fullText;
    static String currentText = "";
    static int currentIndex = 0;
    static float delay = 0.03f;
    static float vanishDelay = 5f;
    static Timer.Task textTimer;
    static GlyphLayout layout = new GlyphLayout();
    
    public static void displayInfo(String text) {
        if(textTimer != null) {
            textTimer.cancel();
        }

        fullText = text;
        currentIndex = 0;
        currentText = "";

        textTimer = Timer.schedule(new Timer.Task(){
            @Override
            public void run() {
                if(currentIndex < fullText.length()) {
                    currentText += fullText.charAt(currentIndex);
                    currentIndex++;
                } else {
                    this.cancel();
                    Timer.schedule(new Timer.Task(){
                        @Override
                        public void run() {
                            currentText = "";
                        }
                    }, vanishDelay);
                }
            }
        }, delay, delay);
    }
	
	public void renderWorld() throws Exception {
		//хуцня с обычным рендером
		//buffer.begin();
		
		//Gdx.gl.glViewport(0, 0, 1280, 720);
		
		//1 стадия
		
		shaderprovider.newstage();
		modelBatch.begin(world.player.cam.cam);
		world.render();
		modelBatch.end();
		
		//2 стадия
		
		/*shaderprovider.newstage();
		
		textureRegion.setRegion(buffer.getColorBufferTexture());
		shaderprovider.stage2pic = textureRegion.getTexture();
		
		buffer.end();
		
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		modelBatch.begin(world.player.cam.cam);
		world.render();
		modelBatch.end();*/
		
		shaderprovider.end();
		
		//отрисовка gui
		
		//Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		modelBatch.begin(mCamera);
		spriteBatch.begin();
		
		//displayinfo
		layout.setText(font, currentText);
	    float textWidth = layout.width;
		font.draw(spriteBatch, currentText, Gdx.graphics.getWidth()/2 - textWidth / 2, Gdx.graphics.getHeight()*0.2f);
		//end
		
		//spriteBatch.draw(mutex.comp, 0,0,mutex.comp.getWidth(),mutex.comp.getHeight(), 0, 0, mutex.comp.getWidth(),mutex.comp.getHeight(), false, true);
		world.player.inventory.render();
		spriteBatch.end();
		modelBatch.end();
	}
	
	public static void render(ModelInstance obj) {
		modelBatch.render(obj);
	}
	
	@Override
	public void render() {
		if (exit) {
			return;
		}
		try {
			if (state == State.PREPARE) {
				int bw = Gdx.graphics.getWidth(), bh = Gdx.graphics.getHeight();
				Gdx.graphics.setWindowedMode(500, 500);
				mutex.prepare();
				mutex.render();
				mutex.endrender();
				state = State.INGAME;
				Gdx.graphics.setWindowedMode(bw, bh);
				world = new World();
				try {
					world.load();
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("мир не подгружается");
					System.exit(0);
				}
				tickLoop();
			} else if (state == State.INGAME) {
				now = System.currentTimeMillis();
				//delta = now - last;
				last = now;
				
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
				
				renderWorld();
				
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
				stage.act(Gdx.graphics.getDeltaTime());
				stage.draw();
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
	
	@SuppressWarnings("deprecation")
	public static void loadTextures() {
		mutex.begin();
		System.out.println(Gdx.files.getLocalStoragePath());
		JsonObject obj = (JsonObject) new JsonParser().parse(Gdx.files.internal("textureData.json").readString());
		
		JsonObject blocks = obj.get("blocks").getAsJsonObject();
		for (String key : blocks.keySet()) {
			Texture texture = new Texture(Gdx.files.internal("textures/blocks/"+blocks.get(key).getAsString()),Format.RGBA8888,true);
			texture.setFilter(TextureFilter.MipMap,TextureFilter.Nearest);
			mutex.addTexture(texture, key);
		}
		
		JsonObject other = obj.get("other").getAsJsonObject();
		for (String key : other.keySet()) {
			Texture texture  = new Texture(Gdx.files.internal("textures/other/"+other.get(key).getAsString()),Format.RGBA8888,true);
			texture.setFilter(TextureFilter.MipMap,TextureFilter.Nearest);
			mutex.addOtherTexture(texture, key);
		}
		
		JsonObject items = obj.get("items").getAsJsonObject();
		for (String key : items.keySet()) {
			Texture texture  = new Texture(Gdx.files.internal("textures/items/"+items.get(key).getAsString()),Format.RGBA8888,true);
			texture.setFilter(TextureFilter.MipMap,TextureFilter.Nearest);
			mutex.addItemTexture(texture, key);
		}
		
		mutex.end();
	}
	
	public static void runChunkUpdate() {
		new Thread(() -> {
	        while (true) {
	            try {
	            	if (exit) Thread.currentThread().stop();
	                world.updateLoadedColumns();
	                ThreadU.sleep(200);
	            } catch (Exception e) {
	            	e.printStackTrace();
					System.exit(2);
	            }
	        }
	    }).start();
	}
	
	static long timeone = System.nanoTime();
	static int curcomp = 0;
	public void tickLoop() {
		runChunkUpdate();
		
		new Thread(() -> {
			try {
				while (true) {
					if (exit) return;
		        	timeone = System.nanoTime();
		    	    tick();
		    	    long two = System.nanoTime();
		    	    int elapsed = (int)(two - timeone);
		    	    int normaled = elapsed/1_000_000;
		    	    int additional = elapsed/100_000-normaled*10;
		    	    int itog = normaled + (additional >= 5 ? 1 : 0);
		    	    int tosleep = tickrate - itog;
		    	    if (tosleep > 0) {
		    	    	ThreadU.sleep(tosleep);
		    	    }
		        }
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
	    }).start();
	}
	
	public static void onCommand(String rawcommand) {
		if (rawcommand.split(" ").length == 0) return;
		String command = rawcommand.split(" ")[0].replace("/", "");
		String[] args = rawcommand.split(" ");
		
		//System.out.println(command);
		if (command.equals("stop")) {
			if (Hpb.world.save()) {
				System.out.println("всё");
				System.exit(0);
			} else {
				System.out.println("откат, сохранение высрало ошибку");
			}
		} else if (command.equals("save")) {
			Hpb.world.save();
			exit=false;
		} else if (command.equals("setblock")) {
			world.setBlock(new Stone(world.player.pos.clone().func_vf()));
			world.player.chat.addMessage("setted");
		} else if (command.equals("gl")) {
			if (args.length < 4) {
				world.player.chat.addMessage("no args");
			} else {
				int x = Integer.parseInt(args[1]);
				int y = Integer.parseInt(args[2]);
				int z = Integer.parseInt(args[3]);
				int light = world.getLight(x, y, z);
				world.player.chat.addMessage("light at ["+x+" "+y+" "+z+"] is "+light);
			}
		} else if (command.equals("weather")) {
			String type = args[1];
			switch (type) {
			case "rain":
				return;//TODO
			}
		}
	}
}
