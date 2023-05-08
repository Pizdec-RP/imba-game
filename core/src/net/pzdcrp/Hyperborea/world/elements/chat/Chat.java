package net.pzdcrp.Hyperborea.world.elements.chat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import net.pzdcrp.Hyperborea.Hpb;

public class Chat {
	private boolean opened = false;
	public Array<String> allChat = new Array<String>();
	public Label currentLine;
	public Label label;
	private TypeListener lst;
	private BitmapFont font;

	public Chat() {
		font = new BitmapFont(Gdx.files.classpath("com/badlogic/gdx/utils/lsans-15.fnt"), Gdx.files.classpath("com/badlogic/gdx/utils/lsans-15.png"), false);
		lst = new TypeListener(this);
		label = new Label("", new Label.LabelStyle(font, Color.WHITE));
		label.setPosition(10, Gdx.graphics.getHeight()/2);
		for (int i = 0; i < 15; i++) {
			allChat.add(" \n");
		}
		Hpb.stage.addActor(label);
		currentLine = new Label("", new Label.LabelStyle(font, Color.WHITE));
		Hpb.stage.addActor(currentLine);
	}
	
	public boolean isOpened() {
		return this.opened;
	}
	
	public void open() {
		this.opened = true;
		Hpb.multiplexer.addProcessor(lst);
		System.out.println(Hpb.multiplexer.size());
	}
	
	public void updateChat() {
		StringBuilder s = new StringBuilder();
		if (allChat.size > 15) allChat.removeIndex(0);
		for (String msg : allChat) {
			s.append(msg);
		}
		label.setText(s);
	}
	
	public void send() {
		String text = currentLine.getText().toString().replace("\n", "");
		if (text.equals("")) return;
		System.out.println("nmsg:"+text);
		if (text.startsWith("/")) Hpb.onCommand(text);
		allChat.add("\n"+text);
		
		updateChat();
	}
	
	public void close() {
		System.out.println("closing");
		opened = false;
		currentLine.setText("");
		Hpb.multiplexer.removeProcessor(lst);
		System.out.println(Hpb.multiplexer.size());
	}
}

class TypeListener implements InputProcessor {
    private Chat chat;

	public TypeListener(Chat chat) {
		this.chat = chat;
	}

	@Override
    public boolean keyTyped(char e) {
		if (!chat.isOpened()) return true;
		chat.currentLine.getText().append(e);
        System.out.println("Key pressed: " + e);
        return true;
    }

	@Override
	public boolean keyDown(int e) {
		if (e == Input.Keys.BACKSPACE) {
			if (chat.currentLine.getText().length != 0) {
				chat.currentLine.getText().substring(0, chat.currentLine.getText().length - 1);
			}
		} else if (e == Input.Keys.ENTER) {
			chat.send();
			chat.close();
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