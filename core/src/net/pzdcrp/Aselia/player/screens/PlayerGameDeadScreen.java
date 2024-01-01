package net.pzdcrp.Aselia.player.screens;

import com.badlogic.gdx.Gdx;

import net.pzdcrp.Aselia.Hpb;
import net.pzdcrp.Aselia.data.TextField;
import net.pzdcrp.Aselia.ui.Button;

public class PlayerGameDeadScreen extends Screen {
	private TextField text;
	private float textx = 50, texty = 50;
	private Button respawn;
	
	public PlayerGameDeadScreen() {
		
	}
	
	@Override
	public void bind() {
		text = new TextField(Hpb.mutex.getFont(40));
		text.setText("ты сдох");
		this.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Hpb.controls.forceIgnore = true;
		Gdx.input.setCursorCatched(false);
		Hpb.controls.processedButtons.add(respawn);
	}
	
	@Override
	public void end() {
		Hpb.controls.forceIgnore = false;
		Gdx.input.setCursorCatched(true);
		Hpb.controls.processedButtons.remove(respawn);
	}

	@Override
	public void render(int halfwidth, int halfheight) {
		text.render(textx, texty);
		respawn.render();
		if (respawn.read()) {
			Hpb.world.player.respawn();
			Hpb.changeScreen(new PlayerInGameHudScreen());
		}
	}

	@Override
	public void resize(int width, int height) {
		texty = height/2 - text.height/2 + 40;
		textx = width/2 - text.width/2;
		respawn = new Button("возродиться", 2.5f, 40, true, width/2, height/2-40);
	}

}
