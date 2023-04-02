package net.pzdcrp.wildland.data;

import java.util.Map;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

import net.pzdcrp.wildland.GameInstance;

public class MBIM {
	private Map<String, Pair> modelsById;
	
	public MBIM(Map<String, Pair> mp) {
		this.modelsById = mp;
	}
	
	public MeshPartBuilder obtain(String id, String texturename) {
		if (!modelsById.containsKey(id)) {
			ModelBuilder mb = new ModelBuilder();
			MeshPartBuilder mpb;
			mb.begin();
			modelsById.put(
					id,
					new Pair(
							mpb = mb.part(
								"cube", 
								GL20.GL_TRIANGLES, 
								VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates,
								new Material(
							    		TextureAttribute.createDiffuse(GameInstance.getTexture(texturename)),
						    			IntAttribute.createCullFace(GL20.GL_FRONT),
						    			new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
							    )
							),
							mb
					)
			);
			return mpb;
		} else {
			return modelsById.get(id).mpb;
		}
	}
}
