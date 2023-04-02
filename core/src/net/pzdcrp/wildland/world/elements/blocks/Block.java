package net.pzdcrp.wildland.world.elements.blocks;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.graphics.g3d.ModelInstance;

import net.pzdcrp.wildland.data.AABB;
import net.pzdcrp.wildland.data.BlockFace;
import net.pzdcrp.wildland.data.MBIM;
import net.pzdcrp.wildland.data.Vector3D;
import net.pzdcrp.wildland.world.elements.entities.Entity;
import net.pzdcrp.wildland.world.elements.inventory.items.Item;

public class Block {
	
	public Vector3D pos;
	private static Map<Integer, Block> blocks = new ConcurrentHashMap<Integer, Block>( ) {
	private static final long serialVersionUID = 3707964282902670945L;
	{
		put(0, new Air(new Vector3D(),null));
		put(1, new Dirt(new Vector3D(),null));
		put(2, new Stone(new Vector3D(),null));
		put(3, new Glass(new Vector3D(),null));
		put(5, new Voed(new Vector3D(),null));
		put(6, new Grass(new Vector3D(),null));
		put(7, new OakLog(new Vector3D(),BlockFace.PX));
		put(8, new OakLog(new Vector3D(),BlockFace.PY));
		put(9, new OakLog(new Vector3D(),BlockFace.PZ));
		put(10, new OakLog(new Vector3D(),BlockFace.NX));
		put(11, new OakLog(new Vector3D(),BlockFace.NY));
		put(12, new OakLog(new Vector3D(),BlockFace.NZ));
		put(13, new Planks(new Vector3D(),null));
		put(14, new TntCrate(new Vector3D(),null));
	}};
	private static Map<Integer, Integer> BlockidToItemid = new ConcurrentHashMap<Integer, Integer>() {
		private static final long serialVersionUID = 37079642665568945L;
		{
			put(0, 0);
			put(1, 1);
			put(2, 2);
			put(3, 3);
			put(5, 4);
			put(6, 5);
			put(8, 6);
			put(7, 6);
			put(9, 6);
			put(10, 6);
			put(11, 6);
			put(12, 6);
			put(13, 7);
			put(14, 8);
		}};
	public enum BlockType {
		air, Void, solid, sandy, glass, nonfull;
	}
	public String texture;
	
	public Block(Vector3D pos, String texture) {
		this.pos = pos.VecToInt();
		this.texture = texture;
	}
	
	public static int idByBlock(Block block) {
		for (Entry<Integer, Block> entry : blocks.entrySet()) {
			if (entry.getValue().equals(block)) return entry.getKey();
		}
		System.out.println("unregistered block: "+block.toString());
		System.exit(0);
		return 0;
	}
	
	public static Block blockById(int id, Vector3D v) {
		try {
			Block block = Block.blocks.get(id).clone();
			block.pos = v;
			return block;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		return null;
	}
	
	public static Block getAbstractBlock(int id) {
		return blocks.get(id);
	}
	
	public boolean isRenderable() {
		return true;
	}
	
	public boolean isCollide() {
		return true;
	}
	
	public boolean collide(AABB with) {
		return new AABB(pos.x,pos.y,pos.z, pos.x+1,pos.y+1,pos.z+1).collide(with);
	}
	
	public AABB getHitbox() {
		return new AABB(pos.x,pos.y,pos.z, pos.x+1,pos.y+1,pos.z+1);
	}
	
	public BlockType getType() {
		return BlockType.solid;
	}
	
	public void addModel(boolean py, boolean ny, boolean nx, boolean px, boolean nz, boolean pz, MBIM mbim) {
		
	}
	
	public BlockFace getFace() {
		return BlockFace.PX;
	}
	
	public Block clone() {
		return null;
	}
	
	@Override
	public boolean equals(Object block) {
		if (block instanceof Block) {
			Block b = (Block) block;
			if (block.getClass() == this.getClass() && b.getFace() == this.getFace()) return true;
			
		}
		return false;
	}
	
	@Override
	public String toString() {
		return this.getClass().getName()+"[face:"+this.getFace()+", pos: "+this.pos.toStringInt()+"]";
	}
	
	public boolean onClick(Entity actor) {
		return false;
	}
	
	public boolean tickable() {
		return false;
	}
	
	public static int itemIdToBlockId(int itemid) {
		for (Entry<Integer, Integer> entry : BlockidToItemid.entrySet()) {
			if (entry.getValue() == itemid) return entry.getKey();
		}
		System.err.println("eblan chini! nema id: "+itemid);
		System.exit(0);
		return 0;
	}

	public static Block blockByItem(Item item) {
		return blocks.get(itemIdToBlockId(item.getId()));
	}

	public void tick() {
		
	}
}
