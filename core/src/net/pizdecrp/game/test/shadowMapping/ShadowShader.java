package net.pizdecrp.game.test.shadowMapping;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class ShadowShader implements Shader {
	private ShaderProgram program;
	private Camera camera;
	private RenderContext context;
	
	int projViewTrans;
    int worldTrans;
    int lightCamProjView;
    int depthMap;
    int diffuseMap;
    int cameraFar;
    int lightDirection;
    
    Texture depthTexture;
    Texture diffuseTexture;
    
    
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		program.dispose();
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		String vert = Gdx.files.internal("shadowVertex.glsl").readString();
        String frag = Gdx.files.internal("shadowFragment.glsl").readString();
        program = new ShaderProgram(vert, frag);
        if (!program.isCompiled())
            throw new GdxRuntimeException(program.getLog());
        
        projViewTrans = program.getUniformLocation("u_projViewTrans");
        worldTrans = program.getUniformLocation("u_worldTrans");
        lightCamProjView = program.getUniformLocation("dLightCamProjView");
        depthMap = program.getUniformLocation("depthMap");
        diffuseMap = program.getUniformLocation("diffuseTexture");
        cameraFar = program.getUniformLocation("cameraFar");
        lightDirection = program.getUniformLocation("lightDirection");
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
	
	public void setDepthMap(Texture depthTexture)
	{
		program.begin();
		this.depthTexture = depthTexture;
	}
	
	public void setLightViewProj(Camera lightCam)
	{
		program.begin();
		program.setUniformMatrix(lightCamProjView, lightCam.combined);
		program.setUniformf(lightDirection, lightCam.direction);
	}

	
	@Override
	public void render(Renderable renderable) {
		// TODO Auto-generated method stub
		program.setUniformi(depthMap, context.textureBinder.bind(depthTexture));
		//Access the diffuse textures of the Sponza mondel and send them to the GPU
		Attribute att = renderable.material.get(TextureAttribute.Diffuse);
		if(   att != null)
		{
			final int unit = context.textureBinder.bind(((TextureAttribute)(renderable.material
					.get(TextureAttribute.Diffuse))).textureDescription);
			program.setUniformi(diffuseMap, unit);
		}

		program.setUniformMatrix(worldTrans, renderable.worldTransform);
		renderable.meshPart.render(program);
	}

	@Override
	public void end() {
		// TODO Auto-generated method stub
		program.end();
	}
}