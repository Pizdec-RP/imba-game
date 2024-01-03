package net.pzdcrp.Aselia.world.elements.entities;

import java.util.List;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.google.gson.JsonObject;

import net.pzdcrp.Aselia.Hpb;
import net.pzdcrp.Aselia.data.AABB;
import net.pzdcrp.Aselia.data.DamageSource;
import net.pzdcrp.Aselia.data.EntityType;
import net.pzdcrp.Aselia.data.Settings;
import net.pzdcrp.Aselia.data.Vector3D;
import net.pzdcrp.Aselia.data.objects.ObjectData;
import net.pzdcrp.Aselia.data.objects.entityObjectData.ItemEntityData;
import net.pzdcrp.Aselia.player.Player;
import net.pzdcrp.Aselia.utils.GameU;
import net.pzdcrp.Aselia.utils.MathU;
import net.pzdcrp.Aselia.utils.VectorU;
import net.pzdcrp.Aselia.world.World;
import net.pzdcrp.Aselia.world.elements.blocks.Block;
import net.pzdcrp.Aselia.world.elements.inventory.items.Item;

public class ItemEntity extends Entity {
	private ModelInstance model;
	private int lifetime = 6000;
	private boolean despawn = false;
	private Item item;
	
	private float sins = 0f;

	/**
	 * For Column.fromJson only
	 * @param pos - position
	 */
	@Deprecated
	public ItemEntity(Vector3D pos, World world, int lid) {
		super(pos, new AABB(-0.15f, -0.15f, -0.15f, 0.15f, 0.15f, 0.15f), EntityType.item, world, lid);
	}

	public ItemEntity(Vector3D pos, Item item, World world, int lid) {
		super(pos, new AABB(-0.15f, -0.15f, -0.15f, 0.15f, 0.15f, 0.15f), EntityType.item, world, lid);
		if (item.getId() == 0) GameU.end("air can not be as item");
		this.item = item;
	}

	@Override
	public void render(float delta) {
		if (despawn) {
			super.despawn();
			return;
		}
		super.render(delta);
		if (model == null) {
			if (item.isModel()) {
				ModelInstance temp = Block.blockModels.get(Block.itemIdToBlockId(item.id));
				if (temp == null) {
					GameU.err("unknown block id "+Block.itemIdToBlockId(item.id)+" in item: "+toString()+" lid: "+localId);
					return;
				}
				this.model = temp.copy();
				model.userData = new Object[] {"item", 0f};
				updateLight();
			} else {
				Mesh mesh = new Mesh(
				    true,
				    4, 6,
				    VertexAttribute.Position(),
				    new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoord0")
				);
				float[] vertices = new float[] {
						-0.15f,    0.3f, 0f, 0, 0,
						0.15f, 0.3f, 0f,     1, 0,
						0.15f, 0,    0f,     1, 1,
						-0.15f,    0, 0f,    0, 1,
					};
				short[] indices = new short[] {
				    0, 1, 2,
				    2, 3, 0
				};
				mesh.setVertices(vertices);
				mesh.setIndices(indices);
				
				// Create a ModelBuilder
				ModelBuilder modelBuilder = new ModelBuilder();
				modelBuilder.begin();
				modelBuilder.part(
					"meshPart", 
					mesh, 
					GL20.GL_TRIANGLES, 
					new Material(
						TextureAttribute.createDiffuse(item.getTexture()),
						IntAttribute.createCullFace(GL20.GL_NONE),
						new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
					)
				);
				Model tmodel = modelBuilder.end();
				model = new ModelInstance(tmodel);
				model.userData = new Object[] {"item", 0f};
			}
		}
		if (VectorU.sqrt(pos, Hpb.world.player.pos) > Settings.maxItemRenderDistance) return;
		
		model.transform.setTranslation(
				Hpb.lerp(beforepos.x, pos.x),
				Hpb.lerp(beforepos.y, pos.y)-0.15f,
				Hpb.lerp(beforepos.z, pos.z));
		if (!item.isModel()) {
			model.transform.rotate(Vector3.Y, 1);
			model.transform.translate(0, (0.15f*MathU.sin(sins+=0.05f))+0.15f, 0);
		}
		Hpb.render(model);
	}

	@Override
	public boolean invincible() {
		return true;
	}

	public void updateLight() {
		float f = Hpb.world.getLight((int)Math.floor(pos.x), (int)Math.floor(pos.y), (int)Math.floor(pos.z));
		((Object[])model.userData)[1] = f;
	}

	@Override
	public boolean tick() {
		boolean continuee = super.tick();
		if (!continuee) return false;
		if (world.isLocal() && model != null) updateLight();
		if (!world.isLocal()) {
			lifetime--;
			if (lifetime <= 0 || despawn) {
				this.despawn();
				super.despawn();
				return false;
			}
			if (lifetime > 5990) return true;
			List<Player> nearPlayers = world.getPlayers(pos, 1.3f);

			@SuppressWarnings("unchecked")
			List<Player> nearestPlayers = (List<Player>) VectorU.sortNearest(nearPlayers, pos);

			for (Player player : nearestPlayers) {
				if (player.castedInv.canMerge(item)) {
					boolean val = player.castedInv.mergeFromItemEntity(item);
					if (val) {
						if (item.count == 0) {
							this.despawn();
							//GameU.log("item count == 0, despawning");
						}//else цикл продолжается дальше
					}//else цикл продолжается дальше
				}
			}
			return true;//не используется
		} else {
			return true;//не используется
		}
	}

	@Override
	public void despawn() {
		despawn = true;
		//GameU.log("despawning item");
		//super.despawn();
		//model.model.dispose();
	}

	@Override
	public void hit(DamageSource src, byte damage) {
		if (src == DamageSource.Explosion) this.despawn();
	}

	@Override
	public void getJson(JsonObject jen) {
		super.getJson(jen);
		jen.addProperty("lt", lifetime);
		jen.addProperty("item", this.item.toString());
	}

	@Override
	public void fromJson(JsonObject jen) {
		this.lifetime = jen.get("lt").getAsInt();
		this.item = Item.fromString(jen.get("item").getAsString());
	}

	@Override
	public int getType() {
		return 2;
	}

	@Override
	public Entity clone(Vector3D pos, World world, ObjectData data, int lid) {
		ItemEntityData ied = (ItemEntityData)data;
		Entity e = new ItemEntity(pos, Item.items.get(ied.item).clone(ied.count), world, lid);
		return e;
	}

	@Override
	public Entity cloneOnColumnLoad(Vector3D pos, World world, int lid) {
		Entity e = new ItemEntity(pos, world, lid);
		return e;
	}

	@Override
	public ObjectData consumeData() {
		ItemEntityData data = new ItemEntityData();
		data.item = item.id;
		data.count = item.count;
		return data;
	}

	@Override
	public String toString() {
		return "ItemEntity(item:"+item.toString()+")";
	}
}
