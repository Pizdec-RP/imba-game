package net.pzdcrp.Aselia.utils;

import java.util.List;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BaseShapeBuilder;
import com.badlogic.gdx.math.Vector3;

import net.pzdcrp.Aselia.data.AABB;
import net.pzdcrp.Aselia.data.Vector3D;
import net.pzdcrp.Aselia.extended.SexyMeshBuilder;

public class ModelUtils extends BaseShapeBuilder {
	public static void addCubeModel(boolean top, boolean bottom, boolean left, boolean right, boolean front, boolean back, SexyMeshBuilder builder, Vector3 pos) {
	    pos.add(-0.5f);
	    //Vector3 normal = new Vector3();
	    builder.setUVRange(0, 0, 1, 1);
	    // bottom
	    if (!bottom) {
	    	builder.rect(
	    			pos.x, pos.y, pos.z+blockScale,
	    			pos.x+blockScale, pos.y, pos.z+blockScale,
	    			pos.x+blockScale, pos.y, pos.z,
	    			pos.x, pos.y, pos.z,
	    			0, -1, 0);
	    }

	    // top
	    if (!top) {
	        builder.rect(
	        		pos.x, pos.y+blockScale, pos.z,// , 0.5f, ,
	        		pos.x+blockScale, pos.y+blockScale, pos.z,//  0.5f, 0.5f, ,
	        		pos.x+blockScale, pos.y+blockScale, pos.z+blockScale,//  0.5f, 0.5f,  0.5f,
	        		pos.x, pos.y+blockScale, pos.z+blockScale,// , 0.5f,  0.5f,
	                0, 1, 0);
	    }

	    // left
	    if (!left) {
	        builder.rect(
	        		pos.x, pos.y, pos.z+blockScale,// , ,  0.5f,
	        		pos.x, pos.y, pos.z,// , , ,
	        		pos.x, pos.y+blockScale, pos.z,// ,  0.5f, ,
	        		pos.x, pos.y+blockScale, pos.z+blockScale,// ,  0.5f,  0.5f,
	                -1, 0, 0);
	    }

	    // right
	    if (!right) {
	        builder.rect(
	        		pos.x+blockScale, pos.y, pos.z,// 0.5f, , ,
	        		pos.x+blockScale, pos.y, pos.z+blockScale,// 0.5f, ,  0.5f,
	        		pos.x+blockScale, pos.y+blockScale, pos.z+blockScale,// 0.5f,  0.5f,  0.5f,
	        		pos.x+blockScale, pos.y+blockScale, pos.z,// 0.5f,  0.5f, ,
	                 1, 0, 0);
	    }

	    // front
	    if (!front) {
	        builder.rect(
	        		pos.x, pos.y, pos.z,// , , ,
	        		pos.x+blockScale, pos.y, pos.z,//  0.5f, , ,
	        		pos.x+blockScale, pos.y+blockScale, pos.z,//  0.5f,  0.5f, ,
	        		pos.x, pos.y+blockScale, pos.z,// ,  0.5f, ,
	                0, 0, -1);
	    }

	    // back
	    if (!back) {
	        builder.rect(
	        		pos.x+blockScale, pos.y, pos.z+blockScale,// 0.5f, ,  0.5f,
	        		pos.x, pos.y, pos.z+blockScale,// , ,  0.5f,
	        		pos.x, pos.y+blockScale, pos.z+blockScale,// ,  0.5f,  0.5f,
	        		pos.x+blockScale, pos.y+blockScale, pos.z+blockScale,//  0.5f,  0.5f,  0.5f,
	                 0, 0, 1);
	    }
	}



	public static ModelInstance combineModels(List<Model> world) {
	    ModelBuilder modelBuilder = new ModelBuilder();
	    modelBuilder.begin();
	    for (Model model : world) {
	    	int i = 0;
	        for (Mesh mesh : model.meshes) {
	            modelBuilder.part("", mesh, GL20.GL_TRIANGLES, model.materials.get(i));
	            i++;
	        }
	    }
	    Model combinedModel = modelBuilder.end();
	    return new ModelInstance(combinedModel);
	}


	public static Vector3 sp = new Vector3();
	private static float blockScale = 1f;
	private static AABB modelBounds;
	private static final AABB defaultBounds = new AABB(0,0,0,1,1,1);
	
	public static void setModelSizes(AABB modelBound) {
		modelBounds = modelBound;
	}
	
	public static void resetModelSizes() {
		modelBounds = defaultBounds;
	}

	public static void setScale(float scl) {
		blockScale = scl;
	}

	public static void setTransform(Vector3D pos) {
		sp.x = (float)pos.x;
		sp.y = (float)pos.y;
		sp.z = (float)pos.z;
		resetModelSizes();
	}

	public static void buildTopZ(SexyMeshBuilder mpb) {
		mpb.rect(
		sp.x+modelBounds.minX, sp.y+blockScale, sp.z,
		sp.x+blockScale, sp.y+blockScale, sp.z,
		sp.x+blockScale, sp.y+blockScale, sp.z+blockScale,
		sp.x, sp.y+blockScale, sp.z+blockScale,
        0, 1, 0);
	}

