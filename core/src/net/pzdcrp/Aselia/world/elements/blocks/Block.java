package net.pzdcrp.Aselia.world.elements.blocks;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.google.gson.JsonObject;

import net.pzdcrp.Aselia.Hpb;
import net.pzdcrp.Aselia.data.AABB;
import net.pzdcrp.Aselia.data.AABBList;
import net.pzdcrp.Aselia.data.BlockFace;
import net.pzdcrp.Aselia.data.BlockModelBuilder;
import net.pzdcrp.Aselia.data.MBIM;
import net.pzdcrp.Aselia.data.MBIM.offset;
import net.pzdcrp.Aselia.data.Vector3D;
import net.pzdcrp.Aselia.extended.SexyMeshBuilder;
import net.pzdcrp.Aselia.player.Player;
import net.pzdcrp.Aselia.utils.GameU;
import net.pzdcrp.Aselia.utils.MathU;
import net.pzdcrp.Aselia.utils.ModelUtils;
import net.pzdcrp.Aselia.utils.VectorU;
import net.pzdcrp.Aselia.world.World;
import net.pzdcrp.Aselia.world.elements.Chunk;
import net.pzdcrp.Aselia.world.elements.entities.Entity;
import net.pzdcrp.Aselia.world.elements.entities.ItemEntity;
import net.pzdcrp.Aselia.world.elements.inventory.items.Item;
import net.pzdcrp.Aselia.world.elements.inventory.items.NoItem;

public class Block {
	//public static PlayerWorld world;// = GameInstance.world;
	public Vector3D pos;
	public static final Map<Integer, Block> blocks = new HashMap<>() {
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
			/*put(14, new TntCrate(new Vector3D()));
			put(15, new Water(new Vector3D(), 1));
			put(16, new Water(new Vector3D(), 2));
			put(17, new Water(new Vector3D(), 3));
			put(18, new Water(new Vector3D(), 4));
			put(19, new Water(new Vector3D(), 5));
			put(20, new Water(new Vector3D(), 6));
			put(21, new Water(new Vector3D(), 7));*/
			put(22, new OakLeaves(new Vector3D()));
			put(23, new Weed(new Vector3D()));
			put(24, new Crate(new Vector3D()));
		}};
	private static Map<Integer, Integer> BlockidToItemid = new ConcurrentHashMap<>() {
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
			//put(22, 8); weed
			//put(23, 9); weed
			put(24, 10);
		}};
	public static Map<Integer, ModelInstance> blockModels = new ConcurrentHashMap<>();
	public enum BlockType {
		air, solid, transparent, noncollideabe;
	}
	public String texture;
	protected AABBList hitbox;
	private int id = -1;

	public static int count = 0;

	/*public Block(Vector3D pos, String texture, AABB hitbox) {
		this.pos = pos;//.VecToInt();
		this.texture = texture;
		this.hitbox = hitbox;
		count++;
	}*/
	public Block(Vector3D pos, String texture) {
		this.pos = pos;//.VecToInt();
		this.texture = texture;
		count++;
	}

	public static Block getRaw(int id) {
		return blocks.get(id);
	}

	public static int idByBlock(Block block) {
		for (Entry<Integer, Block> entry : blocks.entrySet()) {
			if (entry.getValue().equals(block)) return entry.getKey();
		}
		GameU.end("unregistered block: "+block.toString());
		return 0;
	}

	public static Block blockById(int id, Vector3D v) {
		Block block = Block.blocks.get(id).clone(v);
		return block;
	}

	public Block[] getSides(World world) {
	    Set<Vector3D> set = new HashSet<>(Arrays.asList(
	            pos.add(1, 0, 0),
	            pos.add(-1, 0, 0),
	            pos.add(0, 1, 0),
	            pos.add(0, -1, 0),
	            pos.add(0, 0, 1),
	            pos.add(0, 0, -1)
	    ));

	    set.removeIf(pos -> !world.containColumn(VectorU.posToColumn(pos)));

	    Set<Block> blockSet = new HashSet<>();
	    for (Vector3D pos : set) {
	        blockSet.add(world.getBlock(pos));
	    }

	    return blockSet.toArray(new Block[0]);
	}


	public static Block getAbstractBlock(int id) {
		return blocks.get(id);
	}

	public boolean clickable() {
		return false;
	}

	public void onNeighUpdate(World world) {

	}

	public boolean emitLight() {
		return false;
	}

	public void callChunkUpdate(World world) {
		world.getColumn(pos.x, pos.z).chunks[(int) (Math.floor(pos.y)/16)].updateModel();
	}

	public Chunk getChunk(World world) {
		return world.getColumn(pos.x, pos.z).chunks[(int) (Math.floor(pos.y)/16)];
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

	public AABBList getHitbox() {
		return hitbox;
	}

	public BlockType getType() {
		return BlockType.solid;
	}

	public void addModel(boolean py, boolean ny, boolean nx, boolean px, boolean nz, boolean pz, BlockModelBuilder mbim) {

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
		GameU.end("не юзай этот мтеод");
		return new Block(pos, texture);
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

	public void onClick(Player actor) {
		GameU.err("click called on non clickable block");
		GameU.tracer();
		return;
	}

	public boolean tickable() {
		return false;
	}

	public static int itemIdToBlockId(int itemid) {
		for (Entry<Integer, Integer> entry : BlockidToItemid.entrySet()) {
			if (entry.getValue() == itemid) return entry.getKey();
		}
		GameU.end("unknown id: "+itemid);
		return 0;
	}

	public static Block blockByItem(Item item) {
		return blocks.get(itemIdToBlockId(item.getId()));
	}

	public static Item itemByBlockId(int id) {
		Integer itemid = BlockidToItemid.get(id);
		if (itemid == null) return null;
		return Item.items.get(itemid);
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
		if (stage > 9) stage = 9;
		SexyMeshBuilder a = mbim.obtain(pos, true);
		ModelUtils.setTransform(pos);
		Hpb.mutex.hookuvr(a, "ds"+stage, 0, 0, 1, 1);
		mbim.setCuroffset(offset.py);
    	ModelUtils.buildTopX(a);//PY
    	mbim.setCuroffset(offset.nx);
	    ModelUtils.buildLeftPY(a);//NX
	    mbim.setCuroffset(offset.px);
	    ModelUtils.buildRightPY(a);//PX
	    mbim.setCuroffset(offset.nz);
	    ModelUtils.buildFrontY(a);//NZ
	    mbim.setCuroffset(offset.pz);
	    ModelUtils.buildBackY(a);//PZ
	    mbim.setCuroffset(offset.ny);
	    ModelUtils.buildBottomX(a);//NY
	}

	public int getId() {
		if (id == -1) {
			this.id = idByBlock(this);
		}
		return id;
	}

	/**
	 * Server side
	 * @param world
	 */
	public void onBreak(World world) {
		if (world.isLocal()) GameU.end("onBreak не должен вызываться в клиенте");
		Item blockItem = Block.itemByBlockId(getId());
		System.out.println("onb "+isRenderable()+" "+blockItem != null+" "+!(blockItem instanceof NoItem));
		if (isRenderable() && blockItem != null && !(blockItem instanceof NoItem)) {
			ItemEntity e;
			Item i = Block.itemByBlockId(this.getId());
			i.count = 1;
			//if (getId() == 0) GameU.end("pizdec");
			world.spawnEntity(e = new ItemEntity(pos.add(0.5d), i, world, Entity.genLocalId()));
			e.vel.y = 0.01;
			e.vel.x = MathU.rndd(-0.1, 0.1);
			e.vel.z = MathU.rndd(-0.1, 0.1);
		}
	}
}
