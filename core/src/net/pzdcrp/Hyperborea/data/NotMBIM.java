package net.pzdcrp.Hyperborea.data;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.MBIM.offset;
import net.pzdcrp.Hyperborea.extended.SexyMeshBuilder;
import net.pzdcrp.Hyperborea.extended.SexyModelBuilder;
import net.pzdcrp.Hyperborea.utils.GameU;
import net.pzdcrp.Hyperborea.world.elements.Chunk;

public class NotMBIM extends BlockModelBuilder {
	public Pair all;
	private int mask;
	public NotMBIM() {
		mask = 
		VertexAttributes.Usage.Position
		| VertexAttributes.Usage.Normal
		| VertexAttributes.Usage.TextureCoordinates;
		
		SexyModelBuilder mb = new SexyModelBuilder(this, true);
		mb.begin();

		mb = new SexyModelBuilder(this, true);
		mb.begin();
		all = new Pair(
			mb.part(
				"cube",
				GL20.GL_TRIANGLES, 
				mask,
				new Material(
		    		TextureAttribute.createDiffuse(Hpb.mutex.getComplex()),
	    			IntAttribute.createCullFace(GL20.GL_NONE),
	    			new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
			    )
			),
			mb
		);
		
	}
	
	@Override
	public Chunk getChunk() {
		return null;
	}
	
	@Override
	public SexyMeshBuilder obtain(Vector3D blockpos, boolean transparent) {
		return all.mpb;
	}
	
	@Override
	public void setCuroffset(offset ofs) {
		//pass
	}
	
	@Override
	public void rebuildBuilders() {
		if (all.mb.model != null) {
			all.mb.end();
		}
		all.mb.begin();
		all.mpb = all.mb.part(
				"cube",
				GL20.GL_TRIANGLES, 
				mask,
				new Material(
		    		TextureAttribute.createDiffuse(Hpb.mutex.getComplex()),
	    			IntAttribute.createCullFace(GL20.GL_NONE),
	    			new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
			    )
			);
		all.calls = 1;
	}
	
	@Override
	public void clear() {
		rebuildBuilders();
	}
	
	public ModelInstance end() {
		return new ModelInstance(all.mb.end());
	}
}
