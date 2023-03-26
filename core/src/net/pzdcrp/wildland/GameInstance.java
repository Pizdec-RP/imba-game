package net.pzdcrp.wildland;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import net.pizdecrp.game.test.shadowMapping.DLightShader;
import net.pizdecrp.game.test.shadowMapping.ShadowShader;
import net.pzdcrp.wildland.player.ControlListener;
import net.pzdcrp.wildland.utils.ThreadU;
import net.pzdcrp.wildland.world.World;
import net.pzdcrp.wildland.world.elements.Column;

public class GameInstance extends ApplicationAdapter {
	public static SpriteBatch sproteBatch;
	Texture img;
	public static int renderDistance = 3;
	public static ModelBatch modelBatch;
	public static SpriteBatch spriteBatch;
	int i = 0;
	private BitmapFont font;
	private Label crosshair;
	private Label label;
	public static boolean exit = false;
	public static ControlListener controls;
	private Stage stage;
	
	public static Map<String, Texture> textures = new HashMap<>();
	public static final Vector3 forAnyReason = new Vector3();
	public static World world;
	//public static SpriteBatch gui = new SpriteBatch();
	public static ShaderProgram dsp;
	
	public static Label infoLabel;
	
	
	public static Texture depthMap;
	public static net.pizdecrp.game.test.shadowMapping.DirectionalLight dLight;
	public static Camera dLightCam;
	public static FrameBuffer fboDLight;
	public static ShadowShader SP;
	public static DLightShader dLightSP;
	
	@Override
	public void create() {
		System.out.println("loading textures");
		loadTextures();
		System.out.println("lessgo");
		
		world = new World();
		//Gdx.files.internal("vertexShader.vert"),Gdx.files.internal("fragmentShader.frag")
		
		//dsp = new ShaderProgram(Gdx.files.internal("vertexShader.vert"),Gdx.files.internal("fragmentShader.frag"));
		
		
		modelBatch = new ModelBatch(/*new SuperPizdatiyShader("test")*/);
		//modelBatch.getShaderProvider().setDefaultShader();
		
		SP = new ShadowShader();
		SP.init();
		dLightSP = new DLightShader();
		dLightSP.init();
		int DEPTH_SIZE = 512;
		depthMap = new Texture(DEPTH_SIZE, DEPTH_SIZE, Pixmap.Format.RGBA8888);
		dLight = new net.pizdecrp.game.test.shadowMapping.DirectionalLight(new Vector3(0.0f, -0.7f, 0.0f), new Vector3(0.8f, 0.8f, 0.8f));
		dLightCam = new PerspectiveCamera(67, DEPTH_SIZE, DEPTH_SIZE);
		dLightCam.position.set(world.player.getEyeLocation().add(0,-50,0).translate());
		dLightCam.lookAt(new Vector3(0.0f, 0.0f, 0.0f));
		dLightCam.far = 1000f;
		dLightCam.update();
		fboDLight = new FrameBuffer(Pixmap.Format.RGB888, DEPTH_SIZE, DEPTH_SIZE, true);
		
		
		
		spriteBatch = new SpriteBatch();
		
		font = new BitmapFont();
		label = new Label(" ", new Label.LabelStyle(font, Color.WHITE));
		label.setVisible(true);
		crosshair = new Label("+", new Label.LabelStyle(font, Color.RED));
		crosshair.setPosition(Gdx.graphics.getWidth() / 2 - 3, Gdx.graphics.getHeight() / 2 - 9);
		//chat = new Label("", new Label.LabelStyle(font, Color.WHITE));
		
		stage = new Stage();
		
		infoLabel = new Label(" ", new Label.LabelStyle(font, Color.WHITE));
		infoLabel.setPosition(Gdx.graphics.getWidth() / 2, 50);
		infoLabel.setFontScale(1.5f);
		
		stage.addActor(label);
		stage.addActor(crosshair);
		stage.addActor(infoLabel);
		
		
		Gdx.input.setInputProcessor(controls = new ControlListener(this));
		Gdx.input.setCursorCatched(true);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glTexParameterf(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MIN_FILTER, GL20.GL_LINEAR_MIPMAP_LINEAR);
		
		
		tickLoop();
		//chunkGenLoop();
	}
	
