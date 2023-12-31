package net.pzdcrp.Aselia.world.elements.inventory.items;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Texture;

import net.pzdcrp.Aselia.Hpb;
import net.pzdcrp.Aselia.data.BlockFace;
import net.pzdcrp.Aselia.data.Vector3D;
import net.pzdcrp.Aselia.player.Player;
import net.pzdcrp.Aselia.utils.GameU;
import net.pzdcrp.Aselia.world.elements.entities.Entity;


public class Item {
    public int count, id;
	public static final Map<Integer, Item> items = new HashMap<>() {{
		put(1, new DirtItem(0));
		put(3, new GlassItem(0));
		put(5, new GrassItem(0));
		put(0, new NoItem());
		put(6, new OakLogItem(0));
		put(7, new PlanksItem(0));
		put(2, new StoneItem(0));
		put(10, new CrateItem(0));
		put(11, new OakSlabItem(0));
		/*put(8, new TntCrateItem(0));
		put(4, new WaterBucketItem());*/
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

	public void placeBlockAction(Vector3D cp, BlockFace face, Vector3D origin, Player actor) {

	}

	public void breakBlockAction(Entity actor) {

	}

	public Texture getTexture() {
		if (this.isModel()) {
			Texture t =  Hpb.mutex.getItemTexture(getName());
			if (t == null) GameU.end("текстура не задана для предмета "+getClass().getName());
			return t;
		}
		GameU.end("модель вызвана на немодельном предмете "+this.getClass().getName());
		return null;
	}

	public String getName() {
		return "unnamed";
	}

	public boolean isModel() {
		return false;
	}

	public byte getDamage() {
		return 1;
	}

	public Item clone(int count) {
		GameU.end("метод копирования не назначен для класса "+this.getClass().getName());
		return null;
	}

	public String getDescription() {
		return null;
	}

	@Override
	public String toString() {
		return "Item[ i:"+id+", c:"+count+"]";
	}

	public static Item fromString(String s) {
		if (s.startsWith("Item[ i:") && s.endsWith("]")) {
			int count = 0;
			int id = 0;
			s = s.replace("Item[ ", "");
			s = s.replace("]", "");
			for (String a : s.split(", ")) {
				if (a.startsWith("c:")) {
					a = a.replace("c:", "");
					count = Integer.parseInt(a);
				} else if (a.startsWith("i:")) {
					a = a.replace("i:", "");
					id = Integer.parseInt(a);
				}
			}
			return items.get(id).clone(count);
		} else {
			return null;
		}
	}
}
