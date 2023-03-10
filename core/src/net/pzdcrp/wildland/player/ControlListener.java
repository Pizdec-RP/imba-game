package net.pzdcrp.wildland.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

import net.pzdcrp.wildland.GameInstance;
import net.pzdcrp.wildland.world.World;

public class ControlListener implements InputProcessor {

	private GameInstance gameInstance;

	public ControlListener(GameInstance gameInstance) {
		this.gameInstance = gameInstance;
	}

	@Override
	public boolean keyDown(int keycode) {
		if (Gdx.input.isKeyPressed(Input.Keys.M)) {
			Gdx.input.setCursorCatched(!Gdx.input.isCursorCatched());
			return true;
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
			GameInstance.world.player.handleMM(screenX, screenY);
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		if (Gdx.input.isCursorCatched()) {
			GameInstance.world.player.handleMM(screenX, screenY);
			return true;
		}
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		// TODO Auto-generated method stub
		return false;
	}

}
