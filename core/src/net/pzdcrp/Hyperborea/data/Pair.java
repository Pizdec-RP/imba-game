package net.pzdcrp.Hyperborea.data;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

import net.pzdcrp.Hyperborea.extended.SexyMeshBuilder;
import net.pzdcrp.Hyperborea.extended.SexyModelBuilder;

public class Pair {
	public final SexyMeshBuilder mpb;
	public final SexyModelBuilder mb;
	public int calls = 1;
	public Pair(SexyMeshBuilder one, SexyModelBuilder two) {
		this.mpb = one;
		this.mb = two;
	}
}
