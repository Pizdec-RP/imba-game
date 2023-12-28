package net.pzdcrp.Aselia.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.google.gson.JsonObject;

import de.datasecs.hydra.shared.handler.Session;
import de.datasecs.hydra.shared.protocol.packets.Packet;
import net.pzdcrp.Aselia.Hpb;
import net.pzdcrp.Aselia.data.AABB;
import net.pzdcrp.Aselia.data.ActionAuthor;
import net.pzdcrp.Aselia.data.DM;
import net.pzdcrp.Aselia.data.DamageSource;
import net.pzdcrp.Aselia.data.EntityType;
import net.pzdcrp.Aselia.data.Settings;
import net.pzdcrp.Aselia.data.Vector2I;
import net.pzdcrp.Aselia.data.Vector3D;
import net.pzdcrp.Aselia.multiplayer.ServerPlayer;
import net.pzdcrp.Aselia.multiplayer.packets.client.ingame.ClientPlayerActionPacket;
import net.pzdcrp.Aselia.multiplayer.packets.client.ingame.ClientPlayerActionPacket.PlayerAction;
import net.pzdcrp.Aselia.multiplayer.packets.client.ingame.ClientPlayerLocationDataPacket;
import net.pzdcrp.Aselia.multiplayer.packets.client.ingame.ClientPlayerRespawnPacket;
import net.pzdcrp.Aselia.multiplayer.packets.server.entity.ServerEntityDespawnPacket;
import net.pzdcrp.Aselia.multiplayer.packets.server.entity.ServerEntityPositionVelocityPacket;
import net.pzdcrp.Aselia.multiplayer.packets.server.entity.ServerSpawnEntityPacket;
import net.pzdcrp.Aselia.multiplayer.packets.server.ingame.ServerChatPacket;
import net.pzdcrp.Aselia.multiplayer.packets.server.ingame.ServerNotificationPacket;
import net.pzdcrp.Aselia.multiplayer.packets.server.ingame.ServerPlayerRespawnPacket;
import net.pzdcrp.Aselia.multiplayer.packets.server.ingame.ServerSetHealthPacket;
import net.pzdcrp.Aselia.multiplayer.packets.server.inventory.ServerCloseInventoryPacket;
import net.pzdcrp.Aselia.multiplayer.packets.server.inventory.ServerOpenInventoryPacket;
import net.pzdcrp.Aselia.multiplayer.packets.server.inventory.ServerSetSlotPacket;
import net.pzdcrp.Aselia.multiplayer.packets.server.inventory.ServerSetupInventoryPacket;
import net.pzdcrp.Aselia.multiplayer.packets.server.world.ServerChunkLightPacket;
import net.pzdcrp.Aselia.multiplayer.packets.server.world.ServerLoadColumnPacket;
import net.pzdcrp.Aselia.multiplayer.packets.server.world.ServerSetblockPacket;
import net.pzdcrp.Aselia.multiplayer.packets.server.world.ServerUnloadColumnPacket;
import net.pzdcrp.Aselia.server.InternalServer;
import net.pzdcrp.Aselia.server.ServerWorld;
import net.pzdcrp.Aselia.utils.GameU;
import net.pzdcrp.Aselia.utils.MathU;
import net.pzdcrp.Aselia.world.World;
import net.pzdcrp.Aselia.world.elements.Chunk;
import net.pzdcrp.Aselia.world.elements.Column;
import net.pzdcrp.Aselia.world.elements.blocks.Air;
import net.pzdcrp.Aselia.world.elements.blocks.Block;
import net.pzdcrp.Aselia.world.elements.chat.Chat2;
import net.pzdcrp.Aselia.world.elements.entities.Entity;
import net.pzdcrp.Aselia.world.elements.inventory.PlayerInventory;
import net.pzdcrp.Aselia.world.elements.storages.ItemStorage;

