package net.pzdcrp.Aselia.world.elements.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.google.gson.JsonObject;

import net.pzdcrp.Aselia.Hpb;
import net.pzdcrp.Aselia.data.AABB;
import net.pzdcrp.Aselia.data.ActionAuthor;
import net.pzdcrp.Aselia.data.BlockFace;
import net.pzdcrp.Aselia.data.DM;
import net.pzdcrp.Aselia.data.DamageSource;
import net.pzdcrp.Aselia.data.EntityType;
import net.pzdcrp.Aselia.data.OTripple;
import net.pzdcrp.Aselia.data.Settings;
import net.pzdcrp.Aselia.data.Vector2I;
import net.pzdcrp.Aselia.data.Vector3D;
import net.pzdcrp.Aselia.data.objects.ObjectData;
import net.pzdcrp.Aselia.multiplayer.packets.client.ingame.ClientPlayerLocationDataPacket;
import net.pzdcrp.Aselia.multiplayer.packets.server.entity.ServerEntityDespawnPacket;
import net.pzdcrp.Aselia.multiplayer.packets.server.entity.ServerEntityPositionVelocityPacket;
import net.pzdcrp.Aselia.multiplayer.packets.server.entity.ServerSpawnEntityPacket;
import net.pzdcrp.Aselia.player.Player;
import net.pzdcrp.Aselia.utils.GameU;
import net.pzdcrp.Aselia.utils.VectorU;
import net.pzdcrp.Aselia.world.PlayerWorld;
import net.pzdcrp.Aselia.world.World;
import net.pzdcrp.Aselia.world.elements.Chunk;
import net.pzdcrp.Aselia.world.elements.Column;
import net.pzdcrp.Aselia.world.elements.blocks.Air;
import net.pzdcrp.Aselia.world.elements.blocks.Block;
import net.pzdcrp.Aselia.world.elements.blocks.Dirt;
import net.pzdcrp.Aselia.world.elements.inventory.EntityInventory;
import net.pzdcrp.Aselia.world.elements.inventory.IInventory;
import net.pzdcrp.Aselia.world.elements.inventory.PlayerInventory;

public class Entity {
	@SuppressWarnings({ "serial", "deprecation" })
	public static final Map<Integer, Entity> entities = new HashMap<Integer, Entity>() {{
		//put(1, new Player()); //не будет использоваться
		put(2, new ItemEntity(Vector3D.ZERO, null, 0));
	}};
	
	
	//сохраняется
	public Vector3D pos;
	public Vector3D vel = new Vector3D();
	public boolean colx=false,coly=false,colz=false,onGround=false;
	public float yaw = 0,pitch = 0;
	public byte hp;
	public Vector2I beforeechc;
	public double fallstartblock = 0;
	
	//FIXME не сохраняется
	public IInventory inventory;
	
	//не должно сохраняться
	public Vector2I echc; //столбец в котором находится игрок
	public boolean firsttick = true;
	public boolean isPlayer = false;
	public EntityType type;
	public Vector3D beforepos;
	public AABB hitbox;
	public int justspawn = 50;
	public int localId; //айди сущности для пакетов с данными об энтити. должно сбрасываться при рестарте сервера
	
	//классы ссылки
	public Column curCol;
	public Block currentAimBlock = new Air(new Vector3D());
	public BlockFace currentAimFace = BlockFace.PX;
	public Entity currentAimEntity = null;
	public Vector3D currentaimpoint;
	public World world;
	
	public Entity(Vector3D pos, AABB hitbox, EntityType type, World world, int localid) {
		this.localId = localid;
		this.world = world;
		this.type = type;
		this.pos=pos;
		this.beforepos=pos.clone();
		this.hitbox=hitbox;
		this.beforeechc = new Vector2I(pos.x,pos.z);
		this.echc = new Vector2I(pos.x,pos.z);
		if (type == EntityType.player) {//переместить в конкретных ентити
			inventory = new PlayerInventory((Player) this);
			this.isPlayer = true;
		}/* else {
			inventory = new EntityInventory(this);
		}*/
		this.hp = maxhp();
		if (world == null) return;
		if (!world.isLocal()) {
			world.broadcastByColumn(beforeechc, new ServerSpawnEntityPacket(this));
		}
	}

