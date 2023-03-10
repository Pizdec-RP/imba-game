package net.pzdcrp.wildland.player;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import net.pzdcrp.wildland.GameInstance;
import net.pzdcrp.wildland.data.AABB;
import net.pzdcrp.wildland.data.BlockFace;
import net.pzdcrp.wildland.data.EntityType;
import net.pzdcrp.wildland.data.Physics;
import net.pzdcrp.wildland.data.Settings;
import net.pzdcrp.wildland.data.Vector3D;
import net.pzdcrp.wildland.utils.MathU;
import net.pzdcrp.wildland.world.World;
import net.pzdcrp.wildland.world.elements.Chunk;
import net.pzdcrp.wildland.world.elements.Column;
import net.pzdcrp.wildland.world.elements.blocks.Block;
import net.pzdcrp.wildland.world.elements.blocks.Dirt;
import net.pzdcrp.wildland.world.elements.chat.Chat;
import net.pzdcrp.wildland.world.elements.entities.Entity;
import net.pzdcrp.wildland.world.elements.inventory.items.DirtItem;

public class Player extends Entity {
	//public float pitch, yaw;//yaw left-right
	public Camera cam;
	public boolean forward = false, reverse = false,
			left = false, right = false, 
			up=false,down=false,
			rmb = false, lmb = false,
			run = false;
	private static final float mouseSensitivity = 0.1f;
	private Quaternion quaternion = new Quaternion();
	public int actcd = 0;//15 ticks
	public Chat chat = new Chat();
	
	public Player(double tx, double ty, double tz) {
		super(new Vector3D(tx,ty,tz),new AABB(-0.3, 0, -0.3, 0.3, 1.7, 0.3), EntityType.player);
		cam = new Camera();
		cam.setpos(this.pos.x,this.pos.y,this.pos.z);
		cam.cam.update();
		this.inventory.addItem(new DirtItem(this.inventory), actcd);
	}
	
	public void tick() {
		super.tick();
		if (actcd > 0) actcd--;
		updateControls();
		if (run && cam.cam.fieldOfView < Settings.fov+5) {
			cam.cam.fieldOfView += 1;
		} else {
			if (!run && cam.cam.fieldOfView > Settings.fov) {
				cam.cam.fieldOfView -= 1;
			}
		}
		
		movement();
		
		updatePlayerActions();
		cam.setpos(getEyeLocation());
		//updateCamRotation();
		//System.out.println("x:"+x+" y:"+y+" z:"+z+" wp:"+Gdx.input.isKeyPressed(Input.Keys.W));
	}
	
	public void movement() {
		Vector3D velocityGoal = new Vector3D(0,0,0);
		double speed = run ? Physics.runSpeed : Physics.walkSpeed;
		if (forward) {
			velocityGoal.x = speed;
		}
		if (reverse) {
			velocityGoal.x = -speed;
		}
		if (left) {
			velocityGoal.z = -speed;
		}
		if (right) {
			velocityGoal.z = speed;
		}
		
        velX += velocityGoal.x * (float) Math.sin(quaternion.getYawRad()) + velocityGoal.z * (float) Math.cos(quaternion.getYawRad());
        velZ += velocityGoal.z * (float) Math.sin(quaternion.getYawRad()) - velocityGoal.x * (float) Math.cos(quaternion.getYawRad());
        
		//gravity
        if (up) {
			if (this.onGround) {
				this.velY += 0.6400000013F;
				this.onGround = false;
			}
		}
	}
	
	public void updatePlayerActions() {
		if (rmb && Gdx.input.isCursorCatched()) {
			if (actcd <= 0) {
				Vector3D pos = findFacingBlock(false);
				this.currentAimBlock = pos;//TODO this is ass
				if (pos != null) {
					//GameInstance.world.setBlock(pos, 2);
					this.inventory.getSlot(inventory.currentHitboxSlot()).onLClick();
					actcd = 15;
				}
			}
		}
		if (lmb && Gdx.input.isCursorCatched()) {
			if (actcd <= 0) {
				Vector3D pos = findFacingBlock(true);
				if (pos != null) {
					GameInstance.world.setBlock(pos, 0);
					actcd = 15;
				}
			}
		}
	}
	
