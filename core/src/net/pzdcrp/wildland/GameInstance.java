package net.pzdcrp.wildland;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ScreenUtils;

import net.pzdcrp.wildland.data.Vector3D;
import net.pzdcrp.wildland.player.ControlListener;
import net.pzdcrp.wildland.player.Player;
import net.pzdcrp.wildland.utils.ThreadU;
import net.pzdcrp.wildland.utils.VectorU;
import net.pzdcrp.wildland.world.World;
import net.pzdcrp.wildland.world.elements.Column;
import net.pzdcrp.wildland.world.elements.blocks.Block;

public class GameInstance extends ApplicationAdapter {
	public static SpriteBatch batch;
	Texture img;
	public static int renderDistance = 3;
	public static ModelBatch modelBatch;
	int i = 0;
	private BitmapFont font;
	private Label crosshair;
	private Label label;
	public static boolean exit = false;
	public ControlListener controls;
	public static Map<String, Texture> textures = new HashMap<>();
	public static final Vector3 forAnyReason = new Vector3();
	public static World world;
	//public static SpriteBatch gui = new SpriteBatch();
	//public static Label chat;
	
	@Override
	public void create() {
		System.out.println("loading textures");
		loadTextures();
		for (Entry<String, Texture> entry : textures.entrySet()) {
			System.out.println(entry.getKey()+" loaded");
			materials.put(entry.getKey(), new Material(
				TextureAttribute.createDiffuse(entry.getValue()),
    			IntAttribute.createCullFace(GL20.GL_NONE),
    			new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
    			));
		}
		System.out.println("lessgo");
		
		world = new World();
		
		modelBatch = new ModelBatch();
		
		font = new BitmapFont();
		label = new Label(" ", new Label.LabelStyle(font, Color.WHITE));
		label.setVisible(true);
		crosshair = new Label("+", new Label.LabelStyle(font, Color.RED));
		crosshair.setPosition(Gdx.graphics.getWidth() / 2 - 3, Gdx.graphics.getHeight() / 2 - 9);
		crosshair.setVisible(true);
		
		//chat = new Label("", new Label.LabelStyle(font, Color.WHITE));
		
		
		Gdx.input.setInputProcessor(controls = new ControlListener(this));
		Gdx.input.setCursorCatched(true);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		//Gdx.gl.glViewport(0, 0, 1200, 800);
		tickLoop();
	}
	
	public void tick() {
		world.tick();
	}
	
	@Override
	public void render() {
		if (exit) return;
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		modelBatch.begin(world.player.cam.cam);
		world.render();
		modelBatch.end();
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
		/*if (world.player.chat.isOpen) {
			chat.setVisible(true);
			chat.setText(world.player.chat.text);
		} else {
			chat.setVisible(false);
		}*/
	}
	
	@Override
	public void dispose() {
		exit = true;
	}
	
	public static Texture getTexture(String s) {
		return textures.get(s);
	}
	
	public void loadTextures() {
		System.out.println(Gdx.files.getLocalStoragePath());
		textures.put("dirt", new Texture(Gdx.files.internal("dirt.png")));
		textures.put("stone", new Texture(Gdx.files.internal("stone.png")));
		textures.put("slot", new Texture(Gdx.files.internal("slot.png")));
		textures.put("glass", new Texture(Gdx.files.internal("glass.png")));
		textures.put("redsand", new Texture(Gdx.files.internal("red_sand.png")));
		textures.put("grassblock", new Texture(Gdx.files.internal("grass.png")));
	}
	
	public static Map<String, Material> materials = new HashMap<String, Material>();
	public static Material getMaterial(String materialName) {
		return materials.get(materialName);
	}
	
	@SuppressWarnings("removal")
	public void tickLoop() {
		new Thread(()->{
			int curcomp = 0;
			int needtocompensate = 0;
			final int tickrate = 10;
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
