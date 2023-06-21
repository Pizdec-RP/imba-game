package net.pzdcrp.Hyperborea.world.elements.chat;

import java.util.LinkedList;
import java.util.Queue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import net.pzdcrp.Hyperborea.Hpb;

public class Chat {
	private boolean opened = false;
	private static final int MAX_MESSAGES = 100;  // Максимальное количество сообщений в чате
    private final Table table;  // Контейнер для сообщений чата
    private final Queue<Label> messages;  // Очередь сообщений чата
    TextField inputField;
	private TypeListener lst;

	public Chat() {
		this.lst = new TypeListener(this);
		this.table = new Table();
		this.table.add(this.inputField).expandX().fillX().row();
        this.table.bottom().left();
        this.table.setFillParent(true);
        this.messages = new LinkedList<>();
        
        TextField.TextFieldStyle style = new TextField.TextFieldStyle();
        style.font = new BitmapFont();
        style.fontColor = Color.WHITE;
        this.inputField = new TextField("", style);
        
        Hpb.stage.addActor(this.table);
	}
	
	public boolean isOpened() {
		return this.opened;
	}
	
	public void openChat() {
		this.opened=true;
		Hpb.multiplexer.addProcessor(lst);
    }

    public void addMessage(String messageText) {
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = new BitmapFont();
        Label message = new Label(messageText, style);

        if (this.messages.size() >= MAX_MESSAGES) {
            Label oldestMessage = this.messages.poll();
            oldestMessage.remove();
        }

        this.messages.add(message);
        this.table.row();
        this.table.add(message);
        if (messageText.startsWith("/")) {
        	Hpb.onCommand(messageText);
        }
    }
    
	public void closeAndSend() {
		System.out.println("closing");
		addMessage(inputField.getText());
		opened = false;
		inputField.setText("");
		Hpb.multiplexer.removeProcessor(lst);
	}
	
	public void close() {
		System.out.println("closing");
		opened = false;
		inputField.setText("");
		Hpb.multiplexer.removeProcessor(lst);
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
		chat.inputField.setText(chat.inputField.getText()+e);
        System.out.println("Key pressed: " + e);
        return true;
    }

	@Override
	public boolean keyDown(int e) {
		if (e == Input.Keys.BACKSPACE) {
			if (chat.inputField.getText().length() != 0) {
				chat.inputField.getText().substring(0, chat.inputField.getText().length() - 1);
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