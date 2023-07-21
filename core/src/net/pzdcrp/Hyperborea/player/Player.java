package net.pzdcrp.Hyperborea.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.google.gson.JsonObject;

import de.datasecs.hydra.shared.protocol.packets.Packet;
import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.Hpb.State;
import net.pzdcrp.Hyperborea.data.AABB;
import net.pzdcrp.Hyperborea.data.DM;
import net.pzdcrp.Hyperborea.data.DamageSource;
import net.pzdcrp.Hyperborea.data.EntityType;
import net.pzdcrp.Hyperborea.data.Settings;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.multiplayer.packets.ServerChatPacket;
import net.pzdcrp.Hyperborea.utils.MathU;
import net.pzdcrp.Hyperborea.world.elements.Chunk;
import net.pzdcrp.Hyperborea.world.elements.blocks.Air;
import net.pzdcrp.Hyperborea.world.elements.blocks.Block;
import net.pzdcrp.Hyperborea.world.elements.chat.Chat;
import net.pzdcrp.Hyperborea.world.elements.chat.Chat2;
import net.pzdcrp.Hyperborea.world.elements.entities.Entity;
import net.pzdcrp.Hyperborea.world.elements.inventory.PlayerInventory;
import net.pzdcrp.Hyperborea.world.elements.inventory.items.DirtItem;
import net.pzdcrp.Hyperborea.world.elements.inventory.items.GlassItem;
import net.pzdcrp.Hyperborea.world.elements.inventory.items.GrassItem;
import net.pzdcrp.Hyperborea.world.elements.inventory.items.OakLogItem;
import net.pzdcrp.Hyperborea.world.elements.inventory.items.PlanksItem;
import net.pzdcrp.Hyperborea.world.elements.inventory.items.StoneItem;
import net.pzdcrp.Hyperborea.world.elements.inventory.items.WeedItem;

public class Player extends Entity {
	//public float pitch, yaw;//yaw left-right
	public Camera cam;
	public boolean forward = false, reverse = false,
			left = false, right = false, 
			up=false,down=false,
			rmb = false, lmb = false,
			run = false;
	private static final float mouseSensitivity = 0.1f;
	public float camHeight = 1.6f;
	public int actcd = 0;//15 ticks
	public Chat2 chat = new Chat2();
	public PlayerInterface pinterface;
	
	private int breakingticks = 0;
	private Block beforeAimBlock = new Air(new Vector3D());
	private Chunk bbchunk;
	private boolean isMining = false;
	public PlayerInventory castedInv;
	public int curentNumPressed = -1;
	public String nickname;
	
	//сохраняется
	public Vector3D spawnpoint;
	
	
	public Player(double tx, double ty, double tz, String name) {
		super(new Vector3D(tx,ty,tz),new AABB(-0.3, 0, -0.3, 0.3, 1.7, 0.3), EntityType.player);
		this.nickname = name;
		cam = new Camera();
		cam.setpos(this.pos.x,this.pos.y,this.pos.z);
		cam.cam.update();
		pinterface = new PlayerInterface(this);
		this.castedInv = (PlayerInventory) inventory;
		/*this.inventory.addItem(new TntCrateItem(99), 0);
		this.inventory.addItem(new GlassItem(99), 1);
		this.inventory.addItem(new GrassItem(99), 2);
		this.inventory.addItem(new StoneItem(99), 3);
		this.inventory.addItem(new OakLogItem(99), 4);
		this.inventory.addItem(new PlanksItem(99), 5);
		this.inventory.addItem(new DirtItem(99), 6);
		this.inventory.addItem(new WaterBucketItem(1), 7);
		this.inventory.addItem(new WeedItem(99), 8);*/
	}
	
	public void tick() {
		super.tick();
		if (actcd > 0) actcd--;
		updateControls();
		movement();
		updateBlockBreaking();
		if (!chat.isOpened()  || castedInv.isOpened) {
			if (rmb && Gdx.input.isCursorCatched()) {
				if (actcd == 0) {
					this.inventory.onRClick();
					actcd = 4;
				}
			}
		}
		cam.setpos(getEyeLocation());
		if (hp < maxhp()) {
			if (healcd > 0) {
				healcd--;
			} else {
				this.hp += 1;
				healcd = 40;
			}
		}
	}
	
