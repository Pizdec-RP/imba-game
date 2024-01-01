package net.pzdcrp.Aselia.data;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;

import net.pzdcrp.Aselia.Hpb;

public class TextField {
	private static GlyphLayout glyph = new GlyphLayout();
	private BitmapFont font;
	public float width = 0, height = 0;
	public String text;
	private int maxwidth;

	public TextField(BitmapFont font) {
		this.font = font;
	}

	public String setText(String text) {
		this.text = text;
		if (maxwidth == 0) {
			glyph.setText(font, text);
		} else {
			glyph.setText(font, text, 0, maxwidth, font.getColor(), 0, Align.left, false, null);
		}
		width = glyph.width;
		height = glyph.height;
		return text;
	}
	
	/**
	 * @param maxwidth
	 * 0 - unlimited width
	 */
	public void setMaxWidth(int maxwidth) {
		this.maxwidth = maxwidth;
	}
	
	public void render(float x, float y) {
		font.draw(Hpb.spriteBatch, text,x,y);
	}

	public void render(SpriteBatch batch, float x, float y) {
		font.draw(batch, text,x,y);
	}

	public void render(SpriteBatch batch, float x, float y, float r, float g, float b, float a) {
		Color c = font.getColor();
		float br = c.r, bg = c.g, bb = c.b, ba = c.a;
		c.set(r,g,b,a);
		font.draw(batch, text,x,y);
		c.set(br,bg,bb,ba);
	}
}
