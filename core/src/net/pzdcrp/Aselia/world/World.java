package net.pzdcrp.Aselia.world;

import java.util.List;
import java.util.Map;

import de.datasecs.hydra.shared.protocol.packets.Packet;
import net.pzdcrp.Aselia.data.ActionAuthor;
import net.pzdcrp.Aselia.data.Vector2I;
import net.pzdcrp.Aselia.data.Vector3D;
import net.pzdcrp.Aselia.player.Player;
import net.pzdcrp.Aselia.world.elements.Column;
import net.pzdcrp.Aselia.world.elements.blocks.Block;
import net.pzdcrp.Aselia.world.elements.entities.Entity;

public interface World {
	public static final int chunks = 16, maxheight = chunks * 16, buildheight = maxheight-1;

	void tick();

	boolean containColumn(Vector2I pos);

	Block getBlock(Vector3D v);

	Block getBlock(float x, float y, float z);

	void setLight(int x, int y, int z, int num);

	int getLight(int x, int y, int z);

	Column getColumn(Vector2I cc);

	/**
	 *
	 * @param x - block pos
	 * @param z - block pos
	 * @return
	 */
	Column getColumn(double x, double z);

	void breakBlock(Vector3D pos);

	void spawnEntity(Entity entity);

	boolean setBlock(Block block, ActionAuthor author);

	void setBlock(int block, Vector3D pos, ActionAuthor author);

	boolean posDostupna(int x, int y, int z);

	List<Entity> getEntities(Vector3D pos, float radius);

	List<Player> getPlayers(Vector3D pos, float radius);

	boolean isLocal();

	Column getWithoutLoad(Vector2I cc);

	Map<Vector2I,Column> getLoadedColumns();

	boolean posDostupna(Vector3D pos);

	void broadcastByColumn(Vector2I pos, Packet p);

	Entity getEntity(int id);
}
