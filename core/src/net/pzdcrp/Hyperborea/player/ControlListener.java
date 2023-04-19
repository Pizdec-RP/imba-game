package net.pzdcrp.Hyperborea.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.utils.MathU;
import net.pzdcrp.Hyperborea.world.World;
import net.pzdcrp.Hyperborea.world.elements.entities.Entity;
import net.pzdcrp.Hyperborea.world.elements.entities.FallingBlockEntity;
import net.pzdcrp.Hyperborea.world.elements.entities.Particle;

public class ControlListener implements InputProcessor {

	private Hpb gameInstance;

	public ControlListener(Hpb gameInstance) {
		this.gameInstance = gameInstance;
	}
	
	public int curentNumPressed = -1;
	@Override
	public boolean keyDown(int keycode) {
		if (Hpb.world.player.chat.isOpened()) return false;
		
		if (keycode == Input.Keys.M) {
			Gdx.input.setCursorCatched(!Gdx.input.isCursorCatched());
			return true;
		}
		if (keycode == Input.Keys.ENTER) {
			if (!Hpb.world.player.chat.isOpened()) {
				Hpb.world.player.chat.open();
			}
		}
		if (keycode == Input.Keys.F) {
			System.out.println("spawned");
			Vector3D entitypos = Hpb.world.player.getEyeLocation();
			Entity fbe = new FallingBlockEntity(entitypos,1);
			Vector3 camdir = Hpb.world.player.cam.cam.direction.cpy().scl(1.5f);
			fbe.vel.x = camdir.x;
			fbe.vel.y = camdir.y;
			fbe.vel.z = camdir.z;
			Hpb.world.player.curCol.entites.add(fbe);
		}
		if (keycode == Input.Keys.V) {
			Hpb.world.particles.add(new Particle(Hpb.getTexture("firebase"), Hpb.world.player.pos.translate().add(0, 2f, 0), new Vector3(), 120));
		}
		if (keycode == Input.Keys.N) {
			World.renderRad += 1;
		}
		if (keycode == Input.Keys.B) {
			World.renderRad -= 1;
		}
		switch (keycode) {
			case Input.Keys.NUM_1:
	            curentNumPressed = 1;
	            break;
	        case Input.Keys.NUM_2:
	            curentNumPressed = 2;
	            break;
	        case Input.Keys.NUM_3:
	            curentNumPressed = 3;
	            break;
	        case Input.Keys.NUM_4:
	            curentNumPressed = 4;
	            break;
	        case Input.Keys.NUM_5:
	            curentNumPressed = 5;
	            break;
	        case Input.Keys.NUM_6:
	            curentNumPressed = 6;
	            break;
	        case Input.Keys.NUM_7:
	            curentNumPressed = 7;
	            break;
	        case Input.Keys.NUM_8:
	            curentNumPressed = 8;
	            break;
	        case Input.Keys.NUM_9:
	            curentNumPressed = 9;
	            break;
	        case Input.Keys.NUM_0:
	        	curentNumPressed = 10;
	            break;
	        default:
	        	curentNumPressed = -1;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (Gdx.input.isCursorCatched()) {
			Hpb.world.player.handleMM(screenX, screenY);
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		if (Gdx.input.isCursorCatched()) {
			Hpb.world.player.handleMM(screenX, screenY);
			return true;
		}
		return false;
	}

	@Override
	public boolean scrolled(float ax, float ay) {
		//System.out.println(ax+" "+ay);
		if (curentNumPressed+ay < 1) curentNumPressed = 10;
		else if (curentNumPressed+ay > 10) curentNumPressed = 1;
		else curentNumPressed += ay;
		//System.out.println(curentNumPressed);
		return false;
	}

}
