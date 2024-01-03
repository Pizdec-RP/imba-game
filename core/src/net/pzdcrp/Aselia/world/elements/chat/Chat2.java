package net.pzdcrp.Aselia.world.elements.chat;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import net.pzdcrp.Aselia.Hpb;
import net.pzdcrp.Aselia.data.Mutex;
import net.pzdcrp.Aselia.data.TextField;
import net.pzdcrp.Aselia.multiplayer.packets.client.ingame.ClientChatPacket;

public class Chat2 {
	private List<Message> messages = new CopyOnWriteArrayList<>();
	private BitmapFont font;
	private GlyphLayout glyph;
	private boolean isOpened = false;
	public String inputField = "";
	private InputProcessor lst;
	public TextField currentWriteLine;
	private boolean callopen;

	public Chat2() {
		glyph = new GlyphLayout();
		font = Hpb.mutex.getFont(25);
		lst = new TypeListener2(this);
		currentWriteLine = new TextField(font);
		resize(Gdx.graphics.getWidth(), 0);
	}

	public void resize(int width, int height) {
		currentWriteLine.setMaxWidth(width);
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
		if (isOpened) {
			currentWriteLine.render(0, currentWriteLine.height+5);
		}
		if (callopen) {
			Hpb.multiplexer.addProcessor(0, lst);
			isOpened = true;
			callopen = false;
		}
	}

	public void debug(String text) {
		send("[debug] "+text);
	}

	public void fromServer(String text) {
		messages.add(0, new Message(glyph, text, font));
	}

	public void send(String text) {
		if (text.length() <= 1) return;
		Hpb.session.send(new ClientChatPacket(text));
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
		currentWriteLine.resetText();
		callopen = true;//фикс бага с буквой t появляющейся при открытии чата
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
		if (!chat.isOpened() || !Mutex.AllowedSymbols.contains(String.valueOf(e))) return false;
		chat.inputField = chat.inputField+e;
        System.out.println("Key typed: " + e);
        chat.currentWriteLine.setText(chat.inputField);
        return true;
    }

	@Override
	public boolean keyDown(int e) {
		if (e == Input.Keys.BACKSPACE) {
			if (chat.inputField.length() != 0) {
				chat.inputField = chat.inputField.substring(0, chat.inputField.length() - 1);
				chat.currentWriteLine.setText(chat.inputField);
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