	public static int renderCallsBetweenTicks = 16;
	public static int curCBT = 0;
	
	public void tick() {
		world.tick();
		renderCallsBetweenTicks = curCBT;
		curCBT = 0;
		//System.out.println(renderCallsBetweenTicks);
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
	@Override
	public void render() {
		if (exit) return;
		long now = System.currentTimeMillis();
		long delta = now - last;
		last = now;
		curCBT++;
        
		
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		if (!Column.testshader) modelBatch.begin(world.player.cam.cam);
		
		world.render();
		//world.player.inventory.render(spriteBatch);
		if (!Column.testshader) modelBatch.end();
		
		
		StringBuilder builder = new StringBuilder();
		builder.append("onGround: ").append(world.player.onGround);
		builder.append(" time: ").append(world.time);
		builder.append(" col: ").append(world.loadedColumns.size());
		builder.append(" | pos: ").append("x:"+String.format("%.2f",world.player.pos.x)+" y:"+String.format("%.2f",world.player.pos.y)+" z:"+String.format("%.2f",world.player.pos.z));
		int en = 0;
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
	}
	
	@Override
	public void dispose() {
		exit = true;
	}
	
	public static Texture getTexture(String s) {
		return textures.get(s);
	}
	
	public static void loadTextures() {
		System.out.println(Gdx.files.getLocalStoragePath());
		textures.put("dirt", new Texture(Gdx.files.internal("dirt.png")));
		textures.put("stone", new Texture(Gdx.files.internal("stone.png")));
		textures.put("slot", new Texture(Gdx.files.internal("slot.png")));
		textures.put("glass", new Texture(Gdx.files.internal("glass.png")));
		textures.put("redsand", new Texture(Gdx.files.internal("red_sand.png")));
		textures.put("grassblock", new Texture(Gdx.files.internal("grass.png")));
		textures.put("oaklog", new Texture(Gdx.files.internal("oaklog.png")));
		for (Texture texture : textures.values()) {
			//texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			//texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);
		}
	}
	
	public void chunkGenLoop() {
		new Thread(()->{
			int curcomp = 0;
			int needtocompensate = 0;
			final int tickrate = 500;
			while (true) {
				try {
					if (exit) Thread.currentThread().stop();
					long timeone = System.currentTimeMillis();
					world.chunkShit();
					long timetwo = System.currentTimeMillis();
					int raznica = (int) (timetwo - timeone);
					if (needtocompensate > 5000) {
						needtocompensate = 0;
						System.out.println("client overloaded, skiped "+needtocompensate/tickrate+" ticks");
					}
					if (raznica > 0 && raznica < tickrate) {
						curcomp = tickrate-raznica;
						System.out.println("comp "+raznica+"ms");
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
						System.out.println("pass "+raznica+"ms");
						needtocompensate += raznica-tickrate;
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(2);
				}
			}
		}).start();
	}
	
	public void tickLoop() {
		new Thread(()->{
			int curcomp = 0;
			int needtocompensate = 0;
			final int tickrate = 50;
			while (true) {
				try {
					if (exit) Thread.currentThread().stop();
					long timeone = System.currentTimeMillis();
					tick();
					long timetwo = System.currentTimeMillis();
					int raznica = (int) (timetwo - timeone);
					if (needtocompensate > 5000) {
						needtocompensate = 0;
						System.out.println("client overloaded, skiped "+needtocompensate/tickrate+" ticks");
					}
					if (raznica > 0 && raznica < tickrate) {
						curcomp = tickrate-raznica;
						System.out.println("comp "+raznica+"ms");
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
						System.out.println("pass "+raznica+"ms");
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