	private static final float bs = 0.1f;
	public void updateBlockBreaking() {
		if (chat.isOpened() || castedInv.isOpened) {
			breakingticks = 0;
			isMining = false;
			if (bbchunk != null) bbchunk.endBlockBreakStage();
			return;
		}
		if (lmb && !rmb) {
			if (currentAimEntity == null && currentAimBlock != null && beforeAimBlock != null) {
				isMining = true;
				if (beforeAimBlock.pos.equals(currentAimBlock.pos)) {
					breakingticks++;
				} else {
					breakingticks = 0;
					isMining = false;
				}
			} else {
				breakingticks = 0;
				isMining = false;
			}
		} else {
			breakingticks = 0;
			isMining = false;
		}
		
		if (isMining) {
			int miningTicks = (int) (beforeAimBlock.getResistance() * 20);
			if (miningTicks == 0) {
				Hpb.world.breakBlock(beforeAimBlock.pos);
				breakingticks = 0;
				isMining = false;
				return;
			}
			float lerp = MathU.norm(0, miningTicks, breakingticks);
			int stage = (int)Math.floor(lerp * 10);
			bbchunk = beforeAimBlock.getChunk();
			if (bbchunk.bbstage != stage) {
				bbchunk.addBlockBreakStage(beforeAimBlock.pos, stage);
			}
			Hpb.world.spawnParticle(
					beforeAimBlock.texture,
					currentaimpoint.translate(),
					new Vector3(
							MathU.rndf(-bs, bs),
							MathU.rndf(0, bs),
							MathU.rndf(-bs, bs)
					),
					MathU.rndi(8, 16));
			if (breakingticks >= miningTicks) {
				Hpb.world.breakBlock(beforeAimBlock.pos);
				breakingticks = 0;
				bbchunk.endBlockBreakStage();
			}
		} else {
			if (bbchunk != null && bbchunk.bbstage != -1) {
				bbchunk.endBlockBreakStage();
			}
		}
		beforeAimBlock = currentAimBlock;
	}
	
	public void movement() {
		Vector3D velocityGoal = new Vector3D(0,0,0);
		double speed = 0d;
		if (down) {
			speed = DM.walkSpeed/3;
		} else if (run) {
			speed = DM.runSpeed;
		} else {
			speed = DM.walkSpeed;
		}
		if (forward) {
			velocityGoal.x = speed;
		}
		if (reverse) {
			velocityGoal.x = -speed;
		}
		if (left) {
			velocityGoal.z = -speed;
		}
		if (right) {
			velocityGoal.z = speed;
		}
        vel.x += velocityGoal.x * (float) Math.sin(yaw) + velocityGoal.z * (float) Math.cos(yaw);
        vel.z += velocityGoal.z * (float) Math.sin(yaw) - velocityGoal.x * (float) Math.cos(yaw);
        if (up && this.onGround) {
			this.vel.y += 0.509838175463746F;
			this.onGround = false;
		}
	}
	
	/*public void updatePlayerActions() {
		if (rmb && Gdx.input.isCursorCatched()) {
			if (actcd <= 0) {
				if (pos != null) {
					this.inventory.onRClick();
					actcd = 4;
				}
			}
		}
		if (lmb && Gdx.input.isCursorCatched()) {
			if (actcd <= 0) {
				if (pos != null) {
					this.inventory.onLClick();
					actcd = 4;
				}
			}
		}
	}*/
	
	int healcd = 0;
	@Override
	public void hit(DamageSource src, int damage) {
		if (justspawn > 0) return;
		System.out.println("hp: "+hp+" dmg:"+damage);
		if (damage < 0) {
			System.out.println("wrong damage: "+damage);
			return;
		}
		healcd = 80;
		if (hp == -Byte.MIN_VALUE) return;
		this.hp -= damage;
		Hpb.hurtlvl = 50;
		if (hp < 0) {
			Hpb.hurtlvl = 99;
			Hpb.world.player.chat.send(this.getClass().getName()+" died of "+src.toString());
			onDeath();
		}
	}
	
	public void onDeath() {
		Hpb.deadplayer = true;
		this.inventory.dropAllItems();
	}
	
	public void respawn() {
		this.hp = maxhp();
		this.justspawn = 50;
		this.teleport(spawnpoint);
	}
	
