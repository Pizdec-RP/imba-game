package net.pzdcrp.Aselia.world.elements.inventory.items;

import net.pzdcrp.Aselia.data.BlockFace;
import net.pzdcrp.Aselia.data.ItemSortType;
import net.pzdcrp.Aselia.data.Vector3D;
import net.pzdcrp.Aselia.player.Player;
import net.pzdcrp.Aselia.world.elements.blocks.Grass;

public class GrassItem extends Item {
	public static ItemSortType sorttype = ItemSortType.onetype;
	public GrassItem(int count) {
		super(5, count);
	}

	@Override
	public void placeBlockAction(Vector3D cp, BlockFace face, Vector3D origin, Player actor) {
		if (actor.placeBlock(new Grass(cp))) actor.castedInv.wasteHandItem();
	}

	@Override
	public String getName() {
		return "Grass";
	}

	@Override
	public boolean isModel() {
		return true;
	}
	
	@Override
	public Item clone(int count) {
		return new GrassItem(count);
	}
}