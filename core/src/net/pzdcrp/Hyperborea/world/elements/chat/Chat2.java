package net.pzdcrp.Hyperborea.world.elements.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import net.pzdcrp.Hyperborea.Hpb;

public class Chat2 {
	private List<Message> messages = new CopyOnWriteArrayList<>();
	private BitmapFont font;
	private GlyphLayout glyph;
	private boolean isOpened = false;
	public String inputField = "";
	private InputProcessor lst;
	
	public Chat2() {
		glyph = new GlyphLayout();
		font = Hpb.mutex.getFont(25);
		lst = new TypeListener2(this);
	}
	
	public static float
		x = 0, frameHeight = 200;
	public void render(int halfwidth, int halfheight) {
		int i = 0;
		for (Message msg : messages) {
			float y = halfheight+i*msg.height;
			if (y >= halfheight+frameHeight) {
				messages.remove(msg);
				continue;
			}
			//System.out.println("render "+x+" "+y+" "+msg.text);
			font.draw(Hpb.spriteBatch, msg.text, x, y);
			i++;
		}
	}
	
	public void send(String text) {
		if (text.startsWith("/")) {
        	Hpb.onCommand(text);
        }
		messages.add(0, new Message(glyph, text, font));
	}

	public void closeAndSend() {
		System.out.println("sending '"+inputField+"'");
		send(inputField);
		inputField = "";
		close();
	}
	
	public boolean isOpened() {
		return isOpened;
	}
	
	public void openChat() {
		isOpened = true;
		Hpb.multiplexer.addProcessor(0, lst);
	}

	public void close() {
		isOpened = false;
		Hpb.multiplexer.removeProcessor(lst);
	}
	
}

class TypeListener2 implements InputProcessor {
    private Chat2 chat;

	public TypeListener2(Chat2 chat) {
		this.chat = chat;
	}

	@Override
    public boolean keyTyped(char e) {
		if (!chat.isOpened()) return true;
		chat.inputField = chat.inputField+e;
        System.out.println("Key pressed: " + e);
        return true;
    }

	@Override
	public boolean keyDown(int e) {
		if (e == Input.Keys.BACKSPACE) {
			if (chat.inputField.length() != 0) {
				chat.inputField.substring(0, chat.inputField.length() - 1);
			}
		} else if (e == Input.Keys.ENTER) {
			chat.closeAndSend();
		} else if (e == Input.Keys.ESCAPE) {
			chat.close();
		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		// TODO Auto-generated method stub
		return false;
	}
}