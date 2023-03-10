package net.pzdcrp.wildland.world.elements.blocks;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
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

public class Stone extends Block {
	static String tname = "stone";
	public Stone(Vector3D pos,BlockFace blockface) {
		super(pos, tname);
	}
	
	@Override
	public BlockType getType() {
		return BlockType.solid;
	}
	
	@Override
	public Block clone() {
		return new Stone(this.pos,null);
	}
}
