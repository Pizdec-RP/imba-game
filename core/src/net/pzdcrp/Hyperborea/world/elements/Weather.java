package net.pzdcrp.Hyperborea.world.elements;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.google.gson.JsonObject;

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.world.World;

public class Weather {
    private ArrayList<Cloud> clouds;

    public enum weatherType {
        clear, rain, thunder;
    }

    public Weather(World world) {
        this.clouds = new ArrayList<>();
    }

    public void tick() {
    	for (Cloud cloud : clouds) {
            cloud.update();
        }
    }

    public void render() {
        for (Cloud cloud : clouds) {
        	cloud.render();
        }
    }
	
	public void fromJson(JsonObject j) {
		
	}
	
	public JsonObject toJson() {//TODO
		return new JsonObject();
	}

	public void initAsNew() {
		
	}
}
