package net.pzdcrp.Hyperborea.world.elements.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.google.gson.JsonObject;

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.AABB;
import net.pzdcrp.Hyperborea.data.BlockFace;
import net.pzdcrp.Hyperborea.data.EntityType;
import net.pzdcrp.Hyperborea.data.OTripple;
import net.pzdcrp.Hyperborea.data.DM;
import net.pzdcrp.Hyperborea.data.Vector2I;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.player.Player;
import net.pzdcrp.Hyperborea.utils.VectorU;
import net.pzdcrp.Hyperborea.world.World;
import net.pzdcrp.Hyperborea.world.elements.Column;
import net.pzdcrp.Hyperborea.world.elements.blocks.Air;
import net.pzdcrp.Hyperborea.world.elements.blocks.Block;
import net.pzdcrp.Hyperborea.world.elements.blocks.Dirt;
import net.pzdcrp.Hyperborea.world.elements.inventory.EntityInventory;
import net.pzdcrp.Hyperborea.world.elements.inventory.IInventory;
import net.pzdcrp.Hyperborea.world.elements.inventory.PlayerInventory;

public class Entity {
	public Vector3D pos, beforepos;
	public EntityType type;
	public AABB hitbox;
	public Vector3D vel = new Vector3D();
	public boolean colx=false,coly=false,colz=false,onGround=false;
	public float yaw = 0,pitch = 0;
	public Vector2I beforeechc;
	public boolean firsttick = true;
	private boolean isPlayer = false;
	
	//FIXME не сохраняется
	public Block currentAimBlock = new Air(new Vector3D());
	public BlockFace currentAimFace = BlockFace.PX;
	public Entity currentAimEntity = null;
	public IInventory inventory;
	protected byte hp; 
	
	//классы ссылки
	public Column curCol;
	
	public Map<EntityType, Class<? extends Entity>> entities = new HashMap<EntityType, Class<? extends Entity>>() {
	private static final long serialVersionUID = 5611014785520178934L;
	{
		put(EntityType.player, Player.class);
	}};
	
	public Entity(Vector3D pos, AABB hitbox, EntityType type) {
		this.type = type;
		this.pos=pos;
		this.beforepos=pos;
		this.hitbox=hitbox;
		this.beforeechc = new Vector2I(pos.x,pos.z);
		if (type == EntityType.player) {
			inventory = new PlayerInventory(this);
			this.isPlayer = true;
		} else {
			inventory = new EntityInventory(this);
		}
		this.hp = maxhp();
	}
	
	public void tick() throws Exception {
		if (curCol == null) {
			Column col = Hpb.world.getColumn(pos.x,pos.z);
			this.curCol = col;
		}
		updateGravity();
		if (type == EntityType.player) updateFacing();
		applyMovement();
		if (firsttick) {
			Column beforecol = Hpb.world.getColumn(beforeechc);
			if (!beforecol.entites.contains(this)) beforecol.entites.add(this);
			firsttick = false;
		}
		Vector2I echc = new Vector2I(pos.x,pos.z);
		if (this.type == EntityType.player) {
			if (!beforeechc.equals(echc)) {
				Hpb.world.updateLoadedColumns();
			}
		}
		if (!beforeechc.equals(echc)) {
			Column beforecol = Hpb.world.getColumn(beforeechc);
			Column col = Hpb.world.getColumn(pos.x,pos.z);
			this.curCol = col;
			if (col == null || beforecol == null) return;
			beforecol.entites.remove(this);
			col.entites.add(this);
			beforeechc = echc;
		}
		beforepos.set(pos);
	}
	
	public void updateFacing() {
		OTripple tripple = VectorU.findFacingPair(this.getEyeLocation(), Hpb.world.player.cam.cam.direction, this);
		this.currentAimBlock = (Block) tripple.one;
		if (this.currentAimBlock != null) {
			this.currentAimFace = VectorU.getFace(currentAimBlock.pos, (Vector3D)tripple.two);
		}
		this.currentAimEntity = tripple.three == null ? null : (Entity) tripple.three;
	}
	
	public void updateGravity() {
		vel.y -= DM.gravity;
		vel.y *= DM.airdrag;
	}
	
	public List<AABB> getNearBlocks() {
		AABB cube = getHitbox();
		cube = cube.grow(Math.max(vel.x,1),Math.max(vel.y,1),Math.max(vel.z,1));
		List<AABB> b = new ArrayList<>();
		
		for (int tx = (int)Math.floor(Math.min(cube.maxX, cube.minX)); tx < Math.max(cube.maxX, cube.minX); tx++) {
			for (int tz = (int)Math.floor(Math.min(cube.maxZ, cube.minZ)); tz < Math.max(cube.maxZ, cube.minZ); tz++) {
				for (int ty = (int)Math.floor(Math.min(cube.maxY, cube.minY)); ty < Math.max(cube.maxY, cube.minY); ty++) {
					Block bl = Hpb.world.getBlock(new Vector3D(tx, ty, tz));//FIXME оптимизировать
					if (bl != null) {
						if (bl.isCollide()) {
							b.add(bl.getHitbox());
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
			double bx,by,bz;
			for (AABB collidedBB : nb) {
				by = vel.y;
				vel.y = collidedBB.calculateYOffset(this.getHitbox(), vel.y);
				if (by != vel.y) {
					coly = true;
				}
			}
			if (vel.y > 0) {
				onGround = false;
			} else if (vel.y == 0) {
				onGround = true;
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
	
	public JsonObject getCustomProp() {
		return new JsonObject();
	}
	
	public void readCustomProp(JsonObject prop) {
		
	}
	
	public void despawn() {
		Vector2I echc = new Vector2I(pos.x,pos.z);
		Hpb.world.loadedColumns.get(echc).entites.remove(this);
	}

	public void placeBlock(Block block) {
		Hpb.world.setBlock(block);
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
		
	}

	public void hit(Entity enemy, int damage) {
		if (hp == -Byte.MIN_VALUE) return;
		this.hp -= damage;
		if (hp < 0) {
			//Hpb.world.player.chat.send(this.getClass().getName()+" killed by "+enemy.getClass().getName());
			if (isPlayer) {
				((Player)this).deadScreen();
			} else {
				this.despawn();
			}
		}
	}
}
