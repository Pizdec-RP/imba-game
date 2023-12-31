package net.pzdcrp.Aselia.world.elements.inventory.items;

import net.pzdcrp.Aselia.data.BlockFace;
import net.pzdcrp.Aselia.data.ItemSortType;
import net.pzdcrp.Aselia.data.Vector3D;
import net.pzdcrp.Aselia.player.Player;
import net.pzdcrp.Aselia.world.elements.blocks.Dirt;

public class DirtItem extends Item {
	public static ItemSortType sorttype = ItemSortType.onetype;
	public DirtItem(int count) {
		super(1, count);
	}

	@Override
	public void placeBlockAction(Vector3D cp, BlockFace face, Vector3D origin, Player actor) {
		if (actor.placeBlock(new Dirt(cp))) actor.castedInv.wasteHandItem();
	}

	@Override
	public boolean isModel() {
		return true;
	}

	@Override
	public String getName() {
		return "Dirt";
	}

	@Override
	public Item clone(int count) {
		return new DirtItem(count);
	}
}
