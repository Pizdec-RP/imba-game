package net.pzdcrp.Hyperborea.world.elements.chat;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class Message {
	public String text;
	public float width, height;
	public Message(GlyphLayout calculator, String text, BitmapFont font) {
		calculator.setText(font, text);
		this.width = calculator.width;
		this.height = calculator.height;
		this.text = text;
		calculator.reset();
	}
}
