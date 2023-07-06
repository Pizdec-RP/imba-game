package net.pzdcrp.Hyperborea;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
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
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader.Uniform;
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException; 

import net.pzdcrp.Hyperborea.utils.MathU;

public class SuperPizdatiyShader extends BaseShaderProvider {
    //public int test;
    public List<DefaultShader> local = new CopyOnWriteArrayList<>();
    //public List<Shader> stage2 = new CopyOnWriteArrayList<>();
    public Texture stage2pic;
    public int stage = 0;
    public float skylightlevel = 1;
    
    public void newstage() {
    	stage++;
    }
    
    public void end() {
    	stage = 0;
    }
    
    @Override
	public Shader getShader (Renderable renderable) {
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
	}
 
    @Override
    protected Shader createShader(Renderable renderable) {
    	if (renderable.userData != null) {
    		Object[] args = (Object[])renderable.userData;
    		String type = (String)args[0];
    		if (type.equals("chunk")) {
	    		System.out.println("renderable tag: "+(String)((Object[])renderable.userData)[0]);
	    		String vert = Gdx.files.internal("shaders/skyVertexShader.vert").readString();
		        String frag = Gdx.files.internal("shaders/skyFragmentShader.frag").readString();
		        DefaultShader shader = new ChunkModelShader(renderable, new DefaultShader.Config(vert, frag),this);
		        local.add(shader);
		        return shader;
	    	} else if (type.equals("sky")) {
	    		
	    	}
    	}
    	System.out.println("creating default shader for");
    	return new DefaultShader(renderable);
    }
}

class ChunkModelShader extends DefaultShader {
	private SuperPizdatiyShader s;
	private final int screensize = register(new Uniform("screensize"));

	public ChunkModelShader(Renderable renderable, Config config, SuperPizdatiyShader s) {
		super(renderable, config);
		this.s = s;
	}
	
	@Override
	public void begin(final Camera camera, final RenderContext context) {
		super.begin(camera, context);
	}
	
	@Override
	public void render (Renderable renderable, Attributes combinedAttributes) {
		set(screensize, new Vector2((float) Gdx.graphics.getWidth(), (float) Gdx.graphics.getHeight()));
		super.render(renderable, combinedAttributes);
	}
}

class SkyModelShader extends DefaultShader {
	private SuperPizdatiyShader s;
	private final int lightlevel = register(new Uniform("lightlevel"));

	public SkyModelShader(Renderable renderable, Config config, SuperPizdatiyShader s) {
		super(renderable, config);
		this.s = s;
	}
	
	@Override
	public void begin(final Camera camera, final RenderContext context) {
		super.begin(camera, context);
	}
	
	@Override
	public void render (Renderable renderable, Attributes combinedAttributes) {
		set(lightlevel, s.skylightlevel);
		super.render(renderable, combinedAttributes);
	}
}

/*class Stage2Shader extends DefaultShader {
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
}*/