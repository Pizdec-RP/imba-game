package net.pzdcrp.Hyperborea.world.elements.inventory.items;

import com.badlogic.gdx.graphics.Texture;

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.BlockFace;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.utils.VectorU;
import net.pzdcrp.Hyperborea.world.elements.blocks.Air;
import net.pzdcrp.Hyperborea.world.elements.blocks.Block;
import net.pzdcrp.Hyperborea.world.elements.blocks.Dirt;
import net.pzdcrp.Hyperborea.world.elements.entities.Entity;
import net.pzdcrp.Hyperborea.world.elements.inventory.IInventory;

public class DirtItem extends Item {
	public DirtItem(int count) {
		super(1, count);
	}

	@Override
	public void placeBlockAction(Vector3D cp, Entity actor) {
		actor.placeBlock(new Dirt(cp));
	}
	
	@Override
	public boolean isModel() {
		return true;
	}
	
	@Override
	public String getName() {
		return "Dirt";
	}
}
