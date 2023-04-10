package net.pzdcrp.Hyperborea;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader.Uniform;
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class SuperPizdatiyShader extends DefaultShaderProvider {
	 
    private String shaderName;
    //public int test;
    public List<DefaultShader> shaders = new CopyOnWriteArrayList<>();
 
    public SuperPizdatiyShader (String shaderName) {
        this.shaderName = shaderName;
    }
 
    @Override
    protected Shader createShader(Renderable renderable) {
    	System.out.println("renderable tag: "+(String)renderable.userData);
    	if (((String)renderable.userData).equals("c")) {
    		String vert = Gdx.files.internal("shaders/vertexShader.vert").readString();
	        String frag = Gdx.files.internal("shaders/fragmentShader.frag").readString();
	        DefaultShader shader = new TestShader(renderable, new DefaultShader.Config(vert, frag),this);
	        shaders.add(shader);
	        return shader;
    	} else {
    		return new DefaultShader(renderable);
    	}
    }
}

class TestShader extends DefaultShader {
	private SuperPizdatiyShader s;
	protected int test = register(new Uniform("test"));

	public TestShader(Renderable renderable, Config config, SuperPizdatiyShader s) {
		super(renderable, config);
		this.s = s;
	}
	
	@Override
	public void begin(final Camera camera, final RenderContext context) {
		super.begin(camera, context);
	}
	
	@Override
	public void render (Renderable renderable, Attributes combinedAttributes) {
		this.set(test, new Vector3(1,1,0));
		super.render(renderable, combinedAttributes);
	}
}