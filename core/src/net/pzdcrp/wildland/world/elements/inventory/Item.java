package net.pzdcrp.wildland.world.elements.inventory;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.g3d.ModelInstance;


public class Item {
    public static final DecimalFormat DECIMALFORMAT = new DecimalFormat("#.##");
    public int count, id;
	public IInventory inventory;
	
	public Item(IInventory inventory) {
		this.inventory = inventory;
		this.id = 0;
		this.count = 0;
	}
	
	public Item(int id) {
		this.id = id;
	}
	
	public Item(int id, int amount) {
		this.id = id;
		this.count = amount;
	}
	
	public void render() {
		
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
	
	public void onLClick() {
		
	}
}
