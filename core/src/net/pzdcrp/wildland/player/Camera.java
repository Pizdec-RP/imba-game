package net.pzdcrp.wildland.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;

import net.pzdcrp.wildland.data.Vector3D;

public class Camera {
	public PerspectiveCamera cam;
	public int fov = 67;

	public Camera() {
		cam = new PerspectiveCamera(fov, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(0,0,0);
		cam.lookAt(0, 0, 0);
		cam.near = 0.1f;
		
		cam.far = 500f;
		//cam.viewportWidth = 200;
	    //cam.viewportHeight = 150;
		cam.update();
		
	}
	
	public void setpos(Vector3D pos) {
		this.setpos(pos.x,pos.y,pos.z);
	}
	
	public Vector3D getDirection() {
		return null;
	}
	
	public void setpos(double x, double y, double z) {
		cam.position.set((float)x,(float)y,(float)z);
		cam.update();
	}
}
