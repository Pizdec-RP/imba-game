package net.pzdcrp.Hyperborea.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.extended.SexyMeshBuilder;
import net.pzdcrp.Hyperborea.extended.SexyModelBuilder;
import net.pzdcrp.Hyperborea.utils.ModelUtils;
import net.pzdcrp.Hyperborea.world.elements.Chunk;

public class MBIM {
	private Map<String, Pair> modelsById;
	int i = 0;
	private int x,y,z;//kорды блока в чанке т.е. 0-15(1-16)
	public Chunk chunk;
	public List<Integer> lightarray = new ArrayList<>();
	
	public MBIM(Map<String, Pair> mp, Chunk chunk) {
		this.modelsById = mp;
		this.chunk = chunk;
	}
	
	public SexyMeshBuilder obtain(String id, String texturename, Vector3D blockpos) {
		this.x = (int)blockpos.x&15;
		this.y = (int)blockpos.y&15;
		this.z = (int)blockpos.z&15;
		if (!modelsById.containsKey(id)) {
			SexyModelBuilder mb = new SexyModelBuilder(this);
			SexyMeshBuilder mpb;
			mb.begin();
			modelsById.put(
					id,
					new Pair(
							mpb = (SexyMeshBuilder) mb.part(
								"cube", 
								GL20.GL_TRIANGLES, 
								VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates,
								new Material(
							    		TextureAttribute.createDiffuse(Hpb.getTexture(texturename)),
						    			IntAttribute.createCullFace(GL20.GL_FRONT),
						    			new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
							    )
							),
							mb
					)
			);
			return mpb;
		} else {
			i++;
			Pair mod = modelsById.get(id);
			if (++mod.calls > 10000) {//TODO переделать чекер потомучто уже есть способ достать количество вершин. (максимум 16384)
				SexyModelBuilder mb = new SexyModelBuilder(this);
				SexyMeshBuilder mpb;
				mb.begin();
				modelsById.put(
						id+i,
						new Pair(
								mpb = (SexyMeshBuilder) mb.part(
									"cube", 
									GL20.GL_TRIANGLES, 
									VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates,
									new Material(
								    		TextureAttribute.createDiffuse(Hpb.getTexture(texturename)),
							    			IntAttribute.createCullFace(GL20.GL_FRONT),
							    			new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
								    )
								),
								mb
						)
				);
				return mpb;
			}
			return mod.mpb;
		}
	}

	public int[] getLightArray() {
		return this.lightarray.stream().mapToInt(Integer::intValue).toArray();
	}

	public int getCurLight() {
		//System.out.println(chunk.light[x][y][z]);
		return chunk.light[x][y][z];
	}
}
