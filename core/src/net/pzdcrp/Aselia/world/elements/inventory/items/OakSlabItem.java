package net.pzdcrp.Aselia.world.elements.inventory.items;

import net.pzdcrp.Aselia.data.BlockFace;
import net.pzdcrp.Aselia.data.ItemSortType;
import net.pzdcrp.Aselia.data.Vector3D;
import net.pzdcrp.Aselia.player.Player;
import net.pzdcrp.Aselia.world.elements.blocks.OakSlab;

public class OakSlabItem extends Item {
	public static ItemSortType sorttype = ItemSortType.onetype;
	public OakSlabItem(int count) {
		super(11, count);
	}

	@Override
	public void placeBlockAction(Vector3D cp, BlockFace face, Vector3D origin, Player actor) {
		//MathU.isDecimalAfterPointGreaterOrEqualHalf(origin.y)
		if (actor.placeBlock(new OakSlab(cp, (cp.y + 0.5f) < origin.y))) actor.castedInv.wasteHandItem();
	}

	@Override
	public String getName() {
		return "Oak slab";
	}

	@Override
	public boolean isModel() {
		return true;
	}

	@Override
	public Item clone(int count) {
		return new OakSlabItem(count);
	}
}