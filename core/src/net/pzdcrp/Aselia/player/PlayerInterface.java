package net.pzdcrp.Aselia.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.pzdcrp.Aselia.Hpb;
import net.pzdcrp.Aselia.utils.GameU;
import net.pzdcrp.Aselia.utils.MathU;
import net.pzdcrp.Aselia.world.PlayerWorld;
import net.pzdcrp.Aselia.world.elements.inventory.PlayerInventory;

public class PlayerInterface {
	private Player p;
	private float hpw = 0, hph = 20f, hpx = 0, hpy = 0;
	private Pixmap hppix;
	private byte beforehp = 0;
	private Texture hptex;
	private Texture crosshair;
	private final int crosshairsize = 30;
	
	public PlayerInterface(Player p) {
		this.p = p;
		hppix = new Pixmap(130, 30, Pixmap.Format.RGBA8888);
		crosshair = Hpb.mutex.getOTexture("crosshair");
	}
	
	public void render(int halfwidth, int halfheight) {
		if (Hpb.deadplayer) return;
		if (hptex == null) {
			hptex = new Texture(hppix);//инициализатор вызывается не в потоке рендера
			this.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		}
		if (!p.chat.isOpened()) {
			p.inventory.render();
			Hpb.spriteBatch.draw(hptex, hpx, hpy, hpw, hph);
			if (beforehp != p.hp) {
				redrawhp();
				beforehp = p.hp;
			}
			if (!p.castedInv.isOpened) Hpb.spriteBatch.draw(crosshair, halfwidth-crosshairsize, halfheight-crosshairsize, crosshairsize, crosshairsize);
		}
		if (p.castedInv.isOpened) {
			p.castedInv.renderFull();
		} else {
			p.chat.render(halfwidth, halfheight);
		}
		
		
	}
	
	public void redrawhp() {
		int hpw = (int)this.hpw, hph = (int)this.hph;
		
		hppix.setColor(0,0,0,0.0f);
		hppix.fill();
		
		hppix.setColor(0,0,0,1);
		hppix.fillRectangle(0, 0, hpw, 3);
		hppix.fillRectangle(0, hph - 3, hpw, 3);
		hppix.fillRectangle(0, 0, 3, hph);
		hppix.fillRectangle(hpw - 3, 0, 3, hph);
		
		hppix.setColor(1,1,1,0.3f);
		hppix.fillRectangle(1,1, hpw-1, hph-1);
		
		hppix.setColor(1,0,0,1f);
		int hpinpixels = (int) ((hpw-3) * MathU.norm(0f, p.maxhp(), p.hp));
		
		hppix.fillRectangle(4,3, hpinpixels-3, hph - 5);
		
		hptex.draw(hppix, 0, 0);
	}
	
	public void resize(int width, int height) {
		GameU.log("RESIZE");
		PlayerInventory.x = width / 2 - PlayerInventory.frameWidth / 2;
		hpx = PlayerInventory.x;
		hpy = PlayerInventory.y + PlayerInventory.slotHeight + 5f;
		hpw = (PlayerInventory.slotWidth +PlayerInventory.spacing) * 4;
		hppix.dispose();
		hptex.dispose();
		hppix = new Pixmap((int)hpw, (int)hph, Pixmap.Format.RGBA8888);
		hptex = new Texture(hppix);
		redrawhp();
		p.castedInv.onResize();
	}
	
}
