package net.pzdcrp.Aselia.player.screens;

public abstract class Screen {

	public Screen() {
		
	}
	
	public void bind() {
		
	}
	
	public void end() {
		
	}

	public abstract void render(int halfwidth, int halfheight);

	public abstract void resize(int width, int height);

}
