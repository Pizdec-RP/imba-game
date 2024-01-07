package net.pzdcrp.Aselia.data;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.math.Vector2;

import net.pzdcrp.Aselia.Hpb;
import net.pzdcrp.Aselia.utils.GameU;
import net.pzdcrp.Aselia.utils.RenderaU;

public class AnimatedTexture {
	private byte framesCount = 0, currentFrame = 0;
	private int speedMillis = 0;
	private Texture[] frames;
	private Pixmap[] framesp;
	private long lastExecutionTime = System.currentTimeMillis();
	public String key;
	private Vector2I pos;
	
	public AnimatedTexture(Texture mainTexture, String parameters, String key) {
		String[] paramlist = parameters.split(" ");
		framesCount = Byte.parseByte(paramlist[0]);
		speedMillis = Integer.parseInt(paramlist[1]);
		int splitHeight = mainTexture.getHeight() / framesCount;
		frames = splitTexture(mainTexture, splitHeight);
		framesp = new Pixmap[framesCount];
		for (byte i = 0; i < framesCount; i++) {
			framesp[i] = RenderaU.clonePixmap(frames[i].getTextureData().consumePixmap());
		}
		this.key = key;
	}

	public void tick() {
		long currentTime = System.currentTimeMillis();
        if (currentTime - lastExecutionTime >= speedMillis) {
        	currentFrame++;
        	if (currentFrame == framesCount) currentFrame = 0;
        	
        	//setextrue
        	if (!Hpb.mutex.updated) Hpb.mutex.clearPixmap();
        	Hpb.mutex.pixmap.drawPixmap(framesp[currentFrame], pos.x, pos.z);
        	
            lastExecutionTime = currentTime;
            Hpb.mutex.updated = true;
            //GameU.log("updated!");
        }
	}
	
	public Texture getDefault() {
		return frames[0];
	}
	
	private static Texture[] splitTexture(Texture originalTexture, int splitHeight) {
        TextureData textureData = originalTexture.getTextureData();

        if (!textureData.isPrepared()) {
            textureData.prepare();
        }

        Pixmap originalPixmap = textureData.consumePixmap();

        int originalWidth = originalPixmap.getWidth();
        int originalHeight = originalPixmap.getHeight();

        int numRegions = (int) Math.ceil((float) originalHeight / splitHeight);

        Texture[] textureRegions = new Texture[numRegions];
        
        int regionHeight = originalHeight / numRegions;

        for (int i = 0; i < numRegions; i++) {

            Pixmap regionPixmap = new Pixmap(originalWidth, regionHeight, originalPixmap.getFormat());
            regionPixmap.drawPixmap(originalPixmap, 0, 0, 0, i * splitHeight, originalWidth, regionHeight);

            //region.flip(false, true); // Flip vertically to match LibGDX coordinate system
            textureRegions[i] = new Texture(regionPixmap);

            //regionPixmap.dispose();
        }

        originalPixmap.dispose();

        return textureRegions;
    }

	public void setPosition(int x, int y) {
		this.pos = new Vector2I(x,y);
	}
}
