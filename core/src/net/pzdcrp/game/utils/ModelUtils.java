package net.pzdcrp.game.utils;

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

import net.pzdcrp.game.GameInstance;
import net.pzdcrp.game.data.Vector3D;

public class ModelUtils {
	@Deprecated
	public static Model createCubeModel(boolean top, boolean bottom, boolean left, boolean right, boolean front, boolean back, String materialName, boolean transparent) {
	    ModelBuilder modelBuilder = new ModelBuilder();
	    modelBuilder.begin();
	    Material material;// = GameInstance.getMaterial(materialName);
	    /*if (material == null) {
	    	System.out.println(materialName+" yo wha da hell bro");
	    	System.exit(0);
	    }*/
	    /*if (transparent) {
	    	material = new Material(
	    		TextureAttribute.createDiffuse(GameInstance.getTexture(materialName)),
    			IntAttribute.createCullFace(GL20.GL_FRONT),
    			new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
	    	);
	    } else {
	    	material = new Material(
	    		TextureAttribute.createDiffuse(GameInstance.getTexture(materialName)),
	    		IntAttribute.createCullFace(GL20.GL_FRONT)
	    	);
	    }*/
	    material = new Material(
	    		TextureAttribute.createDiffuse(GameInstance.getTexture(materialName)),
    			IntAttribute.createCullFace(GL20.GL_FRONT),
    			new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
	    	);
	    MeshPartBuilder builder = modelBuilder.part("cube", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates, material);

	    //Vector3 normal = new Vector3();
	    builder.setUVRange(0, 0, 1, 1);
	    // bottom
	    if (!bottom) {
	    	builder.rect(
	    			-0.5f, -0.5f, 0.5f, 
	    			0.5f, -0.5f, 0.5f, 
	    			0.5f, -0.5f, -0.5f, 
	    			-0.5f, -0.5f, -0.5f, 
	    			0, -1, 0);
	    }

	    // top
	    if (!top) {
	        builder.rect(
	                -0.5f, 0.5f, -0.5f,
	                 0.5f, 0.5f, -0.5f,
	                 0.5f, 0.5f,  0.5f,
	                -0.5f, 0.5f,  0.5f,
	                0, 1, 0);
	    }

	    // left
	    if (!left) {
	        builder.rect(
	                -0.5f, -0.5f,  0.5f,
	                -0.5f, -0.5f, -0.5f,
	                -0.5f,  0.5f, -0.5f,
	                -0.5f,  0.5f,  0.5f,
	                -1, 0, 0);
	    }

	    // right
	    if (!right) {
	        builder.rect(
	                 0.5f, -0.5f, -0.5f,
	                 0.5f, -0.5f,  0.5f,
	                 0.5f,  0.5f,  0.5f,
	                 0.5f,  0.5f, -0.5f,
	                 1, 0, 0);
	    }

	    // front
	    if (!front) {
	        builder.rect(
	                -0.5f, -0.5f, -0.5f,
	                 0.5f, -0.5f, -0.5f,
	                 0.5f,  0.5f, -0.5f,
	                -0.5f,  0.5f, -0.5f,
	                0, 0, -1);
	    }

	    // back
	    if (!back) {
	        builder.rect(
	                 0.5f, -0.5f,  0.5f,
	                -0.5f, -0.5f,  0.5f,
	                -0.5f,  0.5f,  0.5f,
	                 0.5f,  0.5f,  0.5f,
	                 0, 0, 1);
	    }

	    return modelBuilder.end();
	}
	
	public static Model createCubeModel(boolean top, boolean bottom, boolean left, boolean right, boolean front, boolean back, String materialName, boolean transparent, Vector3 pos) {
	    ModelBuilder modelBuilder = new ModelBuilder();
	    modelBuilder.begin();
	    Material material = new Material(
	    		TextureAttribute.createDiffuse(GameInstance.getTexture(materialName)),
    			IntAttribute.createCullFace(GL20.GL_FRONT),
    			new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
	    	);
	    MeshPartBuilder builder = modelBuilder.part("cube", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates, material);

	    //Vector3 normal = new Vector3();
	    builder.setUVRange(0, 0, 1, 1);
	    // bottom
	    if (!bottom) {
	    	builder.rect(
	    			pos.x-0.5f, pos.y-0.5f, pos.z+0.5f, 
	    			pos.x+0.5f, pos.y-0.5f, pos.z-0.5f, 
	    			pos.x+0.5f, pos.y-0.5f, pos.z-0.5f,// 0.5f, -0.5f, -0.5f, 
	    			pos.x-0.5f, pos.y-0.5f, pos.z-0.5f,// -0.5f, -0.5f, -0.5f, 
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

	    return modelBuilder.end();
	}
	
	public static void addCubeModel(boolean top, boolean bottom, boolean left, boolean right, boolean front, boolean back, MeshPartBuilder builder, Vector3 pos) {
	    
	    //Vector3 normal = new Vector3();
	    builder.setUVRange(0, 0, 1, 1);
	    // bottom
	    if (!bottom) {
	    	builder.rect(
	    			pos.x-0.5f, pos.y-0.5f, pos.z+0.5f, 
	    			pos.x+0.5f, pos.y-0.5f, pos.z-0.5f, 
	    			pos.x+0.5f, pos.y-0.5f, pos.z-0.5f,// 0.5f, -0.5f, -0.5f, 
	    			pos.x-0.5f, pos.y-0.5f, pos.z-0.5f,// -0.5f, -0.5f, -0.5f, 
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
}
