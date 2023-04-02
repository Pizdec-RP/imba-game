package net.pzdcrp.wildland.world.elements;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.pzdcrp.wildland.data.Vector2I;

public class Region {
	public Map<Vector2I, Column> columns = new ConcurrentHashMap<Vector2I, Column>();
	public Vector2I pos;
	
	public Region(Vector2I pos) {
		this.pos = pos;
	}
	
	public Column getColumn(Vector2I cpos) {
		if (columns.containsKey(cpos)) {
			return columns.get(cpos);
		} else {
			Column c = new Column(cpos, true);
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

	public void fromJson(JsonObject obj) {
		JsonArray jcolumns = obj.get("columns").getAsJsonArray();
		
		for (JsonElement jcole : jcolumns) {
			JsonObject jcol = jcole.getAsJsonObject();
			Vector2I cpos = Vector2I.fromString(jcol.get("pos").getAsString());
			Column col = new Column(cpos, false);
			col.fromJson(jcol);
			columns.put(cpos, col);
		}
	}
}
