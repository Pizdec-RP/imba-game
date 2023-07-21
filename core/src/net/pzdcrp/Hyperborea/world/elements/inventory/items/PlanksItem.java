package net.pzdcrp.Hyperborea.world.elements.inventory.items;

import com.badlogic.gdx.graphics.Texture;

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.BlockFace;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.player.Player;
import net.pzdcrp.Hyperborea.utils.VectorU;
import net.pzdcrp.Hyperborea.world.elements.blocks.Air;
import net.pzdcrp.Hyperborea.world.elements.blocks.Block;
import net.pzdcrp.Hyperborea.world.elements.blocks.Grass;
import net.pzdcrp.Hyperborea.world.elements.blocks.Planks;
import net.pzdcrp.Hyperborea.world.elements.entities.Entity;
import net.pzdcrp.Hyperborea.world.elements.inventory.IInventory;

public class PlanksItem extends Item {
	public PlanksItem(int count) {
		super(7, count);
	}

	@Override
	public void placeBlockAction(Vector3D cp, Player actor) {
		if (actor.placeBlock(new Planks(cp))) actor.castedInv.wasteHandItem();
	}
	
	@Override
	public String getName() {
		return "Planks";
	}
	
	@Override
	public boolean isModel() {
		return true;
	}
	
	@Override
	public Item clone(int count) {
		return new PlanksItem(count);
	}
}
