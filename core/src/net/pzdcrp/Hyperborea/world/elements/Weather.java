package net.pzdcrp.Hyperborea.world.elements;

import com.google.gson.JsonObject;

import net.pzdcrp.Hyperborea.world.World;

public class Weather {
	private World world;
	
	public enum weatherType {
		clear, rain, thunder;
	}
	
	public Weather(World world) {
		this.world = world;
	}
	
	public void tick() {
		
	}
	
	public void render() {
		
	}
	
	public void initAsNew() {
		
	}
	
	public void fromJson(JsonObject j) {
		
	}
	
	public JsonObject toJson() {//TODO
		return new JsonObject();
	}
}
