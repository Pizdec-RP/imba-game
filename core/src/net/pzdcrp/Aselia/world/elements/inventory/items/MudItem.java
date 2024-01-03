package net.pzdcrp.Aselia.world.elements.inventory.items;

import com.badlogic.gdx.graphics.Texture;

import net.pzdcrp.Aselia.Hpb;
import net.pzdcrp.Aselia.data.ItemSortType;

public class MudItem extends Item {
	public static ItemSortType sorttype = ItemSortType.onetype;
	public MudItem(int count) {
		super(12, count);
	}

	@Override
	public String getName() {
		return "Mud";
	}

	@Override
	public boolean isModel() {
		return false;
	}
	
	@Override
	public Texture getTexture() {
		return Hpb.mutex.getItemTexture("mud");
	}
	
	@Override
	public Item clone(int count) {
		return new MudItem(count);
	}
}
