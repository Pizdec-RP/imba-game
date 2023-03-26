package net.pzdcrp.wildland.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;

import net.pzdcrp.wildland.GameInstance;
import net.pzdcrp.wildland.data.Settings;
import net.pzdcrp.wildland.data.Vector3D;

public class Camera {
	public PerspectiveCamera cam;
	public float nowfov = Settings.fov, beforefov = Settings.fov;
	public Vector3 before = new Vector3(0,0,0), now = new Vector3(0,0,0);

	public Camera() {
		cam = new PerspectiveCamera(nowfov, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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
	
	public void render() {
		if (GameInstance.curCBT > GameInstance.renderCallsBetweenTicks) return;
		if (GameInstance.curCBT == 0 || GameInstance.renderCallsBetweenTicks == 0) {
			cam.position.set(now);
			//cam.fieldOfView = nowfov;
			cam.update();
			return;
		}
		Vector3 offset = new Vector3(now.x-before.x,now.y-before.y,now.z-before.z);
		float mul = (float)GameInstance.curCBT /(float)GameInstance.renderCallsBetweenTicks;
		
		/*float fovoffset = nowfov-beforefov;
		cam.fieldOfView = beforefov + fovoffset * mul;
		System.out.println(cam.fieldOfView);*/
		
		cam.position.set(before.x + offset.x*mul, before.y + offset.y*mul, before.z + offset.z*mul);
		cam.update();
	}
	
	public void setpos(double x, double y, double z) {
		before = now;
		now = new Vector3((float)x, (float)y, (float)z);
	}
}
