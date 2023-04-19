package net.pzdcrp.Hyperborea;

import java.nio.FloatBuffer;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GLTexture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
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
import com.badlogic.gdx.utils.GdxRuntimeException;

public class SuperPizdatiyShader extends BaseShaderProvider {
	 
    private String shaderName;
    //public int test;
    public List<DefaultShader> local = new CopyOnWriteArrayList<>();
    public List<Shader> stage2 = new CopyOnWriteArrayList<>();
    public Texture stage2pic;
    public int stage = 0;
 
    public SuperPizdatiyShader (String shaderName) {
        this.shaderName = shaderName;
    }
    
    public void newstage() {
    	stage++;
    }
    
    public void end() {
    	stage = 0;
    }
    
    @Override
	public Shader getShader (Renderable renderable) {
    	if (stage == 1) {
			Shader suggestedShader = renderable.shader;
			if (suggestedShader != null && suggestedShader.canRender(renderable)) return suggestedShader;
			for (Shader shader : shaders) {
				if (shader.canRender(renderable)) return shader;
			}
			final Shader shader = createShader(renderable);
			if (!shader.canRender(renderable)) throw new GdxRuntimeException("unable to provide a shader for this renderable");
			shader.init();
			shaders.add(shader);
			return shader;
    	} else if (stage == 2) {
    		Shader suggestedShader = renderable.shader;
			if (suggestedShader != null && suggestedShader.canRender(renderable)) return suggestedShader;
			for (Shader shader : stage2) {
				if (shader.canRender(renderable)) return shader;
			}
			final Shader shader = createShader(renderable);
			if (!shader.canRender(renderable)) throw new GdxRuntimeException("unable to provide a shader for this renderable");
			shader.init();
			stage2.add(shader);
			return shader;
    	}
    	final Shader shader = createShader(renderable);
		if (!shader.canRender(renderable)) throw new GdxRuntimeException("unable to provide a shader for this renderable");
		shader.init();
		shaders.add(shader);
		return shader;
	}
 
    @Override
    protected Shader createShader(Renderable renderable) {
    	if (((String)((Object[])renderable.userData)[0]).equals("c")) {
    		System.out.println("renderable tag: "+(String)((Object[])renderable.userData)[0]);
	    	if (stage == 1) {	    	
	    		String vert = Gdx.files.internal("shaders/vertexShader.vert").readString();
		        String frag = Gdx.files.internal("shaders/fragmentShader.frag").readString();
		        DefaultShader shader = new TestShader(renderable, new DefaultShader.Config(vert, frag),this);
		        local.add(shader);
		        return shader;
	    	} else if (stage == 2) {
	    		String vert = Gdx.files.internal("shaders/2stageVertexShader.vert").readString();
		        String frag = Gdx.files.internal("shaders/2stageFragmentShader.frag").readString();
		        DefaultShader shader = new Stage2Shader(renderable, new DefaultShader.Config(vert, frag),this);
		        local.add(shader);
		        return shader;
	    	}
    	}
    	return new DefaultShader(renderable);
    }
}

class TestShader extends DefaultShader {
	private SuperPizdatiyShader s;
	protected int test = register(new Uniform("test"));
	protected int light = register(new Uniform("light3array"));
	protected int haslight = register(new Uniform("haslight"));

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
		System.out.println("rendering: "+(Hpb.counter++));
		Object[] data = (Object[])renderable.userData;
		if (data.length >= 3 && data[2] instanceof FloatBuffer) {
			FloatBuffer casted = (FloatBuffer)data[2];
			this.set(haslight, 1);
			int loc = program.getUniformLocation("light3array");
			Gdx.gl20.glUniform3fv(loc, 16*16*16, casted);
		} else {
			this.set(haslight, 0);
		}
		this.set(test, new Vector3(1,1,0));
		super.render(renderable, combinedAttributes);
	}
}

class Stage2Shader extends DefaultShader {
	private SuperPizdatiyShader s;
	private int rmt = register(new Uniform("u_reflectionTexture"));

	public Stage2Shader(Renderable renderable, Config config, SuperPizdatiyShader s) {
		super(renderable, config);
		this.s = s;
	}
	
	@Override
	public void begin(final Camera camera, final RenderContext context) {
		super.begin(camera, context);
	}
	
	@Override
	public void render (Renderable renderable, Attributes combinedAttributes) {
		if (s.stage2pic == null) {
			System.out.println("notexture");
			System.exit(0);
		}
		this.set(rmt, s.stage2pic);
		super.render(renderable, combinedAttributes);
	}
}