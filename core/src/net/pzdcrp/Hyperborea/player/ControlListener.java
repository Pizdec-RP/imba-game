package net.pzdcrp.Hyperborea.player;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.AABB;
import net.pzdcrp.Hyperborea.data.BlockFace;
import net.pzdcrp.Hyperborea.data.DamageSource;
import net.pzdcrp.Hyperborea.data.Settings;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.utils.MathU;
import net.pzdcrp.Hyperborea.world.PlayerWorld;
import net.pzdcrp.Hyperborea.world.elements.Particle;
import net.pzdcrp.Hyperborea.world.elements.blocks.OakLog;
import net.pzdcrp.Hyperborea.world.elements.blocks.OakLeaves;
import net.pzdcrp.Hyperborea.world.elements.entities.Entity;
import net.pzdcrp.Hyperborea.world.elements.entities.ItemEntity;
import net.pzdcrp.Hyperborea.world.elements.generators.DefaultWorldGenerator;

public class ControlListener implements InputProcessor {
	private Player p;
	public ControlListener(Player p) {
		this.p = p;
	}
	@Override
	public boolean keyDown(int keycode) {
		if (p.chat.isOpened()) return false;
		if (keycode == Input.Keys.F3) {
			Settings.showHitbox = !Settings.showHitbox;
		}
		if (keycode == Input.Keys.M) {
			Gdx.input.setCursorCatched(!Gdx.input.isCursorCatched());
			return true;
		}
		if (keycode == Input.Keys.T) {
			if (!p.castedInv.isOpened && !p.chat.isOpened()) {
				p.chat.openChat();
			}
		}
		if (keycode == Input.Keys.Y) {
			p.pos.y += 5;
			p.vel.y = 0;
		}
		if (keycode == Input.Keys.G) {
			p.hit(DamageSource.Magic, 2);
		}
		if (keycode == Input.Keys.H) {
			//Hpb.displayInfo("i like sex!");
			//p.teleport(new Vector3D(p.pos.x+12550820/300, 100, 0));
			for (Entity en : Hpb.world.getEntities(p.pos, 99)) {
				if (en instanceof ItemEntity) {
					ItemEntity i = (ItemEntity) en;
					i.vel = i.pos.getDirection(p.pos);
				}
			}
		}
		if (keycode == Input.Keys.V) {
			Hpb.world.particles.add(new Particle(Hpb.mutex.getBlockTexture("dirt"), p.pos.translate().add(0, 2f, 0), new Vector3(), 1200));
		}
		if (keycode == Input.Keys.N) {
			Settings.renderDistance += 1;
			Hpb.world.needToUpdateLoadedColumns = true;
		}
		if (keycode == Input.Keys.B) {
			Settings.renderDistance -= 1;
			Hpb.world.needToUpdateLoadedColumns = true;
		}
		if (keycode == Input.Keys.TAB) {
			if (p.castedInv.isOpened) {
				p.castedInv.close();
			} else {
				p.castedInv.open();
			}
		}
		switch (keycode) {
			case Input.Keys.NUM_1:
	            p.curentNumPressed = 1;
	            break;
	        case Input.Keys.NUM_2:
	        	p.curentNumPressed = 2;
	            break;
	        case Input.Keys.NUM_3:
	        	p.curentNumPressed = 3;
	            break;
	        case Input.Keys.NUM_4:
	        	p.curentNumPressed = 4;
	            break;
	        case Input.Keys.NUM_5:
	        	p.curentNumPressed = 5;
	            break;
	        case Input.Keys.NUM_6:
	        	p.curentNumPressed = 6;
	            break;
	        case Input.Keys.NUM_7:
	        	p.curentNumPressed = 7;
	            break;
	        case Input.Keys.NUM_8:
	        	p.curentNumPressed = 8;
	            break;
	        case Input.Keys.NUM_9:
	        	p.curentNumPressed = 9;
	            break;
	        case Input.Keys.NUM_0:
	        	p.curentNumPressed = 10;
	            break;
		}
		if (keycode == Input.Keys.L) {
			DefaultWorldGenerator.generateTree(p.pos);
		}
		if (keycode == Input.Keys.ESCAPE) {
			if (p.castedInv.isOpened) {
				p.castedInv.close();
			} else {
				System.out.println("выходим без сохранения потмоучто оно не предусмотерно!!!!");
				System.exit(0);
			}
		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (p.castedInv.isOpened) p.castedInv.onMouseClick(screenX, screenY, true, button);
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (p.castedInv.isOpened) p.castedInv.onMouseClick(screenX, screenY, false, button);
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (p.chat.isOpened() || p.castedInv.isOpened) return true;
		if (Gdx.input.isCursorCatched()) {
			p.handleMM(screenX, screenY);
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		if (p.chat.isOpened() || p.castedInv.isOpened) return true;
		if (Gdx.input.isCursorCatched()) {
			p.handleMM(screenX, screenY);
			return true;
		}
		return false;
	}

	@Override
	public boolean scrolled(float ax, float ay) {
		if (p.chat.isOpened() || p.castedInv.isOpened) return true;
		//System.out.println(ax+" "+ay);
		p.curentNumPressed+=ay;
		if (p.curentNumPressed > 10) {
			p.curentNumPressed = 1;
		} else if (p.curentNumPressed < 1) {
			p.curentNumPressed = 10;
		}
		//System.out.println(curentNumPressed);
		return true;
	}

}
