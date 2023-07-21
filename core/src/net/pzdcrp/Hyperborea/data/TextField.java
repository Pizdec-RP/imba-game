package net.pzdcrp.Hyperborea.data;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TextField {
	private static GlyphLayout glyph = new GlyphLayout();
	private BitmapFont font;
	public float width = 0, height = 0;
	public String text;
	
	/**
	 * GlyphLayout inside :D
	 */
	public TextField(BitmapFont font) {
		this.font = font;
	}
	
	public String setText(String text) {
		this.text = text;
		glyph.setText(font, text);
		width = glyph.width;
		height = glyph.height;
		return text;
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