public class Player extends Entity {
	//public float pitch, yaw;//yaw left-right
	public Camera cam;
	public boolean forward = false, reverse = false,
			left = false, right = false,
			up=false,down=false,
			rmb = false, lmb = false,
			run = false;
	private static final float mouseSensitivity = 0.25f;
	public float camHeight = 1.6f;
	public int actcd = 0;//15 ticks
	public Chat2 chat = new Chat2();
	public PlayerInterface pinterface;

	private int breakingticks = 0;
	private Block beforeAimBlock = new Air(new Vector3D());
	private Chunk bbchunk;
	private boolean isMining = false;
	public PlayerInventory castedInv;


	//server
	public String nickname;
	public ServerPlayer serverProfile;


	public Player(double tx, double ty, double tz, String name, World world, int lid) {
		super(new Vector3D(tx,ty,tz),new AABB(-0.3, 0, -0.3, 0.3, 1.7, 0.3), EntityType.player, world, lid);
		this.nickname = name;
		if (world.isLocal()) {
			cam = new Camera();
			cam.setpos(this.pos.x,this.pos.y,this.pos.z);
			cam.cam.update();
		}
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

	@Override
	public boolean tick() {
		boolean continuee = super.tick();
		if (!this.world.isLocal()) {
			if (hp < maxhp() && hp != 0) {
				if (healcd > 0) {
					healcd--;
				} else {
					setHp((byte)(hp + 1));
					healcd = 40;
				}
			}
			return false;
		}
		if (!continuee) return false;
		if (actcd > 0) actcd--;
		updateFacing();
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
		if (world.isLocal()) {
			cam.setpos(getEyeLocation());
		}

		return true;
	}

	@Override
	public void setHp(byte i) {
		if (this.hp != i) {
			this.hp = i;
			if (!world.isLocal()) {
				sendSelfPacket(new ServerSetHealthPacket(hp));
			}
			if (hp <= 0) {
				onDeath();
			}
		}
	}

	private Session session;
	public void sendSelfPacket(Packet p) {
		if (session == null) {
			session = ((ServerWorld) world).getPlayerByName(nickname).session;
		}
		session.send(p);
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
				Hpb.session.send(new ClientPlayerActionPacket(PlayerAction.StartBreakingBlock, beforeAimBlock.pos));
			}
			float lerp = MathU.norm(0, miningTicks, breakingticks);
			int stage = (int)Math.floor(lerp * 10);
			bbchunk = beforeAimBlock.getChunk(world);
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
				//world.breakBlock(beforeAimBlock.pos);
				Hpb.session.send(new ClientPlayerActionPacket(PlayerAction.EndBreakingBlock, beforeAimBlock.pos));
				breakingticks = 0;
				bbchunk.endBlockBreakStage();
				isMining = false;
			}
		} else {
			if (bbchunk != null && bbchunk.bbstage != -1) {
				Hpb.session.send(new ClientPlayerActionPacket(PlayerAction.ResetBreakingBlock, beforeAimBlock.pos));
				bbchunk.endBlockBreakStage();
			}
		}
		beforeAimBlock = currentAimBlock;
	}

	public void movement() {
		Vector3D velocityGoal = new Vector3D(0,0,0);
		double speed = 0d, ospeed = 0d;
		if (down) {
			speed = DM.walkSpeed/3;
			ospeed = speed;
		} else if (run) {
			speed = DM.runSpeed;
			ospeed = DM.walkSpeed;
		} else {
			speed = DM.walkSpeed;
			ospeed = DM.walkSpeed;
		}
		if (forward) {
			velocityGoal.x = speed;
		}
		if (reverse) {
			velocityGoal.x = -ospeed;
		}
		if (left) {
			velocityGoal.z = -ospeed;
		}
		if (right) {
			velocityGoal.z = ospeed;
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
	public void hit(DamageSource src, byte damage) {
		if (justspawn > 0) return;
		System.out.println("hp: "+hp+" dmg:"+damage);
		healcd = 80;
		if (hp == -Byte.MIN_VALUE) return;
		setHp((byte) (this.hp - damage));
		Hpb.hurtlvl = 50;
		if (hp < 0) {
			Hpb.hurtlvl = 99;
			Hpb.world.player.chat.send(this.getClass().getName()+" died of "+src.toString());
		}
	}

	/**multi side*/
	public void onDeath() {
		if (world.isLocal()) {
			Hpb.deadplayer = true;
		} else {
			this.inventory.dropAllItems();
			//TODO отослать близжайшим игрокам пакет о смерти игрока
			//TODO при телепорте ентити проверять пропадает ли она из зоны стрима игроков и если да то деспавнить
		}
		//this.inventory.dropAllItems();
	}

	public void respawn() {
		if (world.isLocal()) {
			Hpb.session.send(new ClientPlayerRespawnPacket());
		} else {
			setHp(maxhp());
			this.justspawn = 50;
			this.teleport(InternalServer.world.randomSpawnPoint());
			sendSelfPacket(new ServerPlayerRespawnPacket(pos));
			echc = new Vector2I(pos.x,pos.z);
		}
	}

	@Override
	public void teleport(Vector3D pos1) {
		super.teleport(pos1);
		if (world.isLocal()) {
			Hpb.session.send(new ClientPlayerLocationDataPacket(pos, vel, onGround, yaw, pitch));
		}/* else {
			if (!serverProfile.columnsAroundPlayer.containsKey(VectorU.posToColumn(pos1)))
				sendSelfPacket(new ServerLoadColumnPacket(world.getColumn(echc)));
		}*/
	}

	@Override
	public void getJson(JsonObject jen) {
		super.getJson(jen);
		jen.add("inventory", castedInv.toJson());
		jen.addProperty("name", nickname);
	}

	@Override
	public void fromJson(JsonObject jen) {
		super.fromJson(jen);
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

		if (down) {
			this.camHeight = 1.4f;
			this.hitbox.maxY = 1.49f;
		} else {
			this.camHeight = 1.6f;
			this.hitbox.maxY = 1.69f;
		}
	}



	@Override
	public void render() {
		super.render();
		if (Gdx.input.isKeyPressed(Input.Keys.W)) {
			if (run && cam.getFov() < Settings.fov+5) {
				cam.setFov(cam.getFov() + 0.4f);
			} else {
				if (!run && cam.getFov() > Settings.fov) {
					cam.setFov(cam.getFov() - 0.4f);
				}
			}
		} else {
			if (cam.getFov() > Settings.fov) {
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
		if (chat.isOpened() || castedInv.isOpened) {
			lastCursorX = screenX;
	        lastCursorY = screenY;
			return;
		}
		//GameU.log("mm "+screenX+" "+screenY);
		float dy = lastCursorY - screenY;
        float dx = lastCursorX - screenX;

        cam.cam.rotate(Vector3.Y,dx * mouseSensitivity);
        rotatePitch(dy * mouseSensitivity);

        lastCursorX = screenX;
        lastCursorY = screenY;

        pitch = MathUtils.atan2(cam.cam.direction.y, (float)Math.sqrt(cam.cam.direction.x * cam.cam.direction.x + cam.cam.direction.z * cam.cam.direction.z));
        yaw = MathUtils.atan2(cam.cam.direction.x, -cam.cam.direction.z);
	}

	public void updateCamRotation() {
		float directionX = -MathUtils.sin(yaw) * MathUtils.cos(pitch);
		float directionY = -MathUtils.sin(pitch);
		float directionZ = MathUtils.cos(yaw) * MathUtils.cos(pitch);
		cam.cam.direction.set(directionX, directionY, directionZ).nor();
		cam.cam.update();
	}

	@Override
	public void setYaw(float yaw) {
		super.setYaw(yaw);
		if (world.isLocal()) {
			updateCamRotation();
		}
	}

	@Override
	public void setPitch(float pitch) {
		super.setPitch(pitch);
		if (world.isLocal()) {
			updateCamRotation();
		}
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

	//client only
	public void onPacket(Packet p) {
		if (p instanceof ServerChatPacket) {
			chat.fromServer(((ServerChatPacket)p).getmsg());
		} else if (p instanceof ServerLoadColumnPacket) {
			ServerLoadColumnPacket packet = (ServerLoadColumnPacket)p;
			world.getLoadedColumns().put(packet.c.pos, packet.c);
		} else if (p instanceof ServerUnloadColumnPacket) {
			ServerUnloadColumnPacket packet = (ServerUnloadColumnPacket)p;
			world.getLoadedColumns().remove(packet.pos);
		} else if (p instanceof ServerChunkLightPacket) {
			ServerChunkLightPacket packet = (ServerChunkLightPacket)p;
			Chunk c = world.getColumn(new Vector2I(
					packet.chunkPos.x,
					packet.chunkPos.z))
			.chunks[packet.chunkPos.y];
			c.setLightStorage(packet.light);
			c.updateModel();//TODO сделать метод который будет обновлять не всю модель а только свет

		} else if (p instanceof ServerSetblockPacket) {
			ServerSetblockPacket packet = (ServerSetblockPacket) p;
			if (packet.author == ActionAuthor.player && packet.id == 0) {
				world.breakBlock(packet.pos);
			} else {
				world.setBlock(packet.id, packet.pos, packet.author);
			}
		} else if (p instanceof ServerSpawnEntityPacket) {
			ServerSpawnEntityPacket packet = (ServerSpawnEntityPacket)p;
			world.spawnEntity(packet.entity);
		} else if (p instanceof ServerEntityPositionVelocityPacket) {
			ServerEntityPositionVelocityPacket packet = (ServerEntityPositionVelocityPacket)p;
			Entity e = world.getEntity(packet.id);
			if (e == null) {
				GameU.err("получена позиция для не созданой сущности");
				return;
			}
			e.setPos(packet.pos);
			e.vel = packet.vel;
		} else if (p instanceof ServerEntityDespawnPacket) {
			ServerEntityDespawnPacket packet = (ServerEntityDespawnPacket)p;
			for (Column c : world.getLoadedColumns().values()) {
				for (Entity entity : c.entites) {
					if (entity.localId == packet.lid) {
						entity.despawn();
						return;
					}
				}
			}
			chat.debug("not found!");
		} else if (p instanceof ServerSetHealthPacket) {
			ServerSetHealthPacket packet = (ServerSetHealthPacket) p;
			this.setHp(packet.hp);
		} else if (p instanceof ServerSetupInventoryPacket) {
			ServerSetupInventoryPacket packet = (ServerSetupInventoryPacket)p;
			this.castedInv.setItems(packet.items);
		} else if (p instanceof ServerSetSlotPacket) {
			ServerSetSlotPacket packet = (ServerSetSlotPacket)p;
			this.castedInv.setSlotFromPacketOnClient(packet.index, packet.item);
		} else if (p instanceof ServerPlayerRespawnPacket) {
			ServerPlayerRespawnPacket packet = (ServerPlayerRespawnPacket)p;
			teleport(packet.pos);
			//Hpb.deadplayer = false;
			//Hpb.deadtimer = 0f;
		} else if (p instanceof ServerCloseInventoryPacket) {
			castedInv.close();
		} else if (p instanceof ServerOpenInventoryPacket) {
			ServerOpenInventoryPacket packet = (ServerOpenInventoryPacket)p;
			ItemStorage is = ItemStorage.storageTable.get(packet.id);
			is.setItems(packet.items);
			castedInv.open(is);
		} else if (p instanceof ServerNotificationPacket) {
			ServerNotificationPacket packet = (ServerNotificationPacket)p;
			GameU.log(packet.text);
			Hpb.displayInfo(packet.text);
		}
	}

	public Vector3D getCenterPoint() {
		return pos.add(0, hitbox.maxY/2, 0);
	}
}