	private boolean sended = false;
	public boolean tick() {
		Vector3D justBeforePos = beforepos.clone();
		beforepos.set(pos);
		if (justspawn > 0) justspawn--;
		
		echc = new Vector2I(pos.x,pos.z);
		
		if (curCol == null) {
			Column col = world.getColumn(echc);
			if (col == null) return false;
			this.curCol = col;
		}
		
		if ((!world.isLocal() && !isPlayer) || world.isLocal()) {
			updateGravity();
			applyMovement();
		}
		
		if (!world.isLocal()) {
			if (!invincible()) {
				if (vel.y < 0) {
			        if (fallstartblock == 0) {
			        	fallstartblock = pos.y;
			        }
		        } else {
		        	if (onGround) {
			        	if (fallstartblock != 0) {
				        	double falled = fallstartblock - pos.y;
				        	if (falled > 3.5) {
				        		byte dmg = (byte) ((falled-3) * 2);
				        		hit(DamageSource.Fall, dmg);
				        	}
				        	fallstartblock = 0;
			        	}
			        } else {
		        		fallstartblock = 0;
			        }
		        }
			}
		}
		
		
		
		//отсыл позиции каждый второй тик
		if (sended) {
			sended = false;
		} else {
			sended = true;
			if (!world.isLocal() && !isPlayer) {
				if (!justBeforePos.equals(pos))
					world.broadcastByColumn(echc, new ServerEntityPositionVelocityPacket(pos, vel, localId));
			} else if (world.isLocal() && isPlayer) {
				Hpb.session.send(new ClientPlayerLocationDataPacket(pos, vel, onGround, yaw, pitch));
			}
		}
		
		//if (!world.isLocal()) GameU.log("1");
		if (firsttick) {
			Column beforecol = world.getColumn(beforeechc);
			if (beforecol == null) return false;
			if (!beforecol.entites.contains(this)) beforecol.entites.add(this);
			firsttick = false;
			return false;
		}
		//if (!world.isLocal()) GameU.log("new echc: "+echc.toString());
		if (!beforeechc.equals(echc)) {
			Column beforecol = world.getColumn(beforeechc);
			Column col = world.getColumn(echc);
			if (col == null) {
				return false;
			}
			if (beforecol != null) 
				beforecol.entites.remove(this);
			
			this.curCol = col;
			col.entites.add(this);
			beforeechc = echc;
		}
		//GameU.log("player in col "+curCol.pos.toString());
		return true;
	}
	
	public boolean invincible() {
		return false;
	}
	
	public void updateFacing() {
		//должно вызываться только с клиента
		Object[] oarr = VectorU.findFacingPair(this.getEyeLocation(), Hpb.world.player.cam.cam.direction, this);
		this.currentAimBlock = (Block) oarr[0];
		if (this.currentAimBlock != null) {
			this.currentAimFace = VectorU.getFace(currentAimBlock.pos, (Vector3D)oarr[1]);
		}
		this.currentAimEntity = oarr[2] == null ? null : (Entity) oarr[2];
		this.currentaimpoint = (Vector3D)oarr[3];
	}
	
	public void updateGravity() {
		vel.y -= DM.gravity;
		vel.y *= DM.airdrag;
	}
	
	public void teleport(Vector3D pos) {
		GameU.log((world.isLocal()?"client":"server")+" bpos: "+this.pos.toString()+" npos: "+pos.toString());
		this.pos = pos;
		this.vel.setZero();
		echc = new Vector2I(pos.x,pos.z);
		curCol = world.getColumn(echc);
	}
	
	public void onPlayerClick(Player p) {
		GameU.err("unused method onPlayerClick");
	}
	
	public List<AABB> getNearBlocks() {
		AABB cube = getHitbox();
		cube = cube.grow(Math.max(vel.x,1),Math.max(vel.y,1),Math.max(vel.z,1));
		List<AABB> b = new ArrayList<>();
		
		for (int tx = (int)Math.floor(Math.min(cube.maxX, cube.minX)); tx < Math.max(cube.maxX, cube.minX); tx++) {
			for (int tz = (int)Math.floor(Math.min(cube.maxZ, cube.minZ)); tz < Math.max(cube.maxZ, cube.minZ); tz++) {
				for (int ty = (int)Math.floor(Math.min(cube.maxY, cube.minY)); ty < Math.max(cube.maxY, cube.minY); ty++) {
					Block bl = world.getBlock(new Vector3D(tx, ty, tz));//TODO оптимизировать
					if (bl != null) {
						if (bl.isCollide()) {
							for (AABB t : bl.getHitbox().get()) {
								b.add(t);
							}
						}
					}
				}
			}
		}
		return b;
	}
	
	public void applyMovement() {
	    if (vel.x != 0 || vel.y != 0 || vel.z != 0) {
	        List<AABB> nb = getNearBlocks();
	        double bx, by, bz;
	        boolean wasMovingDown = vel.y < 0;
	        
	        for (AABB collidedBB : nb) {
	            by = vel.y;
	            vel.y = collidedBB.calculateYOffset(this.getHitbox(), vel.y);
	            if (by != vel.y) {
	                coly = true;
	            }
	        }
	        
	        if (wasMovingDown && vel.y == 0) {
	            onGround = true;
	        } else {
	            onGround = false;
	        }
	        
	        
	        this.pos.y += vel.y;
	        
	        for (AABB collidedBB : nb) {
	            bx = vel.x;
	            vel.x = collidedBB.calculateXOffset(this.getHitbox(), vel.x);
	            if (bx != vel.x) {
	                colx = true;
	            }
	        }
	        this.pos.x += vel.x;
	        
	        for (AABB collidedBB : nb) {
	            bz = vel.z;
	            vel.z = collidedBB.calculateZOffset(this.getHitbox(), vel.z);
	            if (bz != vel.z) {
	                colz = true;
	            }
	        }
	        this.pos.z += vel.z;
	        
	        if (this.isPlayer) {
	            vel.x *= 0.6;
	            vel.z *= 0.6;
	        } else {
	            if (this.onGround) {
	                vel.x *= 0.6;
	                vel.z *= 0.6;
	            } else {
	                vel.x *= 0.98;
	                vel.z *= 0.98;
	            } 
	        }
	        
	        if (Math.abs(vel.x) < DM.badVel) vel.x = 0;
	        if (Math.abs(vel.y) < DM.badVel) vel.y = 0;
	        if (Math.abs(vel.z) < DM.badVel) vel.z = 0;
	        
	    }
	}

	
	public AABB getHitbox() {//FIXME
		return hitbox.noffset(pos);
	}
	
