package net.pzdcrp.Aselia.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import net.pzdcrp.Aselia.Hpb;
import net.pzdcrp.Aselia.data.TextField;
import net.pzdcrp.Aselia.utils.GameU;

public class Button {
	private float width = 160, height = 20;
	private float x, y, textx, texty;
	private float bminx, bminy, bmaxx, bmaxy;
	private TextField text;
	public static Texture texture;
	private boolean clicked = false;

	public Button(String text, float scale, int textsize, boolean autocenter, float x, float y) {
		width *= scale;
		height *= scale;
		if (autocenter) {
			this.x = x - width/2;
			this.y = y - height/2;
			bminx = x - width/2;
			bmaxx = x + width/2;
		} else {
			this.x = x;
			this.y = y;
			bminx = x;
			bmaxx = x + width;
		}
		bmaxy = Gdx.graphics.getHeight() - this.y;
		bminy = bmaxy - height;
		this.text = new TextField(Hpb.mutex.getFont(textsize));
		this.text.setText(text);
		textx = (this.x + width/2) - (this.text.width/2);
		texty = (this.y + height/2) + (this.text.height/2);
	}

	public boolean read() {
		if (clicked) {
			clicked = false;
			return true;
		}
		return false;
	}

	public void render() {
		Hpb.spriteBatch.draw(texture, x, y, width, height);
		text.render(textx, texty);
	}

	public void onClick(int screenX, int screenY, int button) {
		GameU.log("click "+screenX+" "+ screenY);
		GameU.log("bounds "+bminx+" "+bmaxx+" "+bminy+" "+bmaxy);
		if (bminx < screenX && bmaxx > screenX && bminy < screenY && bmaxy > screenY) {
			clicked = true;
		}
	}
}
