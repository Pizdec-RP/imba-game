package net.pzdcrp.Hyperborea.world;

import java.util.List;

import net.pzdcrp.Hyperborea.data.ActionAuthor;
import net.pzdcrp.Hyperborea.data.Vector2I;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.world.elements.Column;
import net.pzdcrp.Hyperborea.world.elements.blocks.Block;
import net.pzdcrp.Hyperborea.world.elements.entities.Entity;

public interface World {
	public static final int chunks = 16, maxheight = chunks * 16, buildheight = maxheight-1;

	void tick();

	Block getBlock(Vector3D v);

	Block getBlock(double x, double y, double z);

	void setLight(int x, int y, int z, int num);

	int getLight(int x, int y, int z);

	Column getColumn(Vector2I cc);

	Column getColumn(double x, double z);

	void breakBlock(Vector3D pos);

	void spawnEntity(Entity entity);

	boolean setBlock(Block block, ActionAuthor author);

	boolean posDostupna(int x, int y, int z);

	List<Entity> getEntities(Vector3D pos, double radius);
	
	boolean isLocal();
	
}
