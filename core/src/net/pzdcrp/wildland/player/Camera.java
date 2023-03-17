package net.pzdcrp.wildland.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;

import net.pzdcrp.wildland.GameInstance;
import net.pzdcrp.wildland.data.Vector3D;

public class Camera {
	public PerspectiveCamera cam;
	public int fov = 67;
	public Vector3 before = new Vector3(0,0,0), now = new Vector3(0,0,0);

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
	
	static final boolean smothmovement = true;
	
	public void render() {
		if (!smothmovement || GameInstance.curCBT > GameInstance.renderCallsBetweenTicks) return;
		if (GameInstance.curCBT == 0 || GameInstance.renderCallsBetweenTicks == 0) {
			cam.position.set(now);
			cam.update();
			return;
		}
		Vector3 offset = new Vector3(now.x-before.x,now.y-before.y,now.z-before.z);
		float mul = (float)GameInstance.curCBT /(float)GameInstance.renderCallsBetweenTicks;
		//System.out.println("offset: "+offset.toString());
		//System.out.println("m: "+mul+" ccbt: "+GameInstance.curCBT+" rcbt: "+GameInstance.renderCallsBetweenTicks);
		cam.position.set(before.x + offset.x*mul, before.y + offset.y*mul, before.z + offset.z*mul);
		//System.out.println("campos: "+cam.position.toString());
		//System.out.println("bef: "+before.toString());
		cam.update();
	}
	
	public void setpos(double x, double y, double z) {//tick
		if (smothmovement) {
			//System.out.println("tick--x:"+x+" y:"+y+" z:"+z);
			before = now;
			now = new Vector3((float)x, (float)y, (float)z);
		} else {
			cam.position.set((float)x, (float)y, (float)z);
			cam.update();
		}
	}
}