	@Override
	public void getJson(JsonObject jen) {
		super.getJson(jen);
		jen.addProperty("spawnpoint", spawnpoint.toString());
		jen.add("inventory", castedInv.toJson());
		jen.addProperty("name", nickname);
	}
	
	@Override
	public void fromJson(JsonObject jen) {
		super.fromJson(jen);
		this.spawnpoint = Vector3D.fromString(jen.get("spawnpoint").getAsString());
		this.castedInv.fromJson(jen.get("inventory").getAsJsonObject());
		this.nickname = jen.get("name").getAsString();
	}
	
	@Override
	public byte maxhp() {
		return 25;
	}
	
	public void updateControls() {
		if (chat.isOpened() || castedInv.isOpened) return;
		if (Gdx.input.isKeyPressed(Input.Keys.W)) {
			forward = true;
		} else forward = false;
		if (Gdx.input.isKeyPressed(Input.Keys.S)) {
			reverse = true;
		} else reverse = false;
		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			left = true;
		} else left = false;
		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			right = true;
		} else right = false;
		if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
			down = true;
		} else down = false;
		if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
			run = true;
		} else run = false;
		if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
			up = true;
		} else up = false;
		if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
			rmb = true;
		} else rmb = false;
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
			lmb = true;
		} else lmb = false;
		if (Gdx.input.isKeyPressed(Input.Keys.R)) {
			Hpb.world.time += 1000;
		}
		if (curentNumPressed != -1) {
			this.inventory.setCurrentSlotInt(curentNumPressed-1);
		}
		
		if (down) {
			this.camHeight = 1.4f;
			this.hitbox.maxY = 1.49f;
		} else {
			this.camHeight = 1.6f;
			this.hitbox.maxY = 1.69f;
		}
	}
	
	
	
	public void render() {
		super.render();
		if (run && cam.getFov() < Settings.fov+5) {
			cam.setFov(cam.getFov() + 0.4f);
		} else {
			if (!run && cam.getFov() > Settings.fov) {
				cam.setFov(cam.getFov() - 0.4f);
			}
		}
		cam.render();
	}
	
	@Override
	public Vector3D getEyeLocation() {
		return new Vector3D(pos.x, pos.y+camHeight, pos.z);
	}
	
	public float getEyeHeight() {
		return (float) (pos.y+camHeight);
	}
	
	public float getYaw() {
		return this.yaw;
    }

    public float getPitch() {
    	return this.pitch;
    }
    
    private int lastCursorX = Gdx.graphics.getWidth() / 2;
    private int lastCursorY = Gdx.graphics.getHeight() / 2;
	
	public void handleMM(int screenX, int screenY) {
		if (chat.isOpened() || castedInv.isOpened) return;
		float dy = lastCursorY - screenY;
        float dx = lastCursorX - screenX;

        cam.cam.rotate(Vector3.Y,dx * mouseSensitivity);
        rotatePitch(dy * mouseSensitivity);

        lastCursorX = screenX;
        lastCursorY = screenY;
        
        pitch = MathUtils.atan2(cam.cam.direction.y, (float)Math.sqrt(cam.cam.direction.x * cam.cam.direction.x + cam.cam.direction.z * cam.cam.direction.z));
        yaw = MathUtils.atan2(cam.cam.direction.x, -cam.cam.direction.z);
	}
	
	@Override
	public void setYaw(float yaw) {
		super.setYaw(yaw);
		System.out.println("yaws");
		cam.cam.rotate(Vector3.Y, yaw);
		cam.cam.update();
	}
	
	@Override
	public void setPitch(float pitch) {
		super.setPitch(pitch);
		cam.cam.rotate(Vector3.X, pitch);
		cam.cam.update();
	}
	
	private final Vector3 tmp = new Vector3();
	
	public void rotatePitch(float step){
        tmp.set(cam.cam.direction).crs(cam.cam.up).nor();

        float currentY = cam.cam.direction.y *90;
        if (Math.abs(currentY+step) > 89.4){
            step = Math.signum(currentY)*89.4f-currentY;
        }

        cam.cam.direction.rotate(tmp, step).nor();
        cam.cam.up.rotate(tmp, step).nor();
    }
	
	@Override
	public int getType() {
		return 1;
	}

	public void onPacket(Packet p) {
		if (p instanceof ServerChatPacket) {
			chat.send(((ServerChatPacket)p).getmsg());
		}
	}
}
