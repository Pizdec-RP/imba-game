package net.pzdcrp.game.world.elements.blocks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

import net.pzdcrp.game.GameInstance;
import net.pzdcrp.game.data.AABB;
import net.pzdcrp.game.data.Vector3D;
import net.pzdcrp.game.world.World;

public class Block {
	
	public Vector3D pos;
	public double xsize, ysize, zsize;
	public static Map<Integer, Class<? extends Block>> blocks = new ConcurrentHashMap<Integer, Class<? extends Block>>( ) {
	private static final long serialVersionUID = 3707964282902670945L;
	{
		put(0, Air.class);
		put(1, Dirt.class);
		put(2, Stone.class);
		put(3, Glass.class);
		put(4, RedSand.class);
		put(5, Voed.class);
		put(6, Grass.class);
	}};
	public enum BlockType {
		air, Void, solid, sandy, glass, nonfull;
	}
	public String texture;
	
	public Block(Vector3D pos, double xsize, double ysize, double zsize, String texture) {
		this.pos = pos;
		this.xsize = xsize;
		this.ysize = ysize;
		this.zsize = zsize;
		this.texture = texture;
		//ModelBuilder mb = new ModelBuilder();
		//TextureRegion tr = new TextureRegion(new Texture(Gdx.files.internal("dirt.png")),0,0,16,16);
		/*if (texture != null) {
			Material material = new Material(TextureAttribute.createDiffuse(texture));
			ModelBuilder modelBuilder = new ModelBuilder();
			modelBuilder.begin();
			MeshPartBuilder meshBuilder = modelBuilder.part("box", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates, material);
			
			meshBuilder.box((float)xsize, (float)ysize, (float)zsize);
			
			Model model = modelBuilder.end();
			Matrix4 transform = new Matrix4();
			transform.translate((float)(pos.x+xsize/2),(float)pos.y,(float)(pos.z+zsize/2));

			for (Mesh mesh : model.meshes) {
			    mesh.transform(transform);
			}
			this.model = new ModelInstance(model);
		}*/
	}
	
	public static Block blockById(int id, Vector3D v) {
		try {
			return (Block)Block.blocks.get(id).getConstructor(Vector3D.class).newInstance(v);
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
		return new AABB(pos.x,pos.y,pos.z, pos.x+xsize,pos.y+ysize,pos.z+zsize).collide(with);
	}
	
	public AABB getHitbox() {
		return new AABB(pos.x,pos.y,pos.z, pos.x+xsize,pos.y+ysize,pos.z+zsize);
	}
	
	public BlockType getType() {
		return BlockType.solid;
	}
}
