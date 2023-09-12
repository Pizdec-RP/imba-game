package net.pzdcrp.Aselia.data;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

import net.pzdcrp.Aselia.extended.SexyMeshBuilder;
import net.pzdcrp.Aselia.extended.SexyModelBuilder;

public class Pair {
	public SexyMeshBuilder mpb;
	public final SexyModelBuilder mb;
	public int calls = 1;
	public Pair(SexyMeshBuilder one, SexyModelBuilder two) {
		this.mpb = one;
		this.mb = two;
	}
}
