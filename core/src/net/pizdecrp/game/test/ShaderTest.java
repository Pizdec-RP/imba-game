package net.pizdecrp.game.test;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.utils.Array;

public class ShaderTest extends ApplicationAdapter {
	//http://obermuhlner.ch/wordpress/2014/09/01/using-glsl-shaders-to-generate-planets/
    private ModelBatch modelBatch;
 
    private PerspectiveCamera camera;
    private CameraInputController cameraInputController;
 
    private final ModelBuilder modelBuilder = new ModelBuilder();

	private ModelInstance instance;
 
    private static final int SPHERE_DIVISIONS_U = 20;
    private static final int SPHERE_DIVISIONS_V = 20;
 
    @Override
    public void create () {
        createTest(new UberShaderProvider("planet"), new Material(), Usage.Position | Usage.Normal | Usage.TextureCoordinates);
    }
 
    private void createTest(ShaderProvider shaderProvider, Material material, long usageAttributes) {
        modelBatch = new ModelBatch(shaderProvider);
 
        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(10f, 10f, 10f);
        camera.lookAt(0, 0, 0);
        camera.near = 1f;
        camera.far = 300f;
        camera.update();
 
        cameraInputController = new CameraInputController(camera);
        Gdx.input.setInputProcessor(cameraInputController);
 
        float sphereRadius = 15f;
        Model model = modelBuilder.createSphere(sphereRadius, sphereRadius, sphereRadius, SPHERE_DIVISIONS_U, SPHERE_DIVISIONS_V, material, usageAttributes);
 
        instance = new ModelInstance(model);
    }
 
    @Override
    public void render () {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.8f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
 
        cameraInputController.update();
 
        modelBatch.begin(camera);
        modelBatch.render(instance);
        modelBatch.end();
    }
}

class UberShaderProvider extends BaseShaderProvider {
	 
    @SuppressWarnings("unused")
	private String shaderName;
 
    public UberShaderProvider (String shaderName) {
        this.shaderName = shaderName;
    }
 
    @Override
    protected Shader createShader(Renderable renderable) {
        String vert = "attribute vec3 a_position;\n"
        		+ "attribute vec2 a_texCoord0;\n"
        		+ " \n"
        		+ "uniform mat4 u_worldTrans;\n"
        		+ "uniform mat4 u_projViewTrans;\n"
        		+ " \n"
        		+ "varying vec2 v_texCoords0;\n"
        		+ " \n"
        		+ "void main() {\n"
        		+ "    v_texCoords0 = a_texCoord0;\n"
        		+ " \n"
        		+ "    gl_Position = u_projViewTrans * u_worldTrans * vec4(a_position, 1.0);\n"
        		+ "}";
        String frag = "#ifdef GL_ES \n"
        		+ "#define LOWP lowp\n"
        		+ "#define MED mediump\n"
        		+ "#define HIGH highp\n"
        		+ "precision mediump float;\n"
        		+ "#else\n"
        		+ "#define MED\n"
        		+ "#define LOWP\n"
        		+ "#define HIGH\n"
        		+ "#endif\n"
        		+ " \n"
        		+ "varying MED vec2 v_texCoords0;\n"
        		+ " \n"
        		+ "void main() {\n"
        		+ "    vec3 color = vec3(v_texCoords0.x, v_texCoords0.y, 0.0);\n"
        		+ " \n"
        		+ "    gl_FragColor.rgb = color;\n"
        		+ "}";
        return new DefaultShader(renderable, new DefaultShader.Config(vert, frag));
    }
 
}