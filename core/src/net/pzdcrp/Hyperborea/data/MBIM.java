package net.pzdcrp.Hyperborea.data;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioSystem;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.extended.SexyMeshBuilder;
import net.pzdcrp.Hyperborea.extended.SexyModelBuilder;
import net.pzdcrp.Hyperborea.utils.ModelUtils;
import net.pzdcrp.Hyperborea.world.elements.Chunk;

public class MBIM {
	//private List<Pair> models;
	int i = 0;
	private int x,y,z;//kорды блока в чанке т.е. 0-15(1-16)
	public Chunk chunk;
	public List<Integer> lightarray = new ArrayList<>();
	public offset curoffset = offset.no;
	public Pair p;
	private VertexAttribute lightdata;
	
	public enum offset {
		py,ny,px,nx,pz,nz,no;
	}
	
	@SuppressWarnings("serial")
	public MBIM(Chunk chunk) {
		lightdata = new VertexAttribute(512, 1, "lightdata");
		VertexAttributes atrs = createMixedVertexAttribute(VertexAttributes.Usage.Position
		| VertexAttributes.Usage.Normal
		| VertexAttributes.Usage.TextureCoordinates,
		new ArrayList<VertexAttribute>() {{add(lightdata);}});
		
		this.chunk = chunk;
		SexyModelBuilder mb = new SexyModelBuilder(this);
		mb.begin();
		p = new Pair(
			(SexyMeshBuilder) mb.part(
				"cube", 
				GL20.GL_TRIANGLES, 
				atrs.getMask(),
				new Material(
		    		TextureAttribute.createDiffuse(Hpb.mutex.comp),
	    			IntAttribute.createCullFace(GL20.GL_FRONT),
	    			new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
			    )
			),
			mb
		);
	}
	
	private VertexAttributes createMixedVertexAttribute(long defaultAtributes, List<VertexAttribute> customAttributes){
	    VertexAttributes defaultAttributes = MeshBuilder.createAttributes(defaultAtributes);
	    List<VertexAttribute> attributeList = new ArrayList<VertexAttribute>();
	    for(VertexAttribute attribute: defaultAttributes){
	        attributeList.add(attribute);
	    }
	    attributeList.addAll(customAttributes);
	    VertexAttribute[] typeArray = new VertexAttribute[0];
	    VertexAttributes mixedVertexAttributes = new VertexAttributes(attributeList.toArray(typeArray));
	    //System.out.println("vertsize: "+mixedVertexAttributes.vertexSize/4);
	    return mixedVertexAttributes;
	}
	
	public SexyMeshBuilder obtain(Vector3D blockpos) {
		this.x = (int)blockpos.x&15;
		this.y = (int)blockpos.y&15;
		this.z = (int)blockpos.z&15;
		
		return p.mpb;
	}

	public int[] getLightArray() {
		return this.lightarray.stream().mapToInt(Integer::intValue).toArray();
	}

	public int getCurLight() {
		try {
			//return chunk.rawGetLight(x, y, z);
			switch (curoffset) {
			case px:
				return chunk.rawGetLight(x+1, y, z);
			case nx:
				return chunk.rawGetLight(x-1, y, z);
			case py:
				return chunk.rawGetLight(x, y+1, z);
			case ny:
				return chunk.rawGetLight(x, y-1, z);
			case pz:
				return chunk.rawGetLight(x, y, z+1);
			case nz:
				return chunk.rawGetLight(x, y, z-1);
			case no:
				return chunk.rawGetLight(x, y, z);
			}
			throw new Exception("если ты видишь эту ошибку то ты гей");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
			return 0;
		}
	}
	
	public ModelInstance end() {
	    Model model = p.mb.end();
	    Mesh mesh = model.meshes.get(0);
	    int numVertices = mesh.getNumVertices();
	    if (numVertices > 0) {
		    int vertexSize = mesh.getVertexAttributes().vertexSize / 4;
		    float[] vertices = new float[numVertices * vertexSize];
		    int[] nums = getLightArray();
		    
		    if (numVertices != nums.length) {
		        System.err.println("Mismatch between numVertices and nums length");
		        System.exit(0);
		    }
		    
		    getVertices(mesh, 0, vertices.length, vertices, 0);
		    
		    int attributeOffset = 8; // Замените на соответствующее значение смещения атрибута
		    
		    for (int i = attributeOffset; i < vertices.length; i += vertexSize) {
		        int numsIndex = (i - attributeOffset) / vertexSize;
		        vertices[i] = nums[numsIndex];
		        
		    }
		    
		    mesh.updateVertices(0, vertices);
	    }
	    return new ModelInstance(model);
	}
	
	public float[] getVertices(Mesh mesh, int srcOffset, int count, float[] vertices, int destOffset) {
		// TODO: Perhaps this method should be vertexSize aware??
		final int max = mesh.getNumVertices() * mesh.getVertexSize() / 4;
		if (count == -1) {
			count = max - srcOffset;
			if (count > vertices.length - destOffset) count = vertices.length - destOffset;
		}
		if (srcOffset < 0 || count <= 0 || (srcOffset + count) > max || destOffset < 0 || destOffset >= vertices.length)
			throw new IndexOutOfBoundsException((srcOffset < 0) +" "+ (count <= 0) +" "+ ((srcOffset + count) > max) +" "+ (destOffset < 0) +" "+ (destOffset >= vertices.length));
		if ((vertices.length - destOffset) < count) throw new IllegalArgumentException(
			"not enough room in vertices array, has " + vertices.length + " floats, needs " + count);
		int pos = mesh.getVerticesBuffer().position();
		((Buffer)mesh.getVerticesBuffer()).position(srcOffset);
		mesh.getVerticesBuffer().get(vertices, destOffset, count);
		((Buffer)mesh.getVerticesBuffer()).position(pos);
		return vertices;
	}
}
