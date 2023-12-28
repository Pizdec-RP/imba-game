package net.pzdcrp.Aselia.world.elements.inventory.items;

import net.pzdcrp.Aselia.data.BlockFace;
import net.pzdcrp.Aselia.data.ItemSortType;
import net.pzdcrp.Aselia.data.Vector3D;
import net.pzdcrp.Aselia.player.Player;
import net.pzdcrp.Aselia.world.elements.blocks.Crate;

public class CrateItem extends Item {
	public static ItemSortType sorttype = ItemSortType.usefull;
	public CrateItem(int count) {
		super(10, count);
	}

	@Override
	public void placeBlockAction(Vector3D cp, BlockFace face, Player actor) {
		if (actor.placeBlock(new Crate(cp))) actor.castedInv.wasteHandItem();
	}
	
	@Override
	public boolean isModel() {
		return true;
	}
	
	@Override
	public String getName() {
		return "Crate";
	}
	
	@Override
	public String getDescription() {
		return "simple storge for\nyou items";
	}
	
	@Override
	public Item clone(int count) {
		return new CrateItem(count);
	}
}
