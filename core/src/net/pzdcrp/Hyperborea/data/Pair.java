package net.pzdcrp.Hyperborea.data;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

public class Pair {
	public final MeshPartBuilder mpb;
	public final ModelBuilder mb;
	public int calls = 1;
	public Pair(MeshPartBuilder one, ModelBuilder two) {
		this.mpb = one;
		this.mb = two;
	}
}
