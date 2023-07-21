package net.pzdcrp.Hyperborea.world.elements.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.google.gson.JsonObject;

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.AABB;
import net.pzdcrp.Hyperborea.data.DamageSource;
import net.pzdcrp.Hyperborea.data.EntityType;
import net.pzdcrp.Hyperborea.data.MBIM;
import net.pzdcrp.Hyperborea.data.Settings;
import net.pzdcrp.Hyperborea.data.Vector2I;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.player.Player;
import net.pzdcrp.Hyperborea.utils.GameU;
import net.pzdcrp.Hyperborea.utils.VectorU;
import net.pzdcrp.Hyperborea.world.elements.blocks.Block;
import net.pzdcrp.Hyperborea.world.elements.inventory.items.Item;
import net.pzdcrp.Hyperborea.world.elements.inventory.items.NoItem;

public class ItemEntity extends Entity {
	private ModelInstance model;
	private int lifetime = 6000, pickanim = 5;
	private int blockid;
	private boolean despawn = false;
	private Item item;
	
	/**
	 * For Column.fromJson only
	 * @param pos - position
	 */
	@Deprecated
	public ItemEntity(Vector3D pos) {
		super(pos, new AABB(-0.15, -0.15, -0.15, 0.15, 0.15, 0.15), EntityType.item);
	}
	
	public ItemEntity(Vector3D pos, Block ofblock, Item item) {
		super(pos, new AABB(-0.15, -0.15, -0.15, 0.15, 0.15, 0.15), EntityType.item);
		this.blockid = ofblock.getId();
		this.item = item;
	}
	
	@Override
	public void render() {
		super.render();
		if (despawn) {
			super.despawn();
			return;
		}
		if (model == null) {
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
	
	public void updateLight() {
		float f = Hpb.world.getLight((int)Math.floor(pos.x), (int)Math.floor(pos.y), (int)Math.floor(pos.z));
		((Object[])model.userData)[1] = f;
	}
	
	@Override
	public void tick() {
		if (model == null || despawn) return;
		updateLight();
		super.tick();
		lifetime--;
		if (lifetime <= 0) this.despawn();
		int beforecount = item.count;
		Player mergedEntity;
		for (Entity e : Hpb.world.getEntities(pos, 2d)) {
			if (e instanceof Player) {
				Vector3D eyeloc = e.getEyeLocation();
				double dist = pos.distanceSq(eyeloc);
				if (dist < 0.3d) {
					mergedEntity = (Player)e;
					mergedEntity.castedInv.mergeFromItemEntity(item);
				} else {
					vel = pos.getDirection(eyeloc).multiply(0.3);
				}
				break;
			}
		}
		if (item.count == 0) {
			despawn = true;
			
		}
		else if (item.count != beforecount) {
			
		}
	}
	
	@Override
	public void despawn() {
		despawn = true;
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
}
