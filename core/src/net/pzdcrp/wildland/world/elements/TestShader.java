package net.pzdcrp.wildland.world.elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class TestShader extends BaseShader {
	// @off
	public final static String vertexShader = Gdx.files.internal("vertexShader.vert").readString();

	public final static String fragmentShader = Gdx.files.internal("fragmentShader.frag").readString();


	protected final ShaderProgram program;

	public TestShader (Renderable renderable) {
		super();
		Gdx.app.log("ShaderTest", "Compiling test shader");

		program = new ShaderProgram(vertexShader, fragmentShader);

		if (!program.isCompiled()) throw new GdxRuntimeException("Couldn't compile shader " + program.getLog());
		String log = program.getLog();
		if (log.length() > 0) Gdx.app.error("ShaderTest", "Shader compilation log: " + log);
	}

	@Override
	public void init () {
		super.init(program, null);
	}

	@Override
	public int compareTo(Shader other) {
		return 0;
	}

	@Override
	public boolean canRender(Renderable instance) {
		return true;
	}
}