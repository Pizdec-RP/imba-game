package net.pzdcrp.Hyperborea.world.elements.entities;

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

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.AABB;
import net.pzdcrp.Hyperborea.data.ActionAuthor;
import net.pzdcrp.Hyperborea.data.BlockFace;
import net.pzdcrp.Hyperborea.data.EntityType;
import net.pzdcrp.Hyperborea.data.OTripple;
import net.pzdcrp.Hyperborea.data.Settings;
import net.pzdcrp.Hyperborea.data.DM;
import net.pzdcrp.Hyperborea.data.DamageSource;
import net.pzdcrp.Hyperborea.data.Vector2I;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.player.Player;
import net.pzdcrp.Hyperborea.utils.GameU;
import net.pzdcrp.Hyperborea.utils.VectorU;
import net.pzdcrp.Hyperborea.world.PlayerWorld;
import net.pzdcrp.Hyperborea.world.elements.Chunk;
import net.pzdcrp.Hyperborea.world.elements.Column;
import net.pzdcrp.Hyperborea.world.elements.blocks.Air;
import net.pzdcrp.Hyperborea.world.elements.blocks.Block;
import net.pzdcrp.Hyperborea.world.elements.blocks.Dirt;
import net.pzdcrp.Hyperborea.world.elements.inventory.EntityInventory;
import net.pzdcrp.Hyperborea.world.elements.inventory.IInventory;
import net.pzdcrp.Hyperborea.world.elements.inventory.PlayerInventory;

public class Entity {
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
	public Vector2I echc;
	public boolean firsttick = true;
	private boolean isPlayer = false;
	public EntityType type;
	public Vector3D beforepos;
	public AABB hitbox;
	public int justspawn = 50;
	
	//классы ссылки
	public Column curCol;
	public Block currentAimBlock = new Air(new Vector3D());
	public BlockFace currentAimFace = BlockFace.PX;
	public Entity currentAimEntity = null;
	public Vector3D currentaimpoint;
	
	public Entity(Vector3D pos, AABB hitbox, EntityType type) {
		this.type = type;
		this.pos=pos;
		this.beforepos=pos.clone();
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
	
	public void tick() {
		beforepos.set(pos);
		if (justspawn > 0) justspawn--;
		if (curCol == null) {
			Column col = Hpb.world.getColumn(pos.x,pos.z);
			this.curCol = col;
		}
		updateGravity();
		if (isPlayer) updateFacing();
		applyMovement();
		if (firsttick) {
			Column beforecol = Hpb.world.getColumn(beforeechc);
			if (!beforecol.entites.contains(this)) beforecol.entites.add(this);
			firsttick = false;
		}
		echc = new Vector2I(pos.x,pos.z);
		if (!beforeechc.equals(echc)) {
			if (isPlayer) Hpb.world.needToUpdateLoadedColumns = true;
			Column beforecol = Hpb.world.getColumn(beforeechc);
			Column col = Hpb.world.getColumn(pos.x,pos.z);
			this.curCol = col;
			if (col == null || beforecol == null) return;
			beforecol.entites.remove(this);
			col.entites.add(this);
			beforeechc = echc;
		}
	}
	
	public void updateFacing() {
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
		this.pos = pos;
		this.vel.setZero();
	}
	
	public void onPlayerClick(Player p) {
		
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
	        
	        if (vel.y < 0) {
		        if (fallstartblock == 0) {
		        	fallstartblock = pos.y;
		        }
	        } else {
	        	if (onGround) {
		        	if (fallstartblock != 0) {
			        	double falled = fallstartblock - pos.y;
			        	if (falled > 3.5) {
			        		int dmg = (int) (falled-3) * 2;
			        		//System.out.println("fall:"+falled+" fs:"+fallstartblock+" pv:"+pos.y+" dmg:"+dmg);
			        		hit(DamageSource.Fall, dmg);
			        	}
			        	fallstartblock = 0;
		        	}
		        } else {
	        		fallstartblock = 0;
		        }
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
		hp = jen.get("hp").getAsByte();
		fallstartblock = jen.get("fsb").getAsDouble();
	}
	
	public void despawn() {
		Vector2I echc = new Vector2I(pos.x,pos.z);
		Hpb.world.loadedColumns.get(echc).entites.remove(this);
	}

	public boolean placeBlock(Block block) {
		if (!this.isPlayer) {
			GameU.end("mob "+this.getClass().getName()+" cannot place blocks");
		}
		return Hpb.world.setBlock(block, ActionAuthor.player);
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
		if (Settings.showHitbox) {
			Hpb.render(getFrame());
		}
	}
	
	public int getType() {
		return 0;
	}

	public void hit(DamageSource src, int damage) {
		if (hp == -Byte.MIN_VALUE) return;
		this.hp -= damage;
		if (hp < 0) {
			this.despawn();
		}
	}
	
	public ModelInstance getFrame() {
		ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        Material material = new Material(ColorAttribute.createDiffuse(Color.WHITE));
        long attributes = Usage.Position | Usage.Normal;
        MeshPartBuilder mpb = modelBuilder.part("cube", GL20.GL_LINES, attributes, material);
        BoxShapeBuilder.build(mpb, getHitbox().translate());
        //System.out.println(toString()+" "+cx+" "+cy+" "+cz+" "+w+" "+h+" "+d);
        Model cubeModel = modelBuilder.end();
        return new ModelInstance(cubeModel);
	}
}
