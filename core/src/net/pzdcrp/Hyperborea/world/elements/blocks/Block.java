package net.pzdcrp.Hyperborea.world.elements.blocks;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.google.gson.JsonObject;

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.AABB;
import net.pzdcrp.Hyperborea.data.BlockFace;
import net.pzdcrp.Hyperborea.data.DM;
import net.pzdcrp.Hyperborea.data.MBIM;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.world.World;
import net.pzdcrp.Hyperborea.world.elements.entities.Entity;
import net.pzdcrp.Hyperborea.world.elements.inventory.items.Item;

public class Block {
	public static World world;// = GameInstance.world;
	public Vector3D pos;
	private static Map<Integer, Block> blocks;
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
		air, solid, transparent;
	}
	public String texture;
	protected AABB hitbox;
	
	public static void init() {
		
		blocks = new ConcurrentHashMap<Integer, Block>( ) {
			private static final long serialVersionUID = 3707964282902670945L;
			{
				put(0, new Air(new Vector3D()));
				put(1, new Dirt(new Vector3D()));
				put(2, new Stone(new Vector3D()));
				put(3, new Glass(new Vector3D()));
				put(5, new Voed(new Vector3D()));
				put(6, new Grass(new Vector3D()));
				put(7, new OakLog(new Vector3D(),BlockFace.PX));
				put(8, new OakLog(new Vector3D(),BlockFace.PY));
				put(9, new OakLog(new Vector3D(),BlockFace.PZ));
				put(10, new OakLog(new Vector3D(),BlockFace.NX));
				put(11, new OakLog(new Vector3D(),BlockFace.NY));
				put(12, new OakLog(new Vector3D(),BlockFace.NZ));
				put(13, new Planks(new Vector3D()));
				put(14, new TntCrate(new Vector3D()));
				put(15, new Water(new Vector3D(), 1));
				put(16, new Water(new Vector3D(), 2));
				put(17, new Water(new Vector3D(), 3));
				put(18, new Water(new Vector3D(), 4));
				put(19, new Water(new Vector3D(), 5));
				put(20, new Water(new Vector3D(), 6));
				put(21, new Water(new Vector3D(), 7));
			}};
	}
	
	public Block(Vector3D pos, String texture, AABB hitbox) {
		this.pos = pos.VecToInt();
		this.texture = texture;
		this.hitbox = hitbox;
	}
	public Block(Vector3D pos, String texture) {
		this(pos, texture, new AABB(pos.x,pos.y,pos.z,pos.x+1,pos.y+1,pos.z+1));
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
			Block block = Block.blocks.get(id).clone(v);
			return block;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		return null;
	}
	
	public Block[] getSides() {
		return new Block[] {
			world.getBlock(pos.add(1, 0, 0)),
			world.getBlock(pos.add(-1, 0, 0)),
			world.getBlock(pos.add(0, 1, 0)),
			world.getBlock(pos.add(0, -1, 0)),
			world.getBlock(pos.add(0, 0, 1)),
			world.getBlock(pos.add(0, 0, -1))
		};
	}
	
	public static Block getAbstractBlock(int id) {
		return blocks.get(id);
	}
	
	public void onNeighUpdate() {
		
	}
	
	public boolean emitLight() {
		return false;
	}
	
	public void callChunkUpdate() {
		world.getColumn(pos.x, pos.z).chunks[(int) (Math.floor(pos.y)/16)].updateModel();
	}
	
	public Block under() throws Exception {
		return world.getBlock(new Vector3D(pos.x, pos.y-1, pos.z));
	}
	
	public boolean isRenderable() {
		return true;
	}
	
	public boolean isCollide() {
		return true;
	}
	
	public boolean isTransparent() {
		return false;
	}
	
	public boolean collide(AABB with) {
		return hitbox.collide(with);
	}
	
	public AABB getHitbox() {
		return hitbox;
	}
	
	public BlockType getType() {
		return BlockType.solid;
	}
	
	public void addModel(boolean py, boolean ny, boolean nx, boolean px, boolean nz, boolean pz, MBIM mbim) {
		
	}
	
	public BlockFace getFace() {
		return BlockFace.PX;
	}
	
	public Block clone(Vector3D pos) {
		System.out.println(this.getClass().getName()+" not overriding clone()");
		return null;
	}
	
	@Deprecated
	@Override
	public Block clone() {
		System.out.println("do not use");
		System.exit(0);
		return new Block(pos, texture, hitbox);
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

	public int getResistance() {
		return 5;
	}
	
	public JsonObject toJson() {
		return null;
	}
	
	public void fromJson(JsonObject data) {
		
	}
}
