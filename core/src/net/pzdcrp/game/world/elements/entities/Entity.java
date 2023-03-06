package net.pzdcrp.game.world.elements.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;

import net.pzdcrp.game.GameInstance;
import net.pzdcrp.game.data.AABB;
import net.pzdcrp.game.data.ColCoords;
import net.pzdcrp.game.data.EntityType;
import net.pzdcrp.game.data.Physics;
import net.pzdcrp.game.data.Vector3D;
import net.pzdcrp.game.player.Player;
import net.pzdcrp.game.world.World;
import net.pzdcrp.game.world.elements.Column;
import net.pzdcrp.game.world.elements.blocks.Block;

public class Entity {
	public Vector3D pos;
	public EntityType type;
	public AABB hitbox;
	public double velX=0, velY=0, velZ=0;
	public boolean colx=false,coly=false,colz=false,onGround=false;
	public float yaw = 0,pitch = 0;
	public ColCoords beforeechc;
	public boolean firsttick = true;
	
	public Map<EntityType, Class<? extends Entity>> entities = new HashMap<EntityType, Class<? extends Entity>>() {
	private static final long serialVersionUID = 5611014785520178934L;
	{
		put(EntityType.player, Player.class);
	}};
	
	public Entity(Vector3D pos, AABB hitbox, EntityType type) {
		this.type = type;
		this.pos=pos;
		this.hitbox=hitbox;
		this.beforeechc = new ColCoords(pos.x,pos.z);
	}
	
	public void tick() {
		updateGravity();
		applyMovement();
		if (firsttick) {
			Column beforecol = GameInstance.world.getColumn(beforeechc);
			if (!beforecol.entites.contains(this)) beforecol.entites.add(this);
			firsttick = false;
		}
		ColCoords echc = new ColCoords(pos.x,pos.z);
		if (this.type == EntityType.player) {
			if (!beforeechc.equals(echc)) {
				GameInstance.world.updateLoadedColumns();
			}
		}
		if (!beforeechc.equals(echc)) {
			Column beforecol = GameInstance.world.getColumn(beforeechc);
			Column col = GameInstance.world.getColumn(pos.x,pos.z);
			if (col == null || beforecol == null) return;
			beforecol.entites.remove(this);
			col.entites.add(this);
			beforeechc = echc;
		}
	}
	
	public void updateGravity() {
		if (velY > 0) {
			velY -= Physics.gravity*0.3;
		} else {
			//System.out.println(velY);
			velY -= Physics.gravity*0.4;
		}
		velY *= Physics.airdrag;
	}
	
	public List<Block> getNearBlocks() {
		AABB cube = getHitbox();
		//System.out.println(cube.toString());
		cube = cube.grow(Math.max(velX,1),Math.max(velY,1),Math.max(velZ,1));
		List<Block> b = new ArrayList<>();
		
		for (int tx = (int)Math.floor(Math.min(cube.maxX, cube.minX)); tx < Math.max(cube.maxX, cube.minX); tx++) {
			for (int tz = (int)Math.floor(Math.min(cube.maxZ, cube.minZ)); tz < Math.max(cube.maxZ, cube.minZ); tz++) {
				//System.out.println("cminy: "+cube.minY+" miny: "+(int)Math.floor(Math.min(cube.maxY, cube.minY))+" maxy: "+Math.max(cube.maxY, cube.minY));
				for (int ty = (int)Math.floor(Math.min(cube.maxY, cube.minY)); ty < Math.max(cube.maxY, cube.minY); ty++) {
					//System.out.println(tx+" "+ty+" "+tz);
					Block bl = GameInstance.world.getBlock(new Vector3D(tx, ty, tz));
					if (bl != null) {
						//System.out.println("a");
						if (bl.isCollide()) {
							b.add(bl);
							//System.out.println("block added");
						}
						
					}
				}
			}
		}
		return b;
	}
	
	public void applyMovement() {
		colx = false;coly = false;colz = false;
		//System.out.println("huy "+velY);
		if (velX != 0 || velY != 0 || velZ != 0) {
			List<Block> nb = getNearBlocks();
			//System.out.println("nb "+nb.size());
			for (Block block : nb) {
				
				if (block.collide(getHitbox().offset(velX, 0, 0))) {
					colx = true;
					if (velX < 0) {
						pos.x = block.getHitbox().maxX+0.3;
					} else if (velX > 0) {
						pos.x = block.getHitbox().minX-0.3;
					}
					velX = 0;
				}
				if (block.collide(getHitbox().offset(0, velY, 0))) {
					coly = true;
					if (velY < 0) {
						pos.y = block.getHitbox().maxY;
						onGround = true;
					} else if (velY > 0) {
						pos.y = block.getHitbox().minY-GameInstance.world.player.hitbox.maxY;
						System.out.println(block.getHitbox().minY);
						onGround = false;
					}
					velY = 0;
				}
				if (block.collide(getHitbox().offset(0, 0, velZ))) {
					colz = true;
					if (velZ < 0) {
						pos.z = block.getHitbox().maxZ+0.3;
					} else if (velZ > 0) {
						pos.z = block.getHitbox().minZ-0.3;
					}
					velZ = 0;
				}
			}
			//System.out.println(coly);
			//System.out.println(velY);
			//System.out.println(y);
			if (!colx) pos.x+=velX;
			if (!coly) pos.y+=velY;
			if (!colz) pos.z+=velZ;
			
			velX *= 0.6;
			velY *= 0.6;
			velZ *= 0.6;
			
			if (Math.abs(velX) < Physics.badVel) velX = 0;
			if (Math.abs(velY) < Physics.badVel) velY = 0;
			if (Math.abs(velZ) < Physics.badVel) velZ = 0;
		}
	}
	
	public AABB getHitbox() {
		return hitbox.noffset(pos);
	}
	
	public JsonObject getCustomProp() {
		return new JsonObject();
	}
	
	public void readCustomProp(JsonObject prop) {
		
	}
}