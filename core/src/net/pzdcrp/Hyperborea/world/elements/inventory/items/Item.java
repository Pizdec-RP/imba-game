package net.pzdcrp.Hyperborea.world.elements.inventory.items;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.ModelInstance;

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.utils.ThreadU;
import net.pzdcrp.Hyperborea.world.elements.entities.Entity;
import net.pzdcrp.Hyperborea.world.elements.inventory.IInventory;


public class Item {
    public int count, id;
	public static final Set<Item> items = new HashSet<Item>() {{
		add(new DirtItem(0));
		add(new GlassItem(0));
		add(new GrassItem(0));
		add(new NoItem());
		add(new OakLogItem(0));
		add(new PlanksItem(0));
		add(new StoneItem(0));
		add(new TntCrateItem(0));
		add(new WaterBucketItem(0));
	}};
	
	public Item(int id) {
		this.id = id;
		this.count = 0;
	}
	
	public Item(int id, int count) {
		this.id = id;
		this.count = count;
	}

	public int getId() {
		return id;
	}

	public int getСount() {
		return count;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setСount(int count) {
		this.count = count;
	}
	
	public int stackSize() {
		return 99;
	}
	
	public void placeBlockAction(Vector3D cp, Entity actor) {
		
	}
	
	public void breakBlockAction(Entity actor) {
		
	}
	
	public Texture getTexture() {
		if (this.isModel()) {
			Texture t =  Hpb.mutex.getItemTexture(getName());
			if (t == null) ThreadU.end("текстура не задана для предмета "+getClass().getName());
			return t;
		}
		ThreadU.end("модель вызвана на немодельном предмете");
		return null;
	}
	
	public String getName() {
		return "unnamed";
	}
	
	public boolean isModel() {
		return false;
	}

	public int getDamage() {
		return 1;
	}
}
