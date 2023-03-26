
package net.pizdecrp.game.test.shadowMapping;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class ShadowMappingTest extends ApplicationAdapter {

	private PerspectiveCamera cam;
	private CameraInputController camController;
	//private AssetManager assets;
	//private Model model;
	private Array<ModelInstance> instances = new Array<ModelInstance>();
	//private ModelInstance modelInstance;
	//private boolean loading;
	private ModelBatch modelBatch;
	private ShadowShader SP;
	
	//The directional light and the shadow map
	private DirectionalLight dLight;
	private PerspectiveCamera dLightCam;
	private FrameBuffer fboDLight;
	private int DEPTH_SIZE = 1024;
	private Texture depthMap;
	private DLightShader dLightSP;
	
	
	private Model sphere;
	private ModelInstance sphereInstance;
	public Model square;
	public ModelInstance squareInstance;
	public Mesh squareMesh;
	
	private Model plane;
	private ModelInstance planeInstance;
	

		
	
	@Override
	public void create () {
		modelBatch = new ModelBatch();
		//The camera
		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(255.f,255.f,-2.54f);
		cam.lookAt(0,0,0);
		cam.near = 1f;
		cam.far = 1000f;
		cam.update();
		
		//loading = true;
		

		
		//assets = new AssetManager();
		//assets.load("sponza/sponza.g3db", Model.class);
		
		SP = new ShadowShader();
		SP.init();
		
		//Setting up the Directional Light
		//Here : DEPTH_SIZE = 1024
		dLight = new DirectionalLight(new Vector3(-0.7f, -0.7f, 0.0f), new Vector3(0.8f, 0.8f, 0.8f));
		dLightCam = new PerspectiveCamera(67, DEPTH_SIZE, DEPTH_SIZE);
		dLightCam.position.set(500f,500f,-2.54f);
		dLightCam.lookAt(new Vector3(0.0f, 0.0f, 0.0f));
		dLightCam.far = 1000f;
		dLightCam.update();
		
		depthMap = new Texture(DEPTH_SIZE, DEPTH_SIZE, Pixmap.Format.RGBA8888);
		fboDLight = new FrameBuffer(Pixmap.Format.RGB888, DEPTH_SIZE, DEPTH_SIZE, true);
		dLightSP = new DLightShader();
		dLightSP.init();
		
		camController = new CameraInputController(dLightCam);
		Gdx.input.setInputProcessor(camController);
		
		ModelBuilder modelBuilder = new ModelBuilder();
		Matrix4 scale = new Matrix4(new Vector3(0.0f, 0.0f,0.0f), new Quaternion(), new Vector3(0.1f,0.1f,0.1f));
		sphere = modelBuilder.createSphere(50f, 50f, 50f, 5, 5, new Material(IntAttribute.createCullFace(GL20.GL_NONE)), Usage.Position | Usage.Normal | Usage.TextureCoordinates);
		sphereInstance = new ModelInstance(sphere, scale);
	    plane = modelBuilder.createBox(500.0f, 2.0f, 500.0f, new Material(new ColorAttribute(ColorAttribute.Diffuse, Color.GREEN), IntAttribute.createCullFace(GL20.GL_NONE)), Usage.Position| Usage.Normal | Usage.TextureCoordinates);
	    planeInstance = new ModelInstance(plane, scale);
	    instances.add(planeInstance);
		instances.add(sphereInstance);
	}

    private void doneLoading() {
		/*model = assets.get("sponza/sponza.g3db", Model.class);
		Matrix4 scale = new Matrix4(new Vector3(0.0f, 0.0f,0.0f), new Quaternion(), new Vector3(0.1f,0.1f,0.1f));
		modelInstance = new ModelInstance(model, scale);
		instances.add(modelInstance);
        loading = false;*/
    }
	
	@Override
	public void render () {
		/*if(loading && assets.update())
			doneLoading();*/
		
		//Here we control the directional light
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
			dLightCam.position.x += 5;
		}
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
        	dLightCam.position.x -= 5;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.UP)){
        	dLightCam.position.y += 5;
		}
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
        	dLightCam.position.y -= 5;
        }
		camController.update();
			
        //Render the shadow map
		fboDLight.begin();
		
		Gdx.gl.glViewport(0, 0, DEPTH_SIZE, DEPTH_SIZE);
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT|GL30.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glEnable(GL30.GL_DEPTH_TEST);

		modelBatch.begin(dLightCam);
		modelBatch.render(instances, dLightSP);
		modelBatch.end();
		
		fboDLight.end();
		
		depthMap = fboDLight.getColorBufferTexture();

		
		//Render the scene
        SP.setDepthMap(depthMap);
        SP.setLightViewProj(dLightCam);
        modelBatch.begin(cam);
        modelBatch.render(instances, SP);
        modelBatch.end();

	}
	
	@Override
	public void dispose () {
		modelBatch.dispose();
	}
}