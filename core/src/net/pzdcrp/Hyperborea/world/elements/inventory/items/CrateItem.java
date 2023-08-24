package net.pzdcrp.Hyperborea.world.elements.inventory.items;

import net.pzdcrp.Hyperborea.data.BlockFace;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.player.Player;
import net.pzdcrp.Hyperborea.world.elements.blocks.Crate;

public class CrateItem extends Item {
	public CrateItem(int count) {
		super(1, count);
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
	public Item clone(int count) {
		return new CrateItem(count);
	}
}
