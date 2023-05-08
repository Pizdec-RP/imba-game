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

import net.pzdcrp.Hyperborea.utils.MathU;

public class SuperPizdatiyShader extends BaseShaderProvider {
    //public int test;
    public List<DefaultShader> local = new CopyOnWriteArrayList<>();
    public List<Shader> stage2 = new CopyOnWriteArrayList<>();
    public Texture stage2pic;
    public int stage = 0;
    public float testx = 0,testz = 0;
    public int testy = 0;
 
    public SuperPizdatiyShader () {
    }
    
    public void newstage() {
    	stage++;
    }
    
    public void end() {
    	stage = 0;
    }
    
    @Override
	public Shader getShader (Renderable renderable) {
    	/*for (Object o : (Object[])renderable.userData) {
    		System.out.println(o.toString());
    	}*/
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
    	if (((String)((Object[])renderable.userData)[0]).startsWith("c")) {
    		System.out.println("renderable tag: "+(String)((Object[])renderable.userData)[0]);
	    	if (stage == 1) {
	    		System.out.println("st1");
	    		String vert = Gdx.files.internal("shaders/vertexShader.vert").readString();
		        String frag = Gdx.files.internal("shaders/fragmentShader.frag").readString();
		        DefaultShader shader = new TestShader(renderable, new DefaultShader.Config(vert, frag),this);
		        local.add(shader);
		        return shader;
	    	} else if (stage == 2) {
	    		System.out.println("st2");
	    		String vert = Gdx.files.internal("shaders/2stageVertexShader.vert").readString();
		        String frag = Gdx.files.internal("shaders/2stageFragmentShader.frag").readString();
		        DefaultShader shader = new Stage2Shader(renderable, new DefaultShader.Config(vert, frag),this);
		        local.add(shader);
		        return shader;
	    	}
    	}
    	System.out.println("creating default shader for");
    	return new DefaultShader(renderable);
    }
}

class TestShader extends DefaultShader {
	private SuperPizdatiyShader s;
	protected int test = register(new Uniform("test"));
	protected int haslight = register(new Uniform("haslight"));
	protected int sdvigx = register(new Uniform("sdvigx"));
	protected int sdvigz = register(new Uniform("sdvigz"));
	protected int sdvigy = register(new Uniform("sdvigy"));
	protected int intArrayTexture = register(new Uniform("intArrayTexture"));

	public TestShader(Renderable renderable, Config config, SuperPizdatiyShader s) {
		super(renderable, config);
		//config.vertexShader = "#version 150\n"+config.vertexShader;
		//config.fragmentShader = "#version 150\n"+config.fragmentShader;
		this.s = s;
	}
	
	@Override
	public void begin(final Camera camera, final RenderContext context) {
		super.begin(camera, context);
	}
	
	@Override
	public void render (Renderable renderable, Attributes combinedAttributes) {
		try {
			Object[] data = (Object[])renderable.userData;
			//System.out.println("--------");
			/*for (Object o : data) {
				System.out.println(o.toString());
			}*/
			if (data.length >= 3 && data[2] instanceof Texture) {//если есть система освещения
				//System.out.println("zaebis: "+data[1]);
				Texture casted = (Texture)data[2];
				set(haslight, 1);
				set(intArrayTexture, casted);
				
				/*if (casted.length <= 4096) {
				    set(haslight, 1);
				    Gdx.gl20.glUniform1iv(loc, casted.length, casted, 0);
				} else if (casted.length <= 8192) {
				    set(haslight, 2);
				    Gdx.gl20.glUniform2iv(loc, casted.length / 2, casted, 0);
				} else if (casted.length <= 12288) {
				    set(haslight, 3);
				    Gdx.gl20.glUniform3iv(loc, casted.length / 3, casted, 0);
				} else {
					//максимальное количество вершин ~64к но данных вмещается ток 16384. надо переделать класс SexyMeshBuilder там кароче поменять надо maxvertices На 16383
					throw new Exception("массив данных об освещении не вмещается в шейдер");
				}*/
				//1-4096 2-8192 3-12288 4-16384
			} else {
				//System.out.println("huevo");
				this.set(haslight, 0);
			}
			set(test, new Vector3(1,1,0));//TODO удалить
			set(sdvigx, s.testx);//TODO удалить
			set(sdvigy, s.testy);//TODO удалить
			set(sdvigz, s.testz);//TODO удалить
			super.render(renderable, combinedAttributes);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
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