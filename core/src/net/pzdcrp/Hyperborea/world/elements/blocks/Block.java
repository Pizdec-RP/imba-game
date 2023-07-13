package net.pzdcrp.Hyperborea.world.elements.blocks;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.google.gson.JsonObject;

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.AABB;
import net.pzdcrp.Hyperborea.data.BlockFace;
import net.pzdcrp.Hyperborea.data.DM;
import net.pzdcrp.Hyperborea.data.MBIM;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.data.MBIM.offset;
import net.pzdcrp.Hyperborea.extended.SexyMeshBuilder;
import net.pzdcrp.Hyperborea.utils.ModelUtils;
import net.pzdcrp.Hyperborea.utils.ThreadU;
import net.pzdcrp.Hyperborea.utils.VectorU;
import net.pzdcrp.Hyperborea.world.World;
import net.pzdcrp.Hyperborea.world.elements.Chunk;
import net.pzdcrp.Hyperborea.world.elements.entities.Entity;
import net.pzdcrp.Hyperborea.world.elements.entities.ItemEntity;
import net.pzdcrp.Hyperborea.world.elements.inventory.items.Item;
import net.pzdcrp.Hyperborea.world.elements.inventory.items.NoItem;

public class Block {
	public static World world;// = GameInstance.world;
	public Vector3D pos;
	public static final Map<Integer, Block> blocks = new HashMap<Integer, Block>() {
		private static final long serialVersionUID = 3707568282902670945L;
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
			put(22, new OakLeaves(new Vector3D()));
			put(23, new Weed(new Vector3D()));
		}};;
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
			put(23, 9);
		}};
	public static Map<Integer, ModelInstance> blockModels = new HashMap<>();
	public enum BlockType {
		air, solid, transparent, noncollideabe;
	}
	public String texture;
	protected AABB hitbox;
	private int id = -1;
	
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
		ThreadU.end("unregistered block: "+block.toString());
		return 0;
	}
	
	public static Block blockById(int id, Vector3D v) {
		Block block = Block.blocks.get(id).clone(v);
		return block;
	}
	
	public Block[] getSides() {
	    Set<Vector3D> set = new HashSet<>(Arrays.asList(
	            pos.add(1, 0, 0),
	            pos.add(-1, 0, 0),
	            pos.add(0, 1, 0),
	            pos.add(0, -1, 0),
	            pos.add(0, 0, 1),
	            pos.add(0, 0, -1)
	    ));

	    set.removeIf(pos -> !world.loadedColumns.containsKey(VectorU.posToColumn(pos)));

	    Set<Block> blockSet = new HashSet<>();
	    for (Vector3D pos : set) {
	        blockSet.add(world.getBlock(pos));
	    }

	    return blockSet.toArray(new Block[0]);
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
	
	public Chunk getChunk() {
		return world.getColumn(pos.x, pos.z).chunks[(int) (Math.floor(pos.y)/16)];
	}
	
	public Block under() {
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
		ThreadU.end("не юзай этот мтеод");
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
		ThreadU.end("unknown id: "+itemid);
		return 0;
	}

	public static Block blockByItem(Item item) {
		return blocks.get(itemIdToBlockId(item.getId()));
	}
	
	public static Item itemByBlockId(int id) {
		Integer itemid = BlockidToItemid.get(id);
		if (itemid == null) return null;
		for (Item i : Item.items) {
			if (i.id == itemid) return i;
		}
		return null;
	}

	public void tick() {
		
	}

	public float getResistance() {
		return 5;
	}
	
	public JsonObject toJson() {
		return null;
	}
	
	public void fromJson(JsonObject data) {
		
	}
	
	public static void bbmodel(MBIM mbim, Vector3D pos, int stage) {
		SexyMeshBuilder a = mbim.obtain(pos, true);
		ModelUtils.setTransform(pos);
		Hpb.mutex.hookuvr(a, "ds"+stage, 0, 0, 1, 1);
		mbim.curoffset = offset.py;
    	ModelUtils.buildTopX(a);//PY
    	mbim.curoffset = offset.nx;
	    ModelUtils.buildLeftPY(a);//NX
	    mbim.curoffset = offset.px;
	    ModelUtils.buildRightPY(a);//PX
	    mbim.curoffset = offset.nz;
	    ModelUtils.buildFrontY(a);//NZ
	    mbim.curoffset = offset.pz;
	    ModelUtils.buildBackY(a);//PZ
	    mbim.curoffset = offset.ny;
	    ModelUtils.buildBottomX(a);//NY
	}
	
	public int getId() {
		if (id == -1) {
			this.id = idByBlock(this);
		}
		return id;
	}
	public void onBreak() {
		Item blockItem = Block.itemByBlockId(getId());
		System.out.println("onb "+isRenderable()+" "+blockItem != null+" "+!(blockItem instanceof NoItem));
		if (isRenderable() && blockItem != null && !(blockItem instanceof NoItem)) {
			world.spawnEntity(new ItemEntity(pos.add(0.5d), this));
		}
	}
}
