package net.pzdcrp.Aselia.world.elements.inventory.items;

import net.pzdcrp.Aselia.data.BlockFace;
import net.pzdcrp.Aselia.data.ItemSortType;
import net.pzdcrp.Aselia.data.Vector3D;
import net.pzdcrp.Aselia.player.Player;
import net.pzdcrp.Aselia.world.elements.blocks.Stone;

public class StoneItem extends Item {
	public static ItemSortType sorttype = ItemSortType.onetype;
	public StoneItem(int count) {
		super(2, count);
	}

	@Override
	public void placeBlockAction(Vector3D cp, BlockFace face, Vector3D origin, Player actor) {
		if (actor.placeBlock(new Stone(cp))) actor.castedInv.wasteHandItem();
	}

	@Override
	public String getName() {
		return "Stone";
	}

	@Override
	public boolean isModel() {
		return true;
	}

	@Override
	public Item clone(int count) {
		return new StoneItem(count);
	}
}
