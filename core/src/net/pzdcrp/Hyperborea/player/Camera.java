package net.pzdcrp.Hyperborea.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.Settings;
import net.pzdcrp.Hyperborea.data.Vector3D;

public class Camera {
	public PerspectiveCamera cam;
	public float nowfov = Settings.fov, beforefov = Settings.fov;
	public Vector3 before = new Vector3(0,0,0), now = new Vector3(0,0,0);

	public Camera() {
		cam = new PerspectiveCamera(nowfov, 1280, 720);
		cam.position.set(0,0,0);
		
		cam.lookAt(0, 0, 0);
		cam.near = 0.1f;
		
		cam.far = 1000f;
		cam.update();
	}
	
	public void setpos(Vector3D pos) {
		this.setpos(pos.x,pos.y,pos.z);
	}
	
	public Vector3D getDirection() {
		return null;
	}
	
	public void setFov(float fov) {
		cam.fieldOfView = fov;
		/*System.out.println(fov);
		this.beforefov = nowfov;
		nowfov = fov;*/
	}
	
	public float getFov() {
		//return nowfov;
		return cam.fieldOfView;
	}
	Vector3 offset = new Vector3();
	public void render() {
		cam.position.set(Hpb.lerp(before.x, now.x), Hpb.lerp(before.y, now.y), Hpb.lerp(before.z, now.z));
		cam.update();
	}
	
	public void setpos(double x, double y, double z) {
		before = now;
		now = new Vector3((float)x, (float)y, (float)z);
	}
}
