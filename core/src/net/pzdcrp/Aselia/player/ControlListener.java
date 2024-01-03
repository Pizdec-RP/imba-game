package net.pzdcrp.Aselia.player;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;

import net.pzdcrp.Aselia.Hpb;
import net.pzdcrp.Aselia.data.Settings;
import net.pzdcrp.Aselia.data.Vector3D;
import net.pzdcrp.Aselia.multiplayer.packets.client.ingame.ClientPlayerActionPacket;
import net.pzdcrp.Aselia.multiplayer.packets.client.ingame.ClientPlayerActionPacket.PlayerAction;
import net.pzdcrp.Aselia.server.InternalServer;
import net.pzdcrp.Aselia.ui.Button;
import net.pzdcrp.Aselia.utils.GameU;
import net.pzdcrp.Aselia.world.elements.Particle;
import net.pzdcrp.Aselia.world.elements.generators.DefaultWorldGenerator;
import net.pzdcrp.Aselia.world.elements.inventory.HandCraftingGUI;

public class ControlListener implements InputProcessor {
	private Player p;
	public boolean forceIgnore = false;
	public List<Button> processedButtons = new CopyOnWriteArrayList<>();

	public ControlListener(Player p) {
		this.p = p;
	}

	@Override
	public boolean keyDown(int keycode) {
		if (forceIgnore || p.chat.isOpened()) return false;
		if (keycode == Input.Keys.Q) {
			if (p.castedInv.getHandItem().id == 0) return false;
			if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
				Hpb.session.send(new ClientPlayerActionPacket(PlayerAction.DropItemStack, null));
			} else {
				Hpb.session.send(new ClientPlayerActionPacket(PlayerAction.DropItem, null));
			}
		}
		if (keycode == Input.Keys.F3) {
			Settings.debug = !Settings.debug;
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
		if (keycode == Input.Keys.H) {
			Hpb.displayInfo("i like sex!");
			p.teleport(new Vector3D(p.pos.x+12550820/300, 100, 0));
			/*for (Entity en : Hpb.world.getEntities(p.pos, 99)) {
				if (en instanceof ItemEntity) {
					ItemEntity i = (ItemEntity) en;
					i.vel = i.pos.getDirection(p.pos);
				}
			}*/
		}
		if (keycode == Input.Keys.V) {
			Hpb.world.particles.add(new Particle(Hpb.mutex.getBlockTexture("dirt"), p.pos.translate().add(0, 2f, 0), new Vector3(), 1200));
		}
		if (keycode == Input.Keys.N) {
			Settings.streamZone += 1;
			GameU.log("renderDistance cannot be changed while you in game");
		}
		if (keycode == Input.Keys.B) {
			Settings.streamZone -= 1;
			GameU.log("renderDistance cannot be changed while you in game");
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
	            p.castedInv.setCurrentSlotInt(0);
	            break;
	        case Input.Keys.NUM_2:
	        	p.castedInv.setCurrentSlotInt(1);
	            break;
	        case Input.Keys.NUM_3:
	        	p.castedInv.setCurrentSlotInt(2);
	            break;
	        case Input.Keys.NUM_4:
	        	p.castedInv.setCurrentSlotInt(3);
	            break;
	        case Input.Keys.NUM_5:
	        	p.castedInv.setCurrentSlotInt(4);
	            break;
	        case Input.Keys.NUM_6:
	        	p.castedInv.setCurrentSlotInt(5);
	            break;
	        case Input.Keys.NUM_7:
	        	p.castedInv.setCurrentSlotInt(6);
	            break;
	        case Input.Keys.NUM_8:
	        	p.castedInv.setCurrentSlotInt(7);
	            break;
	        case Input.Keys.NUM_9:
	        	p.castedInv.setCurrentSlotInt(8);
	            break;
	        case Input.Keys.NUM_0:
	        	p.castedInv.setCurrentSlotInt(9);
	            break;
		}
		if (keycode == Input.Keys.L) {
			DefaultWorldGenerator.generateTree(p.pos);
		}
		if (keycode == Input.Keys.ESCAPE) {
			if (p.castedInv.isOpened) {
				p.castedInv.close();
			} else {
				if (InternalServer.world.save())
					System.exit(0);
				else
					p.chat.debug("error while saving world");
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
		p.castedInv.onMouseClick(screenX, screenY, true, button);
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		//if (p.castedInv.isOpened) p.castedInv.onMouseClick(screenX, screenY, false, button);
		for (Button b : processedButtons) {
			b.onClick(screenX, screenY, button);
		}
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (forceIgnore || !Gdx.input.isCursorCatched()) return false;
		p.handleMM(screenX, screenY);
		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		if (forceIgnore || !Gdx.input.isCursorCatched()) return false;
		p.handleMM(screenX, screenY);
		return true;
	}

	@Override
	public boolean scrolled(float ax, float ay) {
		if (forceIgnore || p.chat.isOpened()) return false;
		if (p.castedInv.isOpened && p.castedInv.openedStorage == null) {
			HandCraftingGUI.scroll(ay);
			return true;
		}

		int cur = p.castedInv.getCurrentSlotInt();
		cur+=ay;

		if (cur > 9) cur = 0;
		else if (cur < 0) cur = 9;

		p.castedInv.setCurrentSlotInt(cur);
		return true;
	}

}
