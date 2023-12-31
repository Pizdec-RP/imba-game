package net.pzdcrp.Aselia.world.elements.inventory.items;

import com.badlogic.gdx.graphics.Texture;

import net.pzdcrp.Aselia.Hpb;
import net.pzdcrp.Aselia.data.BlockFace;
import net.pzdcrp.Aselia.data.ItemSortType;
import net.pzdcrp.Aselia.data.Vector3D;
import net.pzdcrp.Aselia.player.Player;
import net.pzdcrp.Aselia.world.elements.blocks.Weed;

public class WeedItem extends Item {
	public static ItemSortType sorttype = ItemSortType.plants;
	public WeedItem(int count) {
		super(9, count);
	}

	@Override
	public void placeBlockAction(Vector3D cp, BlockFace face, Vector3D origin, Player actor) {
		if (actor.placeBlock(new Weed(cp))) actor.castedInv.wasteHandItem();
	}

	@Override
	public String getName() {
		return "Weed";
	}

	@Override
	public boolean isModel() {
		return false;
	}

	@Override
	public Texture getTexture() {
		return Hpb.mutex.getItemTexture("weed");
	}

	@Override
	public Item clone(int count) {
		return new WeedItem(count);
	}
}
