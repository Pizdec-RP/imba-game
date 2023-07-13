package net.pzdcrp.Hyperborea.world.elements.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
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
import net.pzdcrp.Hyperborea.utils.VectorU;
import net.pzdcrp.Hyperborea.world.elements.blocks.Block;
import net.pzdcrp.Hyperborea.world.elements.inventory.items.Item;
import net.pzdcrp.Hyperborea.world.elements.inventory.items.NoItem;

public class ItemEntity extends Entity {
	private ModelInstance model;
	private int lifetime = 6000;
	private int blockid;
	private boolean despawn = false;
	
	/**
	 * For Column.fromJson only
	 * @param pos
	 */
	public ItemEntity(Vector3D pos) {
		super(pos, new AABB(-0.1, -0.1, -0.1, 0.1, 0.1, 0.1), EntityType.item);
	}
	
	public ItemEntity(Vector3D pos, Block ofblock) {
		super(pos, new AABB(-0.1, -0.1, -0.1, 0.1, 0.1, 0.1), EntityType.item);
		this.blockid = ofblock.getId();
	}
	
	@Override
	public void render() {
		if (despawn) {
			super.despawn();
			return;
		}
		if (model == null) {
			this.model = Block.blockModels.get(blockid).copy();
		}
		if (VectorU.sqrt(pos, Hpb.world.player.pos) > Settings.maxItemRenderDistance) return;
		Hpb.render(model);
		AABB h = this.getHitbox();
		h.formmodel();
		Hpb.render(h.model);
		model.transform.setTranslation(
				(float)Hpb.lerp(pos.x, beforepos.x),
				(float)Hpb.lerp(pos.y, beforepos.y),
				(float)Hpb.lerp(pos.z, beforepos.z));
		//TODO освещение мира должно влиять на освещение предмета
	}
	
	@Override
	public void onPlayerClick(Player p) {
		//add to inventory
	}
	
	@Override
	public void tick() {
		super.tick();
		lifetime--;
		if (lifetime <= 0) this.despawn();
		
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
	}
	
	@Override
	public void fromJson(JsonObject jen) {
		blockid = jen.get("bid").getAsInt();
		//this.model = Block.blockModels.get(blockid).copy();
		this.lifetime = jen.get("lt").getAsInt();
	}
	
	@Override
	public int getType() {
		return 2;
	}
}
