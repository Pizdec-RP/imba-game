package net.pzdcrp.Hyperborea.world.elements.inventory.items;

import com.badlogic.gdx.graphics.Texture;

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.BlockFace;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.player.Player;
import net.pzdcrp.Hyperborea.world.elements.blocks.Grass;
import net.pzdcrp.Hyperborea.world.elements.blocks.Weed;
import net.pzdcrp.Hyperborea.world.elements.entities.Entity;
import net.pzdcrp.Hyperborea.world.elements.inventory.IInventory;

public class WeedItem extends Item {
	public WeedItem(int count) {
		super(9, count);
	}

	@Override
	public void placeBlockAction(Vector3D cp, BlockFace face, Player actor) {
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
