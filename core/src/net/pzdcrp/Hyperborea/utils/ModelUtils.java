package net.pzdcrp.Hyperborea.utils;

import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.Vector3D;

public class ModelUtils {
	public static void addCubeModel(boolean top, boolean bottom, boolean left, boolean right, boolean front, boolean back, MeshPartBuilder builder, Vector3 pos) {
	    
	    //Vector3 normal = new Vector3();
	    builder.setUVRange(0, 0, 1, 1);
	    // bottom
	    if (!bottom) {
	    	builder.rect(
	    			pos.x-0.5f, pos.y-0.5f, pos.z+0.5f, 
	    			pos.x+0.5f, pos.y-0.5f, pos.z+0.5f, 
	    			pos.x+0.5f, pos.y-0.5f, pos.z-0.5f,
	    			pos.x-0.5f, pos.y-0.5f, pos.z-0.5f,
	    			0, -1, 0);
	    }

	    // top
	    if (!top) {
	        builder.rect(
	        		pos.x-0.5f, pos.y+0.5f, pos.z-0.5f,// -0.5f, 0.5f, -0.5f,
	        		pos.x+0.5f, pos.y+0.5f, pos.z-0.5f,//  0.5f, 0.5f, -0.5f,
	        		pos.x+0.5f, pos.y+0.5f, pos.z+0.5f,//  0.5f, 0.5f,  0.5f,
	        		pos.x-0.5f, pos.y+0.5f, pos.z+0.5f,// -0.5f, 0.5f,  0.5f,
	                0, 1, 0);
	    }

	    // left
	    if (!left) {
	        builder.rect(
	        		pos.x-0.5f, pos.y-0.5f, pos.z+0.5f,// -0.5f, -0.5f,  0.5f,
	        		pos.x-0.5f, pos.y-0.5f, pos.z-0.5f,// -0.5f, -0.5f, -0.5f,
	        		pos.x-0.5f, pos.y+0.5f, pos.z-0.5f,// -0.5f,  0.5f, -0.5f,
	        		pos.x-0.5f, pos.y+0.5f, pos.z+0.5f,// -0.5f,  0.5f,  0.5f,
	                -1, 0, 0);
	    }

	    // right
	    if (!right) {
	        builder.rect(
	        		pos.x+0.5f, pos.y-0.5f, pos.z-0.5f,// 0.5f, -0.5f, -0.5f,
	        		pos.x+0.5f, pos.y-0.5f, pos.z+0.5f,// 0.5f, -0.5f,  0.5f,
	        		pos.x+0.5f, pos.y+0.5f, pos.z+0.5f,// 0.5f,  0.5f,  0.5f,
	        		pos.x+0.5f, pos.y+0.5f, pos.z-0.5f,// 0.5f,  0.5f, -0.5f,
	                 1, 0, 0);
	    }

	    // front
	    if (!front) {
	        builder.rect(
	        		pos.x-0.5f, pos.y-0.5f, pos.z-0.5f,// -0.5f, -0.5f, -0.5f,
	        		pos.x+0.5f, pos.y-0.5f, pos.z-0.5f,//  0.5f, -0.5f, -0.5f,
	        		pos.x+0.5f, pos.y+0.5f, pos.z-0.5f,//  0.5f,  0.5f, -0.5f,
	        		pos.x-0.5f, pos.y+0.5f, pos.z-0.5f,// -0.5f,  0.5f, -0.5f,
	                0, 0, -1);
	    }

	    // back
	    if (!back) {
	        builder.rect(
	        		pos.x+0.5f, pos.y-0.5f, pos.z+0.5f,// 0.5f, -0.5f,  0.5f,
	        		pos.x-0.5f, pos.y-0.5f, pos.z+0.5f,// -0.5f, -0.5f,  0.5f,
	        		pos.x-0.5f, pos.y+0.5f, pos.z+0.5f,// -0.5f,  0.5f,  0.5f,
	        		pos.x+0.5f, pos.y+0.5f, pos.z+0.5f,//  0.5f,  0.5f,  0.5f,
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
	
	public static void setTransform(Vector3D pos) {
		sp.x = (float)pos.x+0.5f;
		sp.y = (float)pos.y+0.5f;
		sp.z = (float)pos.z+0.5f;
	}
	
	public static void buildTopZ(MeshPartBuilder mpb) {
		mpb.rect(
		sp.x-0.5f, sp.y+0.5f, sp.z-0.5f,
		sp.x+0.5f, sp.y+0.5f, sp.z-0.5f,
		sp.x+0.5f, sp.y+0.5f, sp.z+0.5f,
		sp.x-0.5f, sp.y+0.5f, sp.z+0.5f,
        0, 1, 0);
	}
	
	public static void buildTopX(MeshPartBuilder mpb) {
		mpb.rect(
		sp.x-0.5f, sp.y+0.5f, sp.z+0.5f,
		sp.x-0.5f, sp.y+0.5f, sp.z-0.5f,
		sp.x+0.5f, sp.y+0.5f, sp.z-0.5f,
		sp.x+0.5f, sp.y+0.5f, sp.z+0.5f,
        0, 1, 0);
	}
	
	public static void buildBottomZ(MeshPartBuilder mpb) {
		mpb.rect(
		sp.x-0.5f, sp.y-0.5f, sp.z+0.5f, 
		sp.x+0.5f, sp.y-0.5f, sp.z+0.5f, 
		sp.x+0.5f, sp.y-0.5f, sp.z-0.5f,
		sp.x-0.5f, sp.y-0.5f, sp.z-0.5f,
		0, -1, 0);
	}
	
	public static void buildBottomX(MeshPartBuilder mpb) {
		mpb.rect(
		sp.x-0.5f, sp.y-0.5f, sp.z-0.5f,
		sp.x-0.5f, sp.y-0.5f, sp.z+0.5f, 
		sp.x+0.5f, sp.y-0.5f, sp.z+0.5f, 
		sp.x+0.5f, sp.y-0.5f, sp.z-0.5f,
		0, -1, 0);
	}
	
	public static void buildLeftY(MeshPartBuilder mpb) {
		mpb.rect(
				sp.x-0.5f, sp.y+0.5f, sp.z-0.5f,
		sp.x-0.5f, sp.y+0.5f, sp.z+0.5f,
		sp.x-0.5f, sp.y-0.5f, sp.z+0.5f,
		sp.x-0.5f, sp.y-0.5f, sp.z-0.5f,
        -1, 0, 0);
	}
	
	public static void buildLeftZ(MeshPartBuilder mpb) {
		mpb.rect(
		sp.x-0.5f, sp.y+0.5f, sp.z+0.5f,
		sp.x-0.5f, sp.y-0.5f, sp.z+0.5f,
		sp.x-0.5f, sp.y-0.5f, sp.z-0.5f,
		sp.x-0.5f, sp.y+0.5f, sp.z-0.5f,
        -1, 0, 0);
	}
	
	public static void buildLeftNY(MeshPartBuilder mpb) {
		mpb.rect(
		sp.x-0.5f, sp.y+0.5f, sp.z-0.5f,
		sp.x-0.5f, sp.y+0.5f, sp.z+0.5f,
		sp.x-0.5f, sp.y-0.5f, sp.z+0.5f,
		sp.x-0.5f, sp.y-0.5f, sp.z-0.5f,
        -1, 0, 0);
	}
	
	public static void buildLeftPY(MeshPartBuilder mpb) {
		mpb.rect(
		sp.x-0.5f, sp.y-0.5f, sp.z+0.5f,
		sp.x-0.5f, sp.y-0.5f, sp.z-0.5f,
		sp.x-0.5f, sp.y+0.5f, sp.z-0.5f,
		sp.x-0.5f, sp.y+0.5f, sp.z+0.5f,
        -1, 0, 0);
	}
	
	public static void buildRightY(MeshPartBuilder mpb) {
		mpb.rect(
				sp.x+0.5f, sp.y+0.5f, sp.z+0.5f,
		sp.x+0.5f, sp.y+0.5f, sp.z-0.5f,
		sp.x+0.5f, sp.y-0.5f, sp.z-0.5f,
		sp.x+0.5f, sp.y-0.5f, sp.z+0.5f,
         1, 0, 0);
	}
	
	public static void buildRightPZ(MeshPartBuilder mpb) {
		mpb.rect(
		sp.x+0.5f, sp.y+0.5f, sp.z-0.5f,
		sp.x+0.5f, sp.y-0.5f, sp.z-0.5f,
		sp.x+0.5f, sp.y-0.5f, sp.z+0.5f,
		sp.x+0.5f, sp.y+0.5f, sp.z+0.5f,
         1, 0, 0);
	}
	
	public static void buildRightNY(MeshPartBuilder mpb) {
		mpb.rect(
		sp.x+0.5f, sp.y+0.5f, sp.z+0.5f,
		sp.x+0.5f, sp.y+0.5f, sp.z-0.5f,
		sp.x+0.5f, sp.y-0.5f, sp.z-0.5f,
		sp.x+0.5f, sp.y-0.5f, sp.z+0.5f,
         1, 0, 0);
	}
	
	public static void buildRightPY(MeshPartBuilder mpb) {
		mpb.rect(
		sp.x+0.5f, sp.y-0.5f, sp.z-0.5f,
		sp.x+0.5f, sp.y-0.5f, sp.z+0.5f,
		sp.x+0.5f, sp.y+0.5f, sp.z+0.5f,
		sp.x+0.5f, sp.y+0.5f, sp.z-0.5f,
         1, 0, 0);
	}
	
	public static void buildFrontY(MeshPartBuilder mpb) {
		mpb.rect(
		sp.x-0.5f, sp.y-0.5f, sp.z-0.5f,
		sp.x+0.5f, sp.y-0.5f, sp.z-0.5f,
		sp.x+0.5f, sp.y+0.5f, sp.z-0.5f,
		sp.x-0.5f, sp.y+0.5f, sp.z-0.5f,
        0, 0, -1);
	}
	
	public static void buildFrontX(MeshPartBuilder mpb) {
		mpb.rect(
				sp.x-0.5f, sp.y+0.5f, sp.z-0.5f,
		sp.x-0.5f, sp.y-0.5f, sp.z-0.5f,
		sp.x+0.5f, sp.y-0.5f, sp.z-0.5f,
		sp.x+0.5f, sp.y+0.5f, sp.z-0.5f,
        0, 0, -1);
	}
	
	public static void buildBackY(MeshPartBuilder mpb) {
		mpb.rect(
		sp.x+0.5f, sp.y-0.5f, sp.z+0.5f,
		sp.x-0.5f, sp.y-0.5f, sp.z+0.5f,
		sp.x-0.5f, sp.y+0.5f, sp.z+0.5f,
		sp.x+0.5f, sp.y+0.5f, sp.z+0.5f,
         0, 0, 1);
	}
	
	public static void buildBackX(MeshPartBuilder mpb) {
		mpb.rect(
				sp.x+0.5f, sp.y+0.5f, sp.z+0.5f,
		sp.x+0.5f, sp.y-0.5f, sp.z+0.5f,
		sp.x-0.5f, sp.y-0.5f, sp.z+0.5f,
		sp.x-0.5f, sp.y+0.5f, sp.z+0.5f,
         0, 0, 1);
	}
}
