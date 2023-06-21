package net.pzdcrp.Hyperborea.utils;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class RenderaU {
	public static Texture resizeTexture(Texture oldTexture, int newWidth, int newHeight) {
		Pixmap oldPixmap = new Pixmap(oldTexture.getWidth(), oldTexture.getHeight(), Format.RGBA8888);
		oldPixmap.drawPixmap(oldTexture.getTextureData().consumePixmap(), 0, 0);
		Pixmap newPixmap = new Pixmap(newWidth, newHeight, Format.RGBA8888);
		newPixmap.drawPixmap(oldPixmap, 
		    0, 0, oldPixmap.getWidth(), oldPixmap.getHeight(), 
		    0, 0, newPixmap.getWidth(), newPixmap.getHeight()
		);
		Texture newTexture = new Texture(newPixmap);
		oldPixmap.dispose();
		newPixmap.dispose();
		return newTexture;
	}
	
	public static Texture flip(Texture tex, boolean fx, boolean fy) {
		Pixmap p = tex.getTextureData().consumePixmap();
        Pixmap flipped = new Pixmap(tex.getWidth(), tex.getHeight(), p.getFormat());

        for (int x = 0; x < tex.getWidth(); x++) {
            for (int y = 0; y < tex.getHeight(); y++) {
                int x1 = fx ? tex.getWidth() - x - 1 : x;
                int y1 = fy ? tex.getHeight() - y - 1 : y;
                flipped.drawPixel(x, y, p.getPixel(x1, y1));
            }
        }
        tex = new Texture(flipped);
        p.dispose();
        flipped.dispose();
        return tex;
	}
}
