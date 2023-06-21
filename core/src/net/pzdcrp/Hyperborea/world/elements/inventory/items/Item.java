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
import net.pzdcrp.Hyperborea.world.elements.inventory.IInventory;


public class Item {
    public int count, id;
	public IInventory inventory;
	public static final Set<Item> items = new HashSet<Item>() {{
		add(new DirtItem(null,0));
		add(new GlassItem(null,0));
		add(new GrassItem(null,0));
		add(new NoItem(null));
		add(new OakLogItem(null,0));
		add(new PlanksItem(null,0));
		add(new StoneItem(null,0));
		add(new TntCrateItem(null,0));
		add(new WaterBucketItem(null,0));
	}};
	
	public Item(IInventory inventory, int id) {
		this.inventory = inventory;
		this.id = id;
		this.count = 0;
	}
	
	public Item(IInventory inventory, int id, int count) {
		this.inventory = inventory;
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
	
	public void onRClick(Vector3D cp) {
		
	}
	
	public void onLClick() {
		
	}
	
	public Texture getTexture() {
		if (this.isModel()) {
			try {
				return Hpb.mutex.getItemTexture(getName());
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("текстура не задана для предмета "+getClass().getName());
				System.exit(0);
			}
		}
		System.exit(0);
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
