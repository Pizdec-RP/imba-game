package net.pzdcrp.Aselia;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;

import net.pzdcrp.Aselia.utils.GameU;

public class SuperPizdatiyShader extends BaseShaderProvider {
    //public int test;
    public List<DefaultShader> local = new CopyOnWriteArrayList<>();
    public int stage = 0;
    public float skylightlevel = 1;
    public Vector3 pos = Vector3.Zero;

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
    		System.out.println("renderable tag: "+type);
    		if (type.equals("chunk")) {
	    		String vert = Gdx.files.internal("shaders/chunkVertexShader.vert").readString();
		        String frag = Gdx.files.internal("shaders/chunkFragmentShader.frag").readString();
		        DefaultShader shader = new ChunkModelShader(renderable, new DefaultShader.Config(vert, frag),this);
		        local.add(shader);
		        return shader;
	    	} else if (type.equals("sky")) {
	    		String vert = Gdx.files.internal("shaders/skyVertexShader.vert").readString();
		        String frag = Gdx.files.internal("shaders/skyFragmentShader.frag").readString();
		        DefaultShader shader = new SkyModelShader(renderable, new DefaultShader.Config(vert, frag),this);
		        local.add(shader);
		        return shader;
	    	} else if (type.equals("item")) {
	    		String vert = Gdx.files.internal("shaders/itemV.glsl").readString();
		        String frag = Gdx.files.internal("shaders/itemF.glsl").readString();
		        DefaultShader shader = new ItemEntityShader(renderable, new DefaultShader.Config(vert, frag),this);
		        local.add(shader);
		        return shader;
	    	} else if (type.equals("cloud")) {
	    		String vert = Gdx.files.internal("shaders/cloudsV.glsl").readString();
		        String frag = Gdx.files.internal("shaders/cloudsF.glsl").readString();
		        DefaultShader shader = new CloudShader(renderable, new DefaultShader.Config(vert, frag));
		        local.add(shader);
		        return shader;
	    	}
    		GameU.arrayPrint("creating default shader for: ", (Object[])renderable.userData);
    	} else {
    		System.out.println("no data, creating default shader");
    	}
    	return new DefaultShader(renderable);
    }
}

class ChunkModelShader extends DefaultShader {
	private SuperPizdatiyShader s;
	private final int
	screensize = register(new Uniform("screensize")),
	campos = register(new Uniform("campos"));

	public ChunkModelShader(Renderable renderable, Config config, SuperPizdatiyShader s) {
		super(renderable, config);
		this.s = s;
	}

	@Override
	public void render (Renderable renderable, Attributes combinedAttributes) {
		set(screensize, new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		set(campos, s.pos);
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
	public void render (Renderable renderable, Attributes combinedAttributes) {
		set(lightlevel, s.skylightlevel);
		super.render(renderable, combinedAttributes);
	}
}

class ItemEntityShader extends DefaultShader {
	private SuperPizdatiyShader s;
	private final int lightlevel = register(new Uniform("light"));

	public ItemEntityShader(Renderable renderable, Config config, SuperPizdatiyShader s) {
		super(renderable, config);
		this.s = s;
	}

	@Override
	public void render (Renderable renderable, Attributes combinedAttributes) {
		float f = (float) ((Object[])renderable.userData)[1];
		set(lightlevel, f);
		super.render(renderable, combinedAttributes);
	}
}

class CloudShader extends DefaultShader {
	private final int lightlevel = register(new Uniform("light"));
	private final int weather = register(new Uniform("weather"));
	private final int pos = register(new Uniform("spos"));
	private final int sdvig = register(new Uniform("sdvig"));
	private Vector3 v = new Vector3();
	public CloudShader(Renderable renderable, Config config) {
		super(renderable, config);
	}

	@Override
	public void render (Renderable renderable, Attributes combinedAttributes) {
		set(lightlevel, Hpb.world.getWorldLightLevelNorm());
		set(weather, Hpb.world.weatherlvl);
		renderable.worldTransform.getTranslation(v);
		set(pos, v);
		set(sdvig, Hpb.world.globaltime);
		super.render(renderable, combinedAttributes);
	}
}