	public Vector3D findFacingBlock(boolean solid) {
		List<Vector3D> list = camRay();
		Vector3D last = list.get(0);
		for (Vector3D temppos : list) {
			Block b = GameInstance.world.getBlock(new Vector3D((int)MathU.floorDouble(temppos.x), (int)MathU.floorDouble(temppos.y), (int)MathU.floorDouble(temppos.z)));
			if (solid) {
				if (b.isCollide()) return b.pos;
			} else {
				if (b.isCollide()) return last;
			}
			last = b.pos;
		}
		return null;
	}
	
	public List<Vector3D> camRay() {
		List<Vector3D> l = new ArrayList<>();
		Vector3D dir = Vector3D.translate(cam.cam.direction.cpy().nor()).multiply(0.1);
		Vector3D point = this.getEyeLocation().add(0,0,0);
		for (int i = 0; i < 60; i++) {
			l.add(point);
			point = point.add(dir);
		}
		return l;
	}
	boolean b1 = false;
	public void updateControls() {
		if (Gdx.input.isKeyPressed(Input.Keys.T)) {
			//this.chat.open();
		}
		if (this.chat.isOpen) return;
		if (Gdx.input.isKeyPressed(Input.Keys.W)) {
			forward = true;
		} else forward = false;
		if (Gdx.input.isKeyPressed(Input.Keys.S)) {
			reverse = true;
		} else reverse = false;
		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			left = true;
		} else left = false;
		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			right = true;
		} else right = false;
		if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
			down = true;
		} else down = false;
		if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
			run = true;
		} else run = false;
		if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
			up = true;
		} else up = false;
		if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
			rmb = true;
		} else rmb = false;
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
			lmb = true;
		} else lmb = false;
		if (Gdx.input.isKeyPressed(Input.Keys.R)) {
			if (!b1) {
				GameInstance.world.time += 400;
				b1 = true;
			}
		} else {
			b1 = false;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
			System.out.println("saving");
			GameInstance.exit = true;
			GameInstance.world.save();
			System.out.println("vse");
			System.exit(0);
		}
	}
	
	
	
	public void render() {
		cam.cam.update();
		cam.cam.view.getRotation(quaternion);
        quaternion.nor();
	}
	
	public Vector3D getEyeLocation() {
		return new Vector3D(pos.x, pos.y+1.6f, pos.z);
	}
	
	public float getEyeHeight() {
		return (float) (pos.y+1.6f);
	}
	
	public float getYaw() {
		Matrix4 matrix = new Matrix4();
		matrix.set(quaternion);

		float yaw = (float)Math.atan2(matrix.val[Matrix4.M02], matrix.val[Matrix4.M00]);
		
		return yaw * MathUtils.radiansToDegrees;
    }

    public float getPitch() {
    	Matrix4 matrix = new Matrix4();
    	matrix.set(quaternion);

    	float pitch = (float)Math.atan2(-matrix.val[Matrix4.M12], Math.sqrt(matrix.val[Matrix4.M02] * matrix.val[Matrix4.M02] + matrix.val[Matrix4.M00] * matrix.val[Matrix4.M00]));

    	return pitch * MathUtils.radiansToDegrees;
    }
    
    private int lastCursorX = Gdx.graphics.getWidth() / 2;
    private int lastCursorY = Gdx.graphics.getHeight() / 2;
	
	public void handleMM(int screenX, int screenY) {
		float dy = lastCursorY - screenY;
        float dx = lastCursorX - screenX;

        cam.cam.rotate(Vector3.Y,dx * mouseSensitivity);
        rotatePitch(dy * mouseSensitivity);

        lastCursorX = screenX;
        lastCursorY = screenY;
	}
	
	private final Vector3 tmp = new Vector3();
	
	public void rotatePitch(float step){
        tmp.set(cam.cam.direction).crs(cam.cam.up).nor();

        float currentY = cam.cam.direction.y *90;
        if (Math.abs(currentY+step) > 89.4){
            step = Math.signum(currentY)*89.4f-currentY;
        }

        cam.cam.direction.rotate(tmp, step).nor();
        cam.cam.up.rotate(tmp, step).nor();

    }
}
