package net.pzdcrp.Aselia;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

public class rendertest implements Screen {
    private FrameBuffer frameBuffer;
    private Texture originalTexture, newTexture;
    private SpriteBatch spriteBatch;

    public rendertest() {
        // Создание объектов FrameBuffer, Texture и SpriteBatch
        frameBuffer = new FrameBuffer(Pixmap.Format.RGB888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        originalTexture = new Texture("original.png");
        newTexture = new Texture(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Pixmap.Format.RGB888);
        spriteBatch = new SpriteBatch();
    }

    // Отрисовка текстуры newTexture
    @Override
    public void render(float delta) {
        frameBuffer.begin();
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        spriteBatch.begin();
        spriteBatch.draw(originalTexture, 0, 0);
        spriteBatch.end();
        frameBuffer.end();
        TextureRegion textureRegion = new TextureRegion(frameBuffer.getColorBufferTexture());
        textureRegion.flip(false, true);
        spriteBatch.begin();
        spriteBatch.draw(textureRegion, 0, 0);
        spriteBatch.end();
    }

    // Копирование содержимого FrameBuffer на текстуру newTexture
    private void copyFrameBufferToTexture() {
        spriteBatch.setProjectionMatrix(spriteBatch.getProjectionMatrix().idt());
        spriteBatch.setTransformMatrix(spriteBatch.getTransformMatrix().idt());
        spriteBatch.begin();
        spriteBatch.draw(frameBuffer.getColorBufferTexture(), 0, 0, newTexture.getWidth(), newTexture.getHeight());
        spriteBatch.end();
    }

    // Вызов метода copyFrameBufferToTexture() для копирования содержимого FrameBuffer на newTexture
    private void copyToNewTexture() {
        frameBuffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        newTexture.draw(new Pixmap(frameBuffer.getWidth(), frameBuffer.getHeight(), Pixmap.Format.RGB888),0,0);
        copyFrameBufferToTexture();
    }

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}
}