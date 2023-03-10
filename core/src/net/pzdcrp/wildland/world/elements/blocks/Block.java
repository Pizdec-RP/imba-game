package net.pzdcrp.wildland.world.elements.blocks;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import net.pzdcrp.wildland.data.AABB;
import net.pzdcrp.wildland.data.BlockFace;
import net.pzdcrp.wildland.data.Pair;
import net.pzdcrp.wildland.data.Vector3D;

public class Block {
	
	public Vector3D pos;
	private static Map<Integer, Block> blocks = new ConcurrentHashMap<Integer, Block>( ) {
	private static final long serialVersionUID = 3707964282902670945L;
	{
		put(0, new Air(new Vector3D(),null));
		put(1, new Dirt(new Vector3D(),null));
		put(2, new Stone(new Vector3D(),null));
		put(3, new Glass(new Vector3D(),null));
		put(4, new RedSand(new Vector3D(),null));
		put(5, new Voed(new Vector3D(),null));
		put(6, new Grass(new Vector3D(),null));
		put(7, new OakLog(new Vector3D(),BlockFace.PX));//px
		put(8, new OakLog(new Vector3D(),BlockFace.PY));//py
		put(9, new OakLog(new Vector3D(),BlockFace.PZ));//pz
		put(10, new OakLog(new Vector3D(),BlockFace.NX));//nx
		put(11, new OakLog(new Vector3D(),BlockFace.NY));//ny
		put(12, new OakLog(new Vector3D(),BlockFace.NZ));//nz
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
	
	public boolean isCustonModel() {
		return false;
	}
	
	public void addModel(boolean py, boolean ny, boolean nx, boolean px, boolean nz, boolean pz, Map<String, Pair> modelsById) {
		
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
}
