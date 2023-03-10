package net.pzdcrp.wildland.world.elements.chat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.InputListener;

import net.pzdcrp.wildland.GameInstance;
import net.pzdcrp.wildland.world.World;

public class Chat {

	public boolean isOpen = false;
	public TextInputListener listener;
	public String text = "";
	
	public Chat() {
		
	}
	
	public void open() {
		this.isOpen = true;
		listener = new ChatListener(this);
	}
	
	public void close() {
		isOpen = false;
		text = "";
		listener = null;
	}
	
}

class ChatListener implements TextInputListener {
	Chat chat;
	public ChatListener(Chat MChat) {
		this.chat = MChat;
	}
	@Override
	public void input(String text) {
		this.chat.text = text;
	}
	@Override
	public void canceled() {
		this.chat.close();
	}
}
