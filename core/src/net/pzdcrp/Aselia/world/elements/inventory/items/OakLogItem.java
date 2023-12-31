package net.pzdcrp.Aselia.world.elements.inventory.items;

import net.pzdcrp.Aselia.data.BlockFace;
import net.pzdcrp.Aselia.data.ItemSortType;
import net.pzdcrp.Aselia.data.Vector3D;
import net.pzdcrp.Aselia.player.Player;
import net.pzdcrp.Aselia.world.elements.blocks.OakLog;

public class OakLogItem extends Item {
	public static ItemSortType sorttype = ItemSortType.onetype;
	public OakLogItem(int count) {
		super(6, count);
	}

	@Override
	public void placeBlockAction(Vector3D cp, BlockFace face, Vector3D origin, Player actor) {
		if (actor.placeBlock(new OakLog(cp, face))) actor.castedInv.wasteHandItem();
	}

	@Override
	public String getName() {
		return "Oak log";
	}

	@Override
	public boolean isModel() {
		return true;
	}

	@Override
	public Item clone(int count) {
		return new OakLogItem(count);
	}
}