	public byte maxhp() {
		return 5;
	}
	
	public void getJson(JsonObject jen) {
		jen.addProperty("type", this.getType());
		jen.addProperty("pos", pos.toString());
		jen.addProperty("vel", vel.toString());
		jen.addProperty("coldata", colx+" "+coly+" "+colz);
		jen.addProperty("onGround", onGround);
		jen.addProperty("yawpitch", yaw+" "+pitch);
		jen.addProperty("beforeechc", beforeechc.toString());
		if (echc == null) {
			echc = new Vector2I(pos.x,pos.z);
			GameU.err("echc is null?");
			GameU.tracer();
		}
		jen.addProperty("echc", echc.toString());
		jen.addProperty("hp", this.hp);
		jen.addProperty("fsb", fallstartblock);
	}
	
	public void fromJson(JsonObject jen) {
		String jvel = jen.get("vel").getAsString();
		vel = Vector3D.fromString(jvel);
		String[] jcoll = jen.get("coldata").getAsString().split(" ");
		colx = Boolean.parseBoolean(jcoll[0]);
		coly = Boolean.parseBoolean(jcoll[1]);
		colz = Boolean.parseBoolean(jcoll[2]);
		onGround = jen.get("onGround").getAsBoolean();
		String[] rot = jen.get("yawpitch").getAsString().split(" ");
		setYaw(Float.parseFloat(rot[0]));
		setPitch(Float.parseFloat(rot[1]));
		beforeechc = Vector2I.fromString(jen.get("beforeechc").getAsString());
		echc = Vector2I.fromString(jen.get("echc").getAsString());
		hp = jen.get("hp").getAsByte();
		fallstartblock = jen.get("fsb").getAsDouble();
	}
	
	public void despawn() {
		curCol.entites.remove(this);
		if (!world.isLocal()) {
			world.broadcastByColumn(echc, new ServerEntityDespawnPacket(this.localId));
		}
	}

	public boolean placeBlock(Block block) {
		if (!this.isPlayer) {
			GameU.end("mob "+this.getClass().getName()+" cannot place blocks");
		}
		return world.setBlock(block, ActionAuthor.player);
	}

	public Vector3D getEyeLocation() {
		return new Vector3D(pos.x, pos.y+hitbox.maxY*0.9, pos.z);
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}
	
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public void render() {
		if (Settings.debug) {
			Hpb.render(getFrame());
		}
	}
	
	public void setHp(byte i) {
		this.hp = i;
	}
	
	public int getType() {
		return 0;
	}

	public void hit(DamageSource src, byte damage) {
		if (damage < 0) {
			System.out.println("wrong damage: "+damage);
			return;
		}
		if (hp == -Byte.MIN_VALUE) return;
		this.setHp(hp -= damage);
		if (hp < 0) {
			this.despawn();
		}
	}
	
	ModelInstance bb = null;
	public ModelInstance getFrame() {
		if (bb == null) {
			ModelBuilder modelBuilder = new ModelBuilder();
	        modelBuilder.begin();
	        Material material = new Material(ColorAttribute.createDiffuse(Color.WHITE));
	        long attributes = Usage.Position | Usage.Normal;
	        MeshPartBuilder mpb = modelBuilder.part("cube", GL20.GL_LINES, attributes, material);
	        BoxShapeBuilder.build(mpb, getHitbox().translate());
	        //System.out.println(toString()+" "+cx+" "+cy+" "+cz+" "+w+" "+h+" "+d);
	        Model cubeModel = modelBuilder.end();
	        return new ModelInstance(cubeModel);
		} else {
			bb.transform.setTranslation(pos.translate());
			return bb;
		}
	}
	
	private static int idCounter = Integer.MIN_VALUE;
	public static int genLocalId() {
		return idCounter++;
	}
	
	/**client side, call by packet*/
	public void setPos(Vector3D newpos) {
		if (world.getColumn(VectorU.posToColumn(newpos)) == null) {
			this.despawn();
		}
		this.beforepos.set(pos);
		this.pos = newpos;
	}
	
	public Entity clone(Vector3D pos, World world, ObjectData data, int localId) {
		GameU.end("не задан метод копирования");
		return null;
	}
	
	public ObjectData consumeData() {
		GameU.end("не заданый метод");
		return null;
	}

	public Entity cloneOnColumnLoad(Vector3D pos, World world, int localId) {
		GameU.end("не задан метод");
		return null;
	}
}
