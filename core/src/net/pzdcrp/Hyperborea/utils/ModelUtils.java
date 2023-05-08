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
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.extended.SexyMeshBuilder;

public class ModelUtils {
	public static void addCubeModel(boolean top, boolean bottom, boolean left, boolean right, boolean front, boolean back, SexyMeshBuilder builder, Vector3 pos) {
	    pos.add(-0.5f);
	    //Vector3 normal = new Vector3();
	    builder.setUVRange(0, 0, 1, 1);
	    // bottom
	    if (!bottom) {
	    	builder.rect(
	    			pos.x, pos.y, pos.z+1, 
	    			pos.x+1, pos.y, pos.z+1, 
	    			pos.x+1, pos.y, pos.z,
	    			pos.x, pos.y, pos.z,
	    			0, -1, 0);
	    }

	    // top
	    if (!top) {
	        builder.rect(
	        		pos.x, pos.y+1, pos.z,// , 0.5f, ,
	        		pos.x+1, pos.y+1, pos.z,//  0.5f, 0.5f, ,
	        		pos.x+1, pos.y+1, pos.z+1,//  0.5f, 0.5f,  0.5f,
	        		pos.x, pos.y+1, pos.z+1,// , 0.5f,  0.5f,
	                0, 1, 0);
	    }

	    // left
	    if (!left) {
	        builder.rect(
	        		pos.x, pos.y, pos.z+1,// , ,  0.5f,
	        		pos.x, pos.y, pos.z,// , , ,
	        		pos.x, pos.y+1, pos.z,// ,  0.5f, ,
	        		pos.x, pos.y+1, pos.z+1,// ,  0.5f,  0.5f,
	                -1, 0, 0);
	    }

	    // right
	    if (!right) {
	        builder.rect(
	        		pos.x+1, pos.y, pos.z,// 0.5f, , ,
	        		pos.x+1, pos.y, pos.z+1,// 0.5f, ,  0.5f,
	        		pos.x+1, pos.y+1, pos.z+1,// 0.5f,  0.5f,  0.5f,
	        		pos.x+1, pos.y+1, pos.z,// 0.5f,  0.5f, ,
	                 1, 0, 0);
	    }

	    // front
	    if (!front) {
	        builder.rect(
	        		pos.x, pos.y, pos.z,// , , ,
	        		pos.x+1, pos.y, pos.z,//  0.5f, , ,
	        		pos.x+1, pos.y+1, pos.z,//  0.5f,  0.5f, ,
	        		pos.x, pos.y+1, pos.z,// ,  0.5f, ,
	                0, 0, -1);
	    }

	    // back
	    if (!back) {
	        builder.rect(
	        		pos.x+1, pos.y, pos.z+1,// 0.5f, ,  0.5f,
	        		pos.x, pos.y, pos.z+1,// , ,  0.5f,
	        		pos.x, pos.y+1, pos.z+1,// ,  0.5f,  0.5f,
	        		pos.x+1, pos.y+1, pos.z+1,//  0.5f,  0.5f,  0.5f,
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
		sp.x = (float)pos.x;
		sp.y = (float)pos.y;
		sp.z = (float)pos.z;
	}
	
	public static void buildTopZ(SexyMeshBuilder mpb) {
		mpb.rect(
		sp.x, sp.y+1, sp.z,
		sp.x+1, sp.y+1, sp.z,
		sp.x+1, sp.y+1, sp.z+1,
		sp.x, sp.y+1, sp.z+1,
        0, 1, 0);
	}
	
	public static void buildTopX(SexyMeshBuilder mpb) {
		mpb.rect(
		sp.x, sp.y+1, sp.z+1,
		sp.x, sp.y+1, sp.z,
		sp.x+1, sp.y+1, sp.z,
		sp.x+1, sp.y+1, sp.z+1,
        0, 1, 0);
	}
	
	public static void buildBottomZ(SexyMeshBuilder mpb) {
		mpb.rect(
		sp.x, sp.y, sp.z+1, 
		sp.x+1, sp.y, sp.z+1, 
		sp.x+1, sp.y, sp.z,
		sp.x, sp.y, sp.z,
		0, -1, 0);
	}
	
	public static void buildBottomX(SexyMeshBuilder mpb) {
		mpb.rect(
		sp.x, sp.y, sp.z,
		sp.x, sp.y, sp.z+1, 
		sp.x+1, sp.y, sp.z+1, 
		sp.x+1, sp.y, sp.z,
		0, -1, 0);
	}
	
	public static void buildLeftY(SexyMeshBuilder mpb) {
		mpb.rect(
				sp.x, sp.y+1, sp.z,
		sp.x, sp.y+1, sp.z+1,
		sp.x, sp.y, sp.z+1,
		sp.x, sp.y, sp.z,
        -1, 0, 0);
	}
	
	public static void buildLeftZ(SexyMeshBuilder mpb) {
		mpb.rect(
		sp.x, sp.y+1, sp.z+1,
		sp.x, sp.y, sp.z+1,
		sp.x, sp.y, sp.z,
		sp.x, sp.y+1, sp.z,
        -1, 0, 0);
	}
	
	public static void buildLeftNY(SexyMeshBuilder mpb) {
		mpb.rect(
		sp.x, sp.y+1, sp.z,
		sp.x, sp.y+1, sp.z+1,
		sp.x, sp.y, sp.z+1,
		sp.x, sp.y, sp.z,
        -1, 0, 0);
	}
	
	public static void buildLeftPY(SexyMeshBuilder mpb) {
		mpb.rect(
		sp.x, sp.y, sp.z+1,
		sp.x, sp.y, sp.z,
		sp.x, sp.y+1, sp.z,
		sp.x, sp.y+1, sp.z+1,
        -1, 0, 0);
	}
	
	public static void buildRightY(SexyMeshBuilder mpb) {
		mpb.rect(
				sp.x+1, sp.y+1, sp.z+1,
		sp.x+1, sp.y+1, sp.z,
		sp.x+1, sp.y, sp.z,
		sp.x+1, sp.y, sp.z+1,
         1, 0, 0);
	}
	
	public static void buildRightPZ(SexyMeshBuilder mpb) {
		mpb.rect(
		sp.x+1, sp.y+1, sp.z,
		sp.x+1, sp.y, sp.z,
		sp.x+1, sp.y, sp.z+1,
		sp.x+1, sp.y+1, sp.z+1,
         1, 0, 0);
	}
	
	public static void buildRightNY(SexyMeshBuilder mpb) {
		mpb.rect(
		sp.x+1, sp.y+1, sp.z+1,
		sp.x+1, sp.y+1, sp.z,
		sp.x+1, sp.y, sp.z,
		sp.x+1, sp.y, sp.z+1,
         1, 0, 0);
	}
	
	public static void buildRightPY(SexyMeshBuilder mpb) {
		mpb.rect(
		sp.x+1, sp.y, sp.z,
		sp.x+1, sp.y, sp.z+1,
		sp.x+1, sp.y+1, sp.z+1,
		sp.x+1, sp.y+1, sp.z,
         1, 0, 0);
	}
	
	public static void buildFrontY(SexyMeshBuilder mpb) {
		mpb.rect(
		sp.x, sp.y, sp.z,
		sp.x+1, sp.y, sp.z,
		sp.x+1, sp.y+1, sp.z,
		sp.x, sp.y+1, sp.z,
        0, 0, -1);
	}
	
	public static void buildFrontX(SexyMeshBuilder mpb) {
		mpb.rect(
				sp.x, sp.y+1, sp.z,
		sp.x, sp.y, sp.z,
		sp.x+1, sp.y, sp.z,
		sp.x+1, sp.y+1, sp.z,
        0, 0, -1);
	}
	
	public static void buildBackY(SexyMeshBuilder mpb) {
		mpb.rect(
		sp.x+1, sp.y, sp.z+1,
		sp.x, sp.y, sp.z+1,
		sp.x, sp.y+1, sp.z+1,
		sp.x+1, sp.y+1, sp.z+1,
         0, 0, 1);
	}
	
	public static void buildBackX(SexyMeshBuilder mpb) {
		mpb.rect(
				sp.x+1, sp.y+1, sp.z+1,
		sp.x+1, sp.y, sp.z+1,
		sp.x, sp.y, sp.z+1,
		sp.x, sp.y+1, sp.z+1,
         0, 0, 1);
	}
}
