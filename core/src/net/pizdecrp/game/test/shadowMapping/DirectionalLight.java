package net.pizdecrp.game.test.shadowMapping;

import com.badlogic.gdx.math.Vector3;

public class DirectionalLight {
	public Vector3 direction;
	public Vector3 color;
	
	public DirectionalLight(Vector3 direction, Vector3 color) {
		this.direction = direction;
		this.color = color;
	}
}