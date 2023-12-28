package net.pzdcrp.Aselia.data;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.badlogic.gdx.math.Vector3;

import net.pzdcrp.Aselia.Hpb;
import net.pzdcrp.Aselia.extended.SexyMeshBuilder;
import net.pzdcrp.Aselia.extended.SexyModelBuilder;
import net.pzdcrp.Aselia.utils.GameU;
import net.pzdcrp.Aselia.world.elements.Chunk;

public class MBIM extends BlockModelBuilder {
	//private List<Pair> models;
	int i = 0;
	private int x,y,z;//kорды блока в чанке т.е. 0-15(1-16)
	public Chunk chunk;
	public List<Integer> Slightarray = new ArrayList<>();
	public List<Integer> Tlightarray = new ArrayList<>();
	public offset curoffset = offset.no;
	public Pair p;
	public Pair t;
	private VertexAttribute lightdata;
	public ModelInstance transparentmodel;
	private long mask;

	public enum offset {
		py,ny,px,nx,pz,nz,no;
	}

	public MBIM(Chunk chunk) {
		this.chunk = chunk;
		ArrayList<VertexAttribute> atr = new ArrayList<>();
		if (chunk != null) {
			lightdata = new VertexAttribute(512, 1, "lightdata");
			atr.add(lightdata);
		}
		VertexAttributes atrs = createMixedVertexAttribute(
		VertexAttributes.Usage.Position
		| VertexAttributes.Usage.Normal
		| VertexAttributes.Usage.TextureCoordinates,
		atr);
		this.mask = atrs.getMask();

		SexyModelBuilder mb = new SexyModelBuilder(this, false);
		mb.begin();
		p = new Pair(
			mb.part(
				"cube",
				GL20.GL_TRIANGLES,
				mask,
				new Material(
		    		TextureAttribute.createDiffuse(Hpb.mutex.getComplex()),
	    			IntAttribute.createCullFace(chunk==null?GL20.GL_NONE:GL20.GL_FRONT)
			    )
			),
			mb
		);
		mb = new SexyModelBuilder(this, true);
		mb.begin();
		t  = new Pair(
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
	public void rebuildBuilders() {
		if (p.mb.model != null) {
			p.mb.end();
		}
		p.mb.begin();
		p.mpb = p.mb.part(
				"cube",
				GL20.GL_TRIANGLES,
				mask,
				new Material(
		    		TextureAttribute.createDiffuse(Hpb.mutex.getComplex()),
	    			IntAttribute.createCullFace(chunk==null?GL20.GL_NONE:GL20.GL_FRONT)
			    )
			);
		p.calls = 1;
		if (t.mb.model != null) {
			t.mb.end();
		}
		t.mb.begin();
		t.mpb = t.mb.part(
				"cube",
				GL20.GL_TRIANGLES,
				mask,
				new Material(
		    		TextureAttribute.createDiffuse(Hpb.mutex.getComplex()),
	    			IntAttribute.createCullFace(GL20.GL_NONE),
	    			new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
			    )
			);
		t.calls = 1;
	}

	private static VertexAttributes createMixedVertexAttribute(long defaultAtributes, List<VertexAttribute> customAttributes){
	    VertexAttributes defaultAttributes = MeshBuilder.createAttributes(defaultAtributes);
	    List<VertexAttribute> attributeList = new ArrayList<>();
	    for(VertexAttribute attribute: defaultAttributes){
	        attributeList.add(attribute);
	    }
	    attributeList.addAll(customAttributes);
	    VertexAttribute[] typeArray = new VertexAttribute[0];
	    VertexAttributes mixedVertexAttributes = new VertexAttributes(attributeList.toArray(typeArray));
	    return mixedVertexAttributes;
	}
	@Override
	public SexyMeshBuilder obtain(Vector3D blockpos, boolean transparent) {
		this.x = (int)blockpos.x&15;
		this.y = (int)blockpos.y&15;
		this.z = (int)blockpos.z&15;
		if (transparent) return this.t.mpb;
		else return p.mpb;
	}
	@Override
	public void clear() {
		i = 0;
		Slightarray.clear();
		Tlightarray.clear();
		rebuildBuilders();
		transparentmodel = null;
	}
	@Override
	public List<Integer> getTlightarray() {
		return Tlightarray;
	}
	@Override
	public List<Integer> getSlightarray() {
		return Slightarray;
	}

	@Override
	public int[] getSLightArray() {
		return this.Slightarray.stream().mapToInt(Integer::intValue).toArray();
	}
	@Override
	public int[] getTLightArray() {
		return this.Tlightarray.stream().mapToInt(Integer::intValue).toArray();
	}
	@Override
	public Chunk getChunk() {
		return this.chunk;
	}
	@Override
	public int getCurLight() {
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
		GameU.end("unknown face "+curoffset.toString());
		return 0;
	}
	@Override
	public void setCuroffset(offset ofs) {
		curoffset = ofs;
	}

	public ModelInstance endSolid() {
	    Model model = p.mb.end();
	    if (chunk != null) {
		    Mesh mesh = model.meshes.get(0);
		    int numVertices = mesh.getNumVertices();
		    if (numVertices > 0) {
			    int vertexSize = mesh.getVertexAttributes().vertexSize / 4;
			    float[] vertices = new float[numVertices * vertexSize];
			    int[] nums = getSLightArray();

			    if (numVertices != nums.length) {
			    	GameU.end("S Mismatch between numVertices and nums length");
			    }

			    getVertices(mesh, 0, vertices.length, vertices, 0);

			    int attributeOffset = 8;

			    for (int i = attributeOffset; i < vertices.length; i += vertexSize) {
			        int numsIndex = (i - attributeOffset) / vertexSize;
			        vertices[i] = nums[numsIndex];

			    }

			    mesh.updateVertices(0, vertices);
		    }
	    }
	    return new ModelInstance(model);
	}

	public ModelInstance endTransparent() {
	    Model model = t.mb.end();
	    if (chunk != null) {
		    Mesh mesh = model.meshes.get(0);
		    int numVertices = mesh.getNumVertices();
		    if (numVertices > 0) {
			    int vertexSize = mesh.getVertexAttributes().vertexSize / 4;
			    float[] vertices = new float[numVertices * vertexSize];
			    int[] nums = getTLightArray();

			    if (numVertices != nums.length) {
			    	GameU.end("T Mismatch between numVertices and nums length");
			    }

			    getVertices(mesh, 0, vertices.length, vertices, 0);

			    int attributeOffset = 8;

			    for (int i = attributeOffset; i < vertices.length; i += vertexSize) {
			        int numsIndex = (i - attributeOffset) / vertexSize;
			        vertices[i] = nums[numsIndex];

			    }

			    mesh.updateVertices(0, vertices);
		    }
	    }
	    return transparentmodel = new ModelInstance(model);
	}

	public void sortTransparent(Vector3 campos) {
	    Mesh mesh = transparentmodel.model.meshes.get(0);
	    int numVertices = mesh.getNumVertices();
	    if (numVertices > 0) {
	        int vertexSize = mesh.getVertexAttributes().vertexSize / 4;
	        float[] nsvertices = new float[numVertices * vertexSize];

	        getVertices(mesh, 0, nsvertices.length, nsvertices, 0);

	        Map<Vector3, List<float[]>> rawNotSorted = new HashMap<>();
	        Vector3[] tempvs = new Vector3[4];
	        float[][] tempf = new float[4][vertexSize];
	        int paravertex = 0, ivertex = 0;

	        for (float vert : nsvertices) {
	            tempf[paravertex][ivertex] = vert;

	            if (ivertex == 0) {
	                tempvs[paravertex] = new Vector3(vert, 0, 0);
	            } else if (ivertex == 1) {
	                tempvs[paravertex].y = vert;
	            } else if (ivertex == 2) {
	                tempvs[paravertex].z = vert;
	            }

	            ivertex++;
	            if (ivertex == vertexSize) {
	                ivertex = 0;
	                paravertex++;

	                if (paravertex == 4) {
	                    paravertex = 0;
	                    Vector3 tempv = averageVector(tempvs);
	                    List<float[]> vertexList = rawNotSorted.get(tempv);
	                    if (vertexList == null) {
	                        vertexList = new ArrayList<>();
	                        rawNotSorted.put(tempv, vertexList);
	                    }
	                    vertexList.add(tempf[0]);
	                    vertexList.add(tempf[1]);
	                    vertexList.add(tempf[2]);
	                    vertexList.add(tempf[3]);
	                    tempvs = new Vector3[4];
	                    tempf = new float[4][vertexSize];
	                }
	            }
	        }

	        float[] sortedVerticesArray = new float[numVertices * vertexSize];
	        int i = 0;

	        while (!rawNotSorted.isEmpty()) {
	            Vector3 farest = null;

	            for (Vector3 vecin : rawNotSorted.keySet()) {
	                if (farest == null || vecin.dst(campos) > farest.dst(campos)) {
	                    farest = vecin;
	                }
	            }

	            List<float[]> farestd = rawNotSorted.remove(farest);

	            for (float[] fv : farestd) {
	                for (float f : fv) {
	                    sortedVerticesArray[i] = f;
	                    i++;
	                }
	            }
	        }

	        mesh.setVertices(sortedVerticesArray);
	    }
	}

	/*старый вариант
	public void sortTransparent() {
    Mesh mesh = transparentmodel.model.meshes.get(0);
    int numVertices = mesh.getNumVertices();
    if (numVertices > 0) {
        int vertexSize = mesh.getVertexAttributes().vertexSize / 4;
        float[] nsvertices = new float[numVertices * vertexSize];

        getVertices(mesh, 0, nsvertices.length, nsvertices, 0);

        Map<Vector3, List<float[]>> rawNotSorted = new HashMap<>();
        Vector3[] tempvs = new Vector3[4];
        float[][] tempf = new float[4][vertexSize];
        int paravertex = 0, ivertex = 0;

        for (int i = 0; i < nsvertices.length; i++) {
            float vert = nsvertices[i];
            tempf[paravertex][ivertex] = vert;

            if (ivertex == 0) {
                tempvs[paravertex] = new Vector3(vert, 0, 0);
            } else if (ivertex == 1) {
                tempvs[paravertex].y = vert;
            } else if (ivertex == 2) {
                tempvs[paravertex].z = vert;
            }

            ivertex++;
            if (ivertex == vertexSize) {
                ivertex = 0;
                paravertex++;

                if (paravertex == 4) {
                    paravertex = 0;
                    Vector3 tempv = averageVector(tempvs);
                    List<float[]> vertexList = rawNotSorted.get(tempv);
                    if (vertexList == null) {
                        vertexList = new ArrayList<>();
                        rawNotSorted.put(tempv, vertexList);
                    }
                    vertexList.add(tempf[0]);
                    vertexList.add(tempf[1]);
                    vertexList.add(tempf[2]);
                    vertexList.add(tempf[3]);
                    tempvs = new Vector3[4];
                    tempf = new float[4][vertexSize];
                }
            }
        }

        Vector3 campos = new Vector3(Hpb.world.player.cam.cam.position);

        float[] sortedVerticesArray = new float[numVertices * vertexSize];
        int i = 0;

        while (!rawNotSorted.isEmpty()) {
            Vector3 farest = null;

            for (Vector3 vecin : rawNotSorted.keySet()) {
                if (farest == null || vecin.dst(campos) > farest.dst(campos)) {
                    farest = vecin;
                }
            }

            List<float[]> farestd = rawNotSorted.remove(farest);

            for (float[] fv : farestd) {
                for (float f : fv) {
                    sortedVerticesArray[i] = f;
                    i++;
                }
            }
        }

        mesh.setVertices(sortedVerticesArray);
    }
}

	 */

	/*int verts = 0;
    System.out.println("before-----------");
    for (float vert : nsvertices) {
    	if (i == 0) System.out.print(verts+": ");
        System.out.print(vert + " ");
        i++;
        if (i == vertexSize) {
            System.out.println();
            i = 0;
            verts++;
        }
    }*/

	/*for (Entry<Vector3, float[][]> entry : rawNotSorted.entrySet()) {
	System.out.println("av: "+entry.getKey().toString());
	for (float[] tf : entry.getValue()) {
		System.out.print("  ");
		for (float tff : tf) {
			System.out.print(tff+" ");
		}
		System.out.println();
	}
	System.out.println("----------------------");
}*/

	/*i = 0;
    verts = 0;
    System.out.println("AFTER-----------");
    for (float vert : nsvertices) {
    	if (i == 0) System.out.print(verts+": ");
        System.out.print(vert + " ");
        i++;
        if (i == vertexSize) {
            System.out.println();
            i = 0;
            verts++;
        }
    }*/

	Vector3 averageVector(Vector3[] vectors) {
	    Vector3 sum = new Vector3();
	    int count = vectors.length;

	    for (Vector3 vector : vectors) {
	        sum.add(vector);
	    }

	    return sum.scl(1.0f / count);
	}

	/*
	int numVertices = m.getNumVertices();
    if (numVertices > 0) {
    	int vertexSize = m.getVertexAttributes().vertexSize / 4;
		float[] vertices = new float[numVertices * vertexSize];
		m.getVertices(vertices);

		int vertexCount = vertices.length / m.getVertexSize();
		Integer[] indices = new Integer[vertexCount];
		for (int i = 0; i < vertexCount; i++) {
		    indices[i] = i;
		}

		Comparator<Integer> distanceComparator = new Comparator<Integer>() {
		    @Override
		    public int compare(Integer idx1, Integer idx2) {
		        Vector3 camPosition = Hpb.world.player.cam.cam.position;
		        Vector3 vertex1 = new Vector3(vertices[idx1+sd], vertices[idx1 + 1+sd], vertices[idx1 + 2+sd]);
		        Vector3 vertex2 = new Vector3(vertices[idx2+sd], vertices[idx2 + 1+sd], vertices[idx2 + 2+sd]);

		        float distance1 = camPosition.dst2(vertex1);
		        float distance2 = camPosition.dst2(vertex2);

		        return Float.compare(distance2, distance1);
		    }
		};

		Arrays.sort(indices, distanceComparator);

		float[] sortedVertices = new float[vertices.length];
		for (int i = 0; i < vertexCount; i++) {
		    int originalIndex = indices[i];
		    int newIndex = i * m.getVertexSize();
		    System.arraycopy(vertices, originalIndex * m.getVertexSize(), sortedVertices, newIndex, m.getVertexSize());
		}

		m.setVertices(sortedVertices);
    }*/

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
