package net.pzdcrp.Hyperborea.world.elements.inventory.items;

import com.badlogic.gdx.graphics.Texture;

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.BlockFace;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.utils.VectorU;
import net.pzdcrp.Hyperborea.world.elements.blocks.Air;
import net.pzdcrp.Hyperborea.world.elements.blocks.Grass;
import net.pzdcrp.Hyperborea.world.elements.blocks.Water;
import net.pzdcrp.Hyperborea.world.elements.entities.Entity;
import net.pzdcrp.Hyperborea.world.elements.inventory.IInventory;

public class WaterBucketItem extends Item {

	public WaterBucketItem(int id) {
		super(id);
	}
	
	@Override
	public void placeBlockAction(Vector3D cp, Entity actor) {
		actor.placeBlock(
			new Water(
				cp,
				7
			)
		);
	}
	
	@Override
	public int stackSize() {
		return 1;
	}
	
	@Override
	public Texture getTexture() {
		return Hpb.mutex.getItemTexture("waterbucket");
	}
	
	@Override
	public String getName() {
		return "water bucket";
	}
}
