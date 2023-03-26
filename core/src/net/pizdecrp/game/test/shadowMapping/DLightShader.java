package net.pizdecrp.game.test.shadowMapping;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class DLightShader implements Shader{
	public ShaderProgram program;
	private Camera camera;
	private RenderContext context;
	
	int projViewTrans;
    int worldTrans;
    int cameraFar;
    
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		program.dispose();
	}

	@Override
	public void init() {
		String vert = Gdx.files.internal("dLightVertex.glsl").readString();
        String frag = Gdx.files.internal("dLightFragment.glsl").readString();
        program = new ShaderProgram(vert, frag);
        if (!program.isCompiled())
            throw new GdxRuntimeException(program.getLog());
        
        projViewTrans = program.getUniformLocation("u_projViewTrans");
        worldTrans = program.getUniformLocation("u_worldTrans");
        cameraFar = program.getUniformLocation("cameraFar");
	}

	@Override
	public int compareTo(Shader other) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean canRender(Renderable instance) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void begin(Camera camera, RenderContext context) {
		this.camera = camera;
		this.context = context;
		program.begin();
        context.setDepthTest(GL20.GL_LEQUAL);
        context.setCullFace(GL20.GL_BACK);
        program.setUniformMatrix(projViewTrans, camera.combined);
        program.setUniformf(cameraFar, camera.far);
		
	}
	
	
	@Override
	public void render(Renderable renderable) {
		program.setUniformMatrix(worldTrans, renderable.worldTransform);
		renderable.meshPart.render(program);
	}

	@Override
	public void end() {
		// TODO Auto-generated method stub
		program.end();
	}

}