package net.pzdcrp.Aselia.world.elements.inventory.items;

import com.badlogic.gdx.graphics.Texture;

import net.pzdcrp.Aselia.Hpb;
import net.pzdcrp.Aselia.data.BlockFace;
import net.pzdcrp.Aselia.data.ItemSortType;
import net.pzdcrp.Aselia.data.Vector3D;
import net.pzdcrp.Aselia.player.Player;
import net.pzdcrp.Aselia.utils.VectorU;
import net.pzdcrp.Aselia.world.elements.blocks.Air;
import net.pzdcrp.Aselia.world.elements.blocks.Block;
import net.pzdcrp.Aselia.world.elements.blocks.Dirt;
import net.pzdcrp.Aselia.world.elements.blocks.Glass;
import net.pzdcrp.Aselia.world.elements.blocks.Grass;
import net.pzdcrp.Aselia.world.elements.blocks.OakLog;
import net.pzdcrp.Aselia.world.elements.blocks.Stone;
import net.pzdcrp.Aselia.world.elements.entities.Entity;
import net.pzdcrp.Aselia.world.elements.inventory.IInventory;

public class OakLogItem extends Item {
	public static ItemSortType sorttype = ItemSortType.onetype;
	public OakLogItem(int count) {
		super(6, count);
	}

	@Override
	public void placeBlockAction(Vector3D cp, BlockFace face, Player actor) {
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

