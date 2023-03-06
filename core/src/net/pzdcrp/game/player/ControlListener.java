package net.pzdcrp.game.player;

import com.badlogic.gdx.InputProcessor;

import net.pzdcrp.game.GameInstance;
import net.pzdcrp.game.world.World;

public class ControlListener implements InputProcessor {

	private GameInstance gameInstance;

	public ControlListener(GameInstance gameInstance) {
		this.gameInstance = gameInstance;
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		World.player.handleMM(screenX, screenY);
		return true;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		// TODO Auto-generated method stub
		return false;
	}

}
