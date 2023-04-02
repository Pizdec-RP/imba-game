package net.pzdcrp.wildland.world.elements.inventory.items;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.ModelInstance;

import net.pzdcrp.wildland.world.elements.inventory.IInventory;


public class Item {
    public int count, id;
	public IInventory inventory;
	
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
	
	public int maxCount() {
		return 99;
	}
	
	public void onRClick() {
		
	}
	
	public void onLClick() {
		
	}
	
	public Texture getTexture() {
		return null;
	}
	
	public String getName() {
		return "unnamed";
	}
}
