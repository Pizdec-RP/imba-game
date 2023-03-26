package net.pzdcrp.wildland;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;

public class SuperPizdatiyShader extends DefaultShaderProvider {
	 
    private String shaderName;
 
    public SuperPizdatiyShader (String shaderName) {
        this.shaderName = shaderName;
    }
 
    @Override
    protected Shader createShader(Renderable renderable) {
    	if (renderable.material.id == "s") {
	        String vert = Gdx.files.internal("vertexShader.vert").readString();
	        String frag = Gdx.files.internal("fragmentShader.frag").readString();
	        System.out.println(frag);
	        System.out.println(vert);
	        return new DefaultShader(renderable, new DefaultShader.Config(vert, frag));
    	} else {
    		return super.createShader(renderable);
    	}
    }
 
}
