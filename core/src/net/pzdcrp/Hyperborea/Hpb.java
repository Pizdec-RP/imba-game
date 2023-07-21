package net.pzdcrp.Hyperborea;

import java.util.UUID;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Timer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.datasecs.hydra.client.Client;
import de.datasecs.hydra.client.HydraClient;
import de.datasecs.hydra.shared.handler.Session;
import de.datasecs.hydra.shared.protocol.HydraProtocol;
import de.datasecs.hydra.shared.protocol.packets.Packet;
import de.datasecs.hydra.shared.protocol.packets.listener.HydraPacketListener;
import io.netty.channel.ChannelOption;
import net.pzdcrp.Hyperborea.data.ActionAuthor;
import net.pzdcrp.Hyperborea.data.Mutex;
import net.pzdcrp.Hyperborea.data.Settings;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.multiplayer.HpbProtocol;
import net.pzdcrp.Hyperborea.multiplayer.packets.ClientPlayerConnectionPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ServerChatPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ServerSpawnPlayerPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ServerSuccessConnectPacket;
import net.pzdcrp.Hyperborea.player.ControlListener;
import net.pzdcrp.Hyperborea.player.Player;
import net.pzdcrp.Hyperborea.server.InternalServer;
import net.pzdcrp.Hyperborea.utils.MathU;
import net.pzdcrp.Hyperborea.utils.GameU;
import net.pzdcrp.Hyperborea.utils.VectorU;
import net.pzdcrp.Hyperborea.world.PlayerWorld;
import net.pzdcrp.Hyperborea.world.elements.Column;
import net.pzdcrp.Hyperborea.world.elements.blocks.Block;
import net.pzdcrp.Hyperborea.world.elements.blocks.Stone;

public class Hpb extends ApplicationAdapter {
	private static ModelBatch modelBatch;
	public static SpriteBatch spriteBatch;
	public static BitmapFont font;
	private Label label;
	private static final int tickrate = 50;
	public static boolean exit = false;
	public static ControlListener controls;
	public static Stage stage;
	public static SuperPizdatiyShader shaderprovider;
	//public static OrthographicCamera mCamera;
	public static Mutex mutex;
	
	public static final Vector3 forAnyReason = new Vector3();
	public static PlayerWorld world;
	//public static SpriteBatch gui = new SpriteBatch();
	
	public static Label infoLabel;
	public static InputMultiplexer multiplexer;
	//private static ShaderProgram sp;
	//public static FrameBuffer buffer;
	//public static TextureRegion textureRegion;
	
	public static State state = State.CREATINGINTERNALSERVER;
	private ShaderProgram stage2shader;
	public static InternalServer internalserver;
	public static boolean deadplayer = false;
	private static GlyphLayout respawn;
	
	public enum State {
		CREATINGINTERNALSERVER, SENDCONNECTION, WAITFORSERVER, PREPARE, INGAME
	}
	
	private HydraProtocol protocol = new HpbProtocol(new HpbPacketListener(this));
	private boolean spawned = false;
	public static Session session;
	private static HydraClient client;
	public static UUID playerId;
	
	public Hpb() {
	}
	
	public void packetReceived(Session s, Packet p) {
		GameU.log("client got packet "+p.getClass().getName());
		if (p instanceof ServerSuccessConnectPacket) {
			playerId = ((ServerSuccessConnectPacket)p).getid();
			world = new PlayerWorld();
			world.load();
			tickLoop();
		} else if (p instanceof ServerSpawnPlayerPacket) {
			if (spawned) GameU.end("спавн не должен вызываться более 1 раза");
			spawned = true;
			ServerSpawnPlayerPacket packet = (ServerSpawnPlayerPacket)p;
			world.player = new Player(packet.x,packet.y,packet.z, "Player1488");
			//TODO transfer other data like inventory, rotation, hp
			multiplexer.addProcessor(controls = new ControlListener(world.player));
		} else {
			world.player.onPacket(p);
		}
	}

	@Override
	public void create() {
		mutex = new Mutex();
		System.out.println("loading textures");
		loadTextures();
		
		
		System.out.println("lessgo");
		modelBatch = new ModelBatch(shaderprovider = new SuperPizdatiyShader());
		spriteBatch = new SpriteBatch();
		
		font = mutex.getFont(20);
		label = new Label(" ", new Label.LabelStyle(font, Color.WHITE));
		label.setVisible(true);
		
		stage = new Stage();
		respawn = new GlyphLayout();
		respawn.setText(mutex.getFont(40), "Ты сдох. Возвращаемся через 0.00!");//TODO переделать на свой класс
		infoLabel = new Label(" ", new Label.LabelStyle(font, Color.WHITE));
		infoLabel.setPosition(Gdx.graphics.getWidth() / 2, 100);
		
		stage.addActor(label);
		stage.addActor(infoLabel);
		
		multiplexer = new InputMultiplexer();
		Gdx.input.setInputProcessor(multiplexer);
		
		Gdx.input.setCursorCatched(true);
		buffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		textureRegion = new TextureRegion();
		stage2shader = new ShaderProgram(Gdx.files.internal("shaders/2stageV.vert"), Gdx.files.internal("shaders/2stageF.frag"));
		screensizeatr = stage2shader.getUniformLocation("screensize");
		hurtlevelatr = stage2shader.getUniformLocation("hurtlevel");
		isdeadatr = stage2shader.getUniformLocation("isdead");
		randomatr = stage2shader.getUniformLocation("random");
		Thread.currentThread().setName("main thd");
	}
	
