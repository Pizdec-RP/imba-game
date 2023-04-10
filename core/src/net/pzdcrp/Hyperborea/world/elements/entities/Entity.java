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
import net.pzdcrp.Hyperborea.data.Physics;
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
	public double velX=0, velY=0, velZ=0;
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
	}
	
	public void tick() {
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
		OTripple tripple = VectorU.findFacingPair(this.getEyeLocation(), Hpb.world.player.cam.cam.direction);
		this.currentAimBlock = (Block) tripple.one;
		if (this.currentAimBlock != null) {
			this.currentAimFace = VectorU.getFace(currentAimBlock.pos, (Vector3D)tripple.two);
		}
		this.currentAimEntity = tripple.three == null ? null : (Entity) tripple.three;
	}
	
	public void updateGravity() {
		velY -= Physics.gravity;
		velY *= Physics.airdrag;
	}
	
	public List<Block> getNearBlocks() {
		AABB cube = getHitbox();
		//System.out.println(cube.toString());
		cube = cube.grow(Math.max(velX,1),Math.max(velY,1),Math.max(velZ,1));
		List<Block> b = new ArrayList<>();
		
		for (int tx = (int)Math.floor(Math.min(cube.maxX, cube.minX)); tx < Math.max(cube.maxX, cube.minX); tx++) {
			for (int tz = (int)Math.floor(Math.min(cube.maxZ, cube.minZ)); tz < Math.max(cube.maxZ, cube.minZ); tz++) {
				for (int ty = (int)Math.floor(Math.min(cube.maxY, cube.minY)); ty < Math.max(cube.maxY, cube.minY); ty++) {
					Block bl = Hpb.world.getBlock(new Vector3D(tx, ty, tz));//FIXME оптимизировать
					if (bl != null) {
						if (bl.isCollide()) {
							b.add(bl);
						}
						
					}
				}
			}
		}
		return b;
	}
	
	List<Vector3D> tempvel = new ArrayList<>(); 
	public void applyMovement() {
		colx = false;coly = false;colz = false;
		//System.out.println("huy "+velY);
		if (velX != 0 || velY != 0 || velZ != 0) {
			List<Block> nb = getNearBlocks();
			//System.out.println("nb "+nb.size());
			for (Block block : nb) {
				//for (Vector3D vel : tempvel) {
					if (block.collide(getHitbox().offset(velX, 0, 0))) {
						colx = true;
						if (velX < 0) {
							pos.x = block.getHitbox().maxX+Math.abs(hitbox.maxX);
						} else if (velX > 0) {
							pos.x = block.getHitbox().minX-Math.abs(hitbox.minX);
						}
						velX = 0;
					}
					if (block.collide(getHitbox().offset(0, velY, 0))) {
						coly = true;
						if (velY < 0) {
							pos.y = block.getHitbox().maxY;
							onGround = true;
						} else if (velY > 0) {
							pos.y = block.getHitbox().minY-hitbox.maxY;
							onGround = false;
						}
						velY = 0;
					}
					if (block.collide(getHitbox().offset(0, 0, velZ))) {
						colz = true;
						if (velZ < 0) {
							pos.z = block.getHitbox().maxZ+Math.abs(hitbox.maxZ);
						} else if (velZ > 0) {
							pos.z = block.getHitbox().minZ-Math.abs(hitbox.minZ);
						}
						velZ = 0;
					}
				//}
			}
			//System.out.println(coly);
			//System.out.println(velY);
			//System.out.println(y);
			if (!colx) pos.x+=velX;
			if (!coly) pos.y+=velY;
			if (!colz) pos.z+=velZ;
			
			if (this.isPlayer) {
				velX *= 0.6;
				velZ *= 0.6;
			} else {
				if (this.onGround) {
					velX *= 0.6;
					velZ *= 0.6;
				} else {
					velX *= 0.98;
					velZ *= 0.98;
				} 
			}
			
			if (Math.abs(velX) < Physics.badVel) velX = 0;
			if (Math.abs(velY) < Physics.badVel) velY = 0;
			if (Math.abs(velZ) < Physics.badVel) velZ = 0;
		}
	}
	
	public AABB getHitbox() {//FIXME
		return hitbox.noffset(pos);
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
}
