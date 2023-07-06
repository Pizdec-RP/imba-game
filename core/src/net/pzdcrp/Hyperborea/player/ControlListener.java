package net.pzdcrp.Hyperborea.player;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.BlockFace;
import net.pzdcrp.Hyperborea.data.Settings;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.utils.MathU;
import net.pzdcrp.Hyperborea.world.World;
import net.pzdcrp.Hyperborea.world.elements.Particle;
import net.pzdcrp.Hyperborea.world.elements.blocks.OakLog;
import net.pzdcrp.Hyperborea.world.elements.blocks.OakLeaves;
import net.pzdcrp.Hyperborea.world.elements.entities.Entity;
import net.pzdcrp.Hyperborea.world.elements.generators.DefaultWorldGenerator;

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
				Hpb.world.player.chat.openChat();
			}
		}
		if (keycode == Input.Keys.H) {
			Hpb.displayInfo("i like sex!");
		}
		if (keycode == Input.Keys.V) {
			Hpb.world.particles.add(new Particle(Hpb.mutex.getBlockTexture("dirt"), Hpb.world.player.pos.translate().add(0, 2f, 0), new Vector3(), 1200));
		}
		if (keycode == Input.Keys.N) {
			Settings.renderDistance += 1;
			Hpb.world.needToUpdateLoadedColumns = true;
		}
		if (keycode == Input.Keys.B) {
			Settings.renderDistance -= 1;
			Hpb.world.needToUpdateLoadedColumns = true;
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
		}
		if (keycode == Input.Keys.L) {
			DefaultWorldGenerator.generateTree(Hpb.world.player.pos);
		}
		return true;
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
		//TODO Auto-generated method stub
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
		System.out.println(ax+" "+ay);
		curentNumPressed+=ay;
		if (curentNumPressed > 10) {
			curentNumPressed = 1;
		} else if (curentNumPressed < 1) {
			curentNumPressed = 10;
		}
		//System.out.println(curentNumPressed);
		return true;
	}

}