	@Override
	public void resize (int width, int height) {
		if (world == null) return;
		world.player.cam.cam.viewportWidth = width;
		world.player.cam.cam.viewportHeight = height;
	    spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
	    buffer = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, true);
	    world.player.pinterface.resize(width, height);
	}
	
	public void tick() {
		world.tick();
	}
	
	long last = System.currentTimeMillis();
	//public static ModelInstance modelInstance;
	private static long /*delta, */now;
	private static int en;
	
	public static double tlerp(double a, double b, int ticks) {
	    float t = (float)(System.nanoTime() - timeone) / (tickrate * ticks * 1000000);
	    if (t > 1f) t = 1f;
	    return a * (1.0 - t) + b * t;
	}
	
	public static float lerp(float a, float b) {
	    float t = (float)(System.nanoTime() - timeone) / (tickrate * 1000000);
	    if (t > 1f) t = 1f;
	    return a * (1.0f - t) + b * t;
	}
	
	public static double lerp(double a, double b) {
	    float t = (float)(System.nanoTime() - timeone) / (tickrate * 1000000);
	    if (t > 1f) t = 1f;
	    return a * (1.0 - t) + b * t;
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
	private static FrameBuffer buffer;
	private static TextureRegion textureRegion;
	private static int screensizeatr, hurtlevelatr, isdeadatr, randomatr;
	public static float hurtlvl = 0, deadtimer = 0;
	
	public void renderWorld() {
		if (hurtlvl > 0) {
			hurtlvl--;
		}
		shaderprovider.pos.set(world.player.cam.cam.position);
		buffer.begin();

		//Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		int halfwidth = Gdx.graphics.getWidth()/2;
		int halfheight = Gdx.graphics.getHeight()/2;
		
		//1 стадия
		shaderprovider.newstage();
		modelBatch.begin(world.player.cam.cam);
		world.render();
		modelBatch.end();
		shaderprovider.end();
		//конец 1 стадии
		
		//2 стадия
		textureRegion.setRegion(buffer.getColorBufferTexture());
		
		buffer.end();
		
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		spriteBatch.begin();
		spriteBatch.setShader(stage2shader);
		stage2shader.setUniformf(screensizeatr, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		stage2shader.setUniformf(isdeadatr, deadplayer?1f:0f);
		stage2shader.setUniformf(hurtlevelatr, hurtlvl);
		stage2shader.setUniformf(randomatr, MathU.rndnrm());
		
		Texture buftex = textureRegion.getTexture();
		
		spriteBatch.draw(buftex, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, buftex.getWidth(), buftex.getHeight(), false, true);
		spriteBatch.setShader(null);
		
		if (deadplayer) { 
			if (hurtlvl == 0) {
				BitmapFont f = mutex.getFont(40);
				deadtimer++;
				if (deadtimer >= 400) {
					world.player.respawn();
					deadplayer = false;
					deadtimer = 0f;
				} else if (deadtimer >= 300) {
					float a = 1-MathU.norm(300, 400, deadtimer);
					float b = MathU.norm(300, 400, deadtimer);
					f.getColor().set(0, 1, b, a);
				} else if (deadtimer >= 0 && deadtimer <= 100) {
					f.getColor().r = 1-MathU.norm(0, 100, deadtimer);
				} else if (deadtimer >= 100 && deadtimer <= 200) {
					f.getColor().g = 1-MathU.norm(100, 200, deadtimer);
					f.getColor().r = MathU.norm(100, 200, deadtimer);
				} else if (deadtimer >= 200 && deadtimer <= 300) {
					f.getColor().b = 1-MathU.norm(200, 300, deadtimer);
					f.getColor().g = MathU.norm(200, 300, deadtimer);
				}
				f.draw(spriteBatch,
						"Ты сдох. Возвращаемся через "+String.format("%.2f",((1-MathU.norm(0, 400, deadtimer))*5))+"!",
						halfwidth - respawn.width / 2, halfheight);
				f.getColor().set(1,1,1,1);
			}
		} else {
			//displayinfo
			layout.setText(font, currentText);
		    float textWidth = layout.width;
			font.draw(spriteBatch, currentText, halfwidth - textWidth / 2, Gdx.graphics.getHeight()*0.2f);
			world.player.pinterface.render(halfwidth, halfheight);
		}
		
		spriteBatch.end();
		//конец 2 стадии
	}
	
	public static void render(ModelInstance obj) {
		modelBatch.render(obj);
	}
	public static void render(ModelInstance obj, Environment env) {
		modelBatch.render(obj, env);
	}
	
	@Override
	public void render() {
		if (exit) {
			return;
		}
		try {
			if (state == State.CREATINGINTERNALSERVER) {
				if (internalserver == null) {
					GameU.log("opening server");
					internalserver = new InternalServer();
				} else {
					if (internalserver.server.isActive()) {
						if (client == null) {
							client = new Client.Builder("127.0.0.1", 7777, protocol)
					            .workerThreads(4)
					            .option(ChannelOption.TCP_NODELAY, true)
					            .build();
							GameU.log("client created");
							session = client.getSession();
						} else if (client.isConnected()) {
							GameU.log("sending");
							state = State.SENDCONNECTION;
						}
					}
				}
			} else if (state == State.SENDCONNECTION) {
				if (client.isConnected()) {
					state = State.WAITFORSERVER;
					GameU.log("sending...");
					session.send(new ClientPlayerConnectionPacket("smartass", Settings.renderDistance));
				}
			} else if (state == State.WAITFORSERVER) {
				if (playerId != null) {
					GameU.log("server responded");
					state = State.PREPARE;
				}
			} else if (state == State.PREPARE) {
				int bw = Gdx.graphics.getWidth(), bh = Gdx.graphics.getHeight();
				Gdx.graphics.setWindowedMode(500, 500);
				mutex.prepare();
				mutex.render();
				mutex.endrender();
				state = State.INGAME;
				Gdx.graphics.setWindowedMode(bw, bh);
			} else if (state == State.INGAME) {
				now = System.currentTimeMillis();
				last = now;
				
				renderWorld();
				
				StringBuilder builder = new StringBuilder();
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
				builder.append(" | vecs: ").append(Vector3D.test);
				builder.append(" | blocks: ").append(Block.count);
				label.setText(builder);
				stage.act(Gdx.graphics.getDeltaTime());
				stage.draw();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("render error");
			exit = true;
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
			//texture.setFilter(TextureFilter.MipMap,TextureFilter.Nearest);
			mutex.addTexture(texture, key);
		}
		
		JsonObject other = obj.get("other").getAsJsonObject();
		for (String key : other.keySet()) {
			Texture texture  = new Texture(Gdx.files.internal("textures/other/"+other.get(key).getAsString()),Format.RGBA8888,true);
			//texture.setFilter(TextureFilter.MipMap,TextureFilter.Nearest);
			mutex.addOtherTexture(texture, key);
		}
		
		JsonObject items = obj.get("items").getAsJsonObject();
		for (String key : items.keySet()) {
			Texture texture  = new Texture(Gdx.files.internal("textures/items/"+items.get(key).getAsString()),Format.RGBA8888,true);
			//texture.setFilter(TextureFilter.MipMap,TextureFilter.Nearest);
			mutex.addItemTexture(texture, key);
		}
		
		mutex.end();
	}
	
	static long timeone = System.nanoTime();
	static int curcomp = 0;
	
	public void tickLoop() {
		new Thread(() -> {
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
	    	    	GameU.sleep(tosleep);
	    	    }
	        }
	    }, "client tick thread").start();
	}
	
	public static void onCommand(String rawcommand) {
		if (rawcommand.split(" ").length == 0) return;
		String command = rawcommand.split(" ")[0].replace("/", "");
		String[] args = rawcommand.split(" ");
		
		//TODO ClientPlayerCommandPacket
		
		//System.out.println(command);
		/*if (command.equals("stop")) {
			if (Hpb.world.save()) {
				System.out.println("всё");
				System.exit(0);
			} else {
				System.out.println("откат, сохранение высрало ошибку");
			}
		} else if (command.equals("save")) {
			Hpb.world.save();
			exit=false;
			System.exit(0);
		} else if (command.equals("setblock")) {
			world.setBlock(new Stone(world.player.pos.clone().func_vf()), ActionAuthor.command);
			world.player.chat.send("setted");
		} else if (command.equals("gl")) {
			if (args.length < 4) {
				world.player.chat.send("no args");
			} else {
				int x = Integer.parseInt(args[1]);
				int y = Integer.parseInt(args[2]);
				int z = Integer.parseInt(args[3]);
				int light = world.getLight(x, y, z);
				world.player.chat.send("light at ["+x+" "+y+" "+z+"] is "+light);
			}
		} else if (command.equals("weather")) {
			String type = args[1];
			switch (type) {
			case "rain":
				return;//TODO
			}
		}*/
	}
}