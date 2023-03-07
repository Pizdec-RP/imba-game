package net.pzdcrp.wildland.world.elements.blocks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;

import net.pzdcrp.wildland.GameInstance;
import net.pzdcrp.wildland.data.Vector3D;
import net.pzdcrp.wildland.world.elements.blocks.Block.BlockType;

public class Dirt extends Block {
	static String tname = "dirt";
	public Dirt(Vector3D pos) {
		super(pos,1d,1d,1d, tname);
		//System.out.println("crtd");
	}
	
	public static int id() {
		return 1;
	}
	
	@Override
	public BlockType getType() {
		return BlockType.solid;
	}
}	
