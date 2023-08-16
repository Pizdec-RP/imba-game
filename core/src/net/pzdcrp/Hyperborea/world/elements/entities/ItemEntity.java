package net.pzdcrp.Hyperborea.world.elements.entities;

import java.util.List;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.google.gson.JsonObject;

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.AABB;
import net.pzdcrp.Hyperborea.data.DamageSource;
import net.pzdcrp.Hyperborea.data.EntityType;
import net.pzdcrp.Hyperborea.data.Settings;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.data.objects.ObjectData;
import net.pzdcrp.Hyperborea.data.objects.entityObjectData.ItemEntityData;
import net.pzdcrp.Hyperborea.player.Player;
import net.pzdcrp.Hyperborea.utils.GameU;
import net.pzdcrp.Hyperborea.utils.VectorU;
import net.pzdcrp.Hyperborea.world.World;
import net.pzdcrp.Hyperborea.world.elements.blocks.Block;
import net.pzdcrp.Hyperborea.world.elements.inventory.items.Item;

public class ItemEntity extends Entity {
	private ModelInstance model;
	private int lifetime = 6000;
	private int blockid = -1;
	private boolean despawn = false;
	private Item item;
	
	/**
	 * For Column.fromJson only
	 * @param pos - position
	 */
	@Deprecated
	public ItemEntity(Vector3D pos, World world, int lid) {
		super(pos, new AABB(-0.15, -0.15, -0.15, 0.15, 0.15, 0.15), EntityType.item, world, lid);
	}
	
	public ItemEntity(Vector3D pos, int ofblock, Item item, World world, int lid) {
		super(pos, new AABB(-0.15, -0.15, -0.15, 0.15, 0.15, 0.15), EntityType.item, world, lid);
		this.blockid = ofblock;
		GameU.log("spawned item with id: "+blockid+" in "+(world.isLocal()?"client":"server"));
		if (blockid == 0) GameU.end("air can not be as item");
		this.item = item;
	}
	
	@Override
	public void render() {
		if (despawn) {
			super.despawn();
			return;
		}
		super.render();
		if (model == null) {
			GameU.log("getting id: "+blockid);
			this.model = Block.blockModels.get(blockid).copy();
			model.userData = new Object[] {"item", 0f};
			updateLight();
		}
		
		if (VectorU.sqrt(pos, Hpb.world.player.pos) > Settings.maxItemRenderDistance) return;
		
		Hpb.render(model);
		model.transform.setTranslation(
				(float)Hpb.lerp(beforepos.x, pos.x)-0.15f,
				(float)Hpb.lerp(beforepos.y, pos.y)-0.15f,
				(float)Hpb.lerp(beforepos.z, pos.z)-0.15f);
	}
	
	@Override
	public boolean invincible() {
		return true;
	}
	
	public void updateLight() {
		float f = Hpb.world.getLight((int)Math.floor(pos.x), (int)Math.floor(pos.y), (int)Math.floor(pos.z));
		((Object[])model.userData)[1] = f;
	}
	
	@Override
	public boolean tick() {
		boolean continuee = super.tick();
		if (!continuee) return false;
		if (world.isLocal() && model != null) updateLight();
		if (!world.isLocal()) {
			lifetime--;
			if (lifetime <= 0 || despawn) {
				this.despawn();
				super.despawn();
				return false;
			}
			List<Player> nearPlayers = world.getPlayers(pos, 1);
			
			@SuppressWarnings("unchecked")
			List<Player> nearestPlayers = (List<Player>) VectorU.sortNearest(nearPlayers, pos);
			
			for (Player player : nearestPlayers) {
				if (player.castedInv.canMerge(item)) {
					boolean val = player.castedInv.mergeFromItemEntity(item);
					if (val) {
						if (item.count == 0) {
							this.despawn();
							GameU.log("item count == 0, despawning");
						}//else цикл продолжается дальше
					}//else цикл продолжается дальше
				}
			}
			return true;//не используется
		} else {
			return true;//не используется
		}
	}
	
	@Override
	public void despawn() {
		despawn = true;
		GameU.log("despawning item");
		//super.despawn();
		//model.model.dispose();
	}
	
	@Override
	public void hit(DamageSource src, int damage) {
		if (src == DamageSource.Explosion) this.despawn();
	}
	
	@Override
	public void getJson(JsonObject jen) {
		super.getJson(jen);
		jen.addProperty("bid", blockid);
		jen.addProperty("lt", lifetime);
		jen.addProperty("item", this.item.toString());
	}
	
	@Override
	public void fromJson(JsonObject jen) {
		blockid = jen.get("bid").getAsInt();
		this.lifetime = jen.get("lt").getAsInt();
		this.item = Item.fromString(jen.get("item").getAsString());
	}
	
	@Override
	public int getType() {
		return 2;
	}
	
	@Override
	public Entity clone(Vector3D pos, World world, ObjectData data, int lid) {
		ItemEntityData ied = (ItemEntityData)data;
		Entity e = new ItemEntity(pos, ied.blockIdModel, null, world, lid);
		return e;
	}
	
	@Override
	public Entity cloneOnColumnLoad(Vector3D pos, World world, int lid) {
		Entity e = new ItemEntity(pos, world, lid);
		return e;
	}
	
	@Override
	public ObjectData consumeData() {
		ItemEntityData data = new ItemEntityData();
		if (blockid == 0) GameU.end("zeroid "+toString());
		data.blockIdModel = blockid;
		return data;
	}
	
	@Override
	public String toString() {
		return "ItemEntity id:"+blockid+" item: "+item.toString();
	}
}