	public static void buildTopX(SexyMeshBuilder mpb) {
		mpb.rect(
		sp.x, sp.y+blockScale, sp.z+blockScale,
		sp.x, sp.y+blockScale, sp.z,
		sp.x+blockScale, sp.y+blockScale, sp.z,
		sp.x+blockScale, sp.y+blockScale, sp.z+blockScale,
        0, 1, 0);
	}

	public static void buildBottomZ(SexyMeshBuilder mpb) {
		mpb.rect(
		sp.x, sp.y, sp.z+blockScale,
		sp.x+blockScale, sp.y, sp.z+blockScale,
		sp.x+blockScale, sp.y, sp.z,
		sp.x, sp.y, sp.z,
		0, -1, 0);
	}

	public static void buildBottomX(SexyMeshBuilder mpb) {
		mpb.rect(
		sp.x, sp.y, sp.z,
		sp.x, sp.y, sp.z+blockScale,
		sp.x+blockScale, sp.y, sp.z+blockScale,
		sp.x+blockScale, sp.y, sp.z,
		0, -1, 0);
	}

	public static void buildLeftY(SexyMeshBuilder mpb) {
		mpb.rect(
				sp.x, sp.y+blockScale, sp.z,
		sp.x, sp.y+blockScale, sp.z+blockScale,
		sp.x, sp.y, sp.z+blockScale,
		sp.x, sp.y, sp.z,
        -1, 0, 0);
	}

	public static void buildLeftZ(SexyMeshBuilder mpb) {
		mpb.rect(
		sp.x, sp.y+blockScale, sp.z+blockScale,
		sp.x, sp.y, sp.z+blockScale,
		sp.x, sp.y, sp.z,
		sp.x, sp.y+blockScale, sp.z,
        -1, 0, 0);
	}

	public static void buildLeftNY(SexyMeshBuilder mpb) {
		mpb.rect(
		sp.x, sp.y+blockScale, sp.z,
		sp.x, sp.y+blockScale, sp.z+blockScale,
		sp.x, sp.y, sp.z+blockScale,
		sp.x, sp.y, sp.z,
        -1, 0, 0);
	}

	public static void buildLeftPY(SexyMeshBuilder mpb) {
		mpb.rect(
		sp.x, sp.y, sp.z+blockScale,
		sp.x, sp.y, sp.z,
		sp.x, sp.y+blockScale, sp.z,
		sp.x, sp.y+blockScale, sp.z+blockScale,
        -1, 0, 0);
	}

	public static void buildRightY(SexyMeshBuilder mpb) {
		mpb.rect(
				sp.x+blockScale, sp.y+blockScale, sp.z+blockScale,
		sp.x+blockScale, sp.y+blockScale, sp.z,
		sp.x+blockScale, sp.y, sp.z,
		sp.x+blockScale, sp.y, sp.z+blockScale,
         1, 0, 0);
	}

	public static void buildRightPZ(SexyMeshBuilder mpb) {
		mpb.rect(
		sp.x+blockScale, sp.y+blockScale, sp.z,
		sp.x+blockScale, sp.y, sp.z,
		sp.x+blockScale, sp.y, sp.z+blockScale,
		sp.x+blockScale, sp.y+blockScale, sp.z+blockScale,
         1, 0, 0);
	}

	public static void buildRightNY(SexyMeshBuilder mpb) {
		mpb.rect(
		sp.x+blockScale, sp.y+blockScale, sp.z+blockScale,
		sp.x+blockScale, sp.y+blockScale, sp.z,
		sp.x+blockScale, sp.y, sp.z,
		sp.x+blockScale, sp.y, sp.z+blockScale,
         1, 0, 0);
	}

	public static void buildRightPY(SexyMeshBuilder mpb) {
		mpb.rect(
		sp.x+blockScale, sp.y, sp.z,
		sp.x+blockScale, sp.y, sp.z+blockScale,
		sp.x+blockScale, sp.y+blockScale, sp.z+blockScale,
		sp.x+blockScale, sp.y+blockScale, sp.z,
         1, 0, 0);
	}

	public static void buildFrontY(SexyMeshBuilder mpb) {
		mpb.rect(
		sp.x, sp.y, sp.z,
		sp.x+blockScale, sp.y, sp.z,
		sp.x+blockScale, sp.y+blockScale, sp.z,
		sp.x, sp.y+blockScale, sp.z,
        0, 0, -1);
	}

	public static void buildFrontX(SexyMeshBuilder mpb) {
		mpb.rect(
				sp.x, sp.y+blockScale, sp.z,
		sp.x, sp.y, sp.z,
		sp.x+blockScale, sp.y, sp.z,
		sp.x+blockScale, sp.y+blockScale, sp.z,
        0, 0, -1);
	}

	public static void buildBackY(SexyMeshBuilder mpb) {
		mpb.rect(
		sp.x+blockScale, sp.y, sp.z+blockScale,
		sp.x, sp.y, sp.z+blockScale,
		sp.x, sp.y+blockScale, sp.z+blockScale,
		sp.x+blockScale, sp.y+blockScale, sp.z+blockScale,
         0, 0, 1);
	}

	public static void buildBackX(SexyMeshBuilder mpb) {
		mpb.rect(
				sp.x+blockScale, sp.y+blockScale, sp.z+blockScale,
		sp.x+blockScale, sp.y, sp.z+blockScale,
		sp.x, sp.y, sp.z+blockScale,
		sp.x, sp.y+blockScale, sp.z+blockScale,
         0, 0, 1);
	}
}
