package net.pzdcrp.Hyperborea.world.elements;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.Vector2I;
import net.pzdcrp.Hyperborea.utils.GameU;
import net.pzdcrp.Hyperborea.world.World;

public class Region {
	public Map<Vector2I, Column> columns = new ConcurrentHashMap<Vector2I, Column>();
	public Vector2I pos;
	public World world;
	
	public Region(Vector2I pos, World world) {
		if (world == null) GameU.end("null world");
		this.pos = pos;
		this.world = world;
	}
	
	public Column getColumn(Vector2I cpos) {
		if (columns.containsKey(cpos)) {
			return columns.get(cpos);
		} else {
			Column c = new Column(cpos, true, world);
			columns.put(cpos, c);
			return c;
		}
	}
	
	public JsonObject toJson() {
		JsonObject jreg = new JsonObject();
		
		JsonArray jcolumns = new JsonArray();
		
		for (Column column : columns.values()) {
			jcolumns.add(column.toJson());
		}
		
		jreg.add("columns", jcolumns);
		
		return jreg;
	}

	public void fromJson(JsonObject obj) throws Exception {
		if (world == null) GameU.end("null world");
		JsonArray jcolumns = obj.get("columns").getAsJsonArray();
		
		for (JsonElement jcole : jcolumns) {
			JsonObject jcol = jcole.getAsJsonObject();
			Vector2I cpos = Vector2I.fromString(jcol.get("pos").getAsString());
			Column col = new Column(cpos, false, world);
			col.fromJson(jcol);
			columns.put(cpos, col);
		}
	}
}
