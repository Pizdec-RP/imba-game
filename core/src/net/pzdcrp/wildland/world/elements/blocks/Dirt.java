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
import net.pzdcrp.wildland.data.BlockFace;
import net.pzdcrp.wildland.data.Vector3D;
import net.pzdcrp.wildland.world.elements.blocks.Block.BlockType;

public class Dirt extends Block {
	static String tname = "dirt";
	public Dirt(Vector3D pos,BlockFace blockface) {
		super(pos,tname);
	}
	
	
	@Override
	public BlockType getType() {
		return BlockType.solid;
	}
	
	@Override
	public Block clone() {
		return new Dirt(this.pos,null);
	}
}	
