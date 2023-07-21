package net.pzdcrp.Hyperborea.extended;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import net.pzdcrp.Hyperborea.data.BlockModelBuilder;
import net.pzdcrp.Hyperborea.data.MBIM;
import net.pzdcrp.Hyperborea.data.Vector3D;

public class SexyMeshBuilder extends MeshBuilder {
	private BlockModelBuilder mbim;
	private boolean transparent = true;
	public SexyMeshBuilder(BlockModelBuilder mbim2, boolean transparent) {
		this.mbim = mbim2;
		this.transparent = transparent;
	}

	@Override
	public short vertex (Vector3 pos, Vector3 nor, Color col, Vector2 uv) {
		return super.vertex(pos, nor, col, uv);
	}
	
	@Override
	public void rect (float x00, float y00, float z00, float x10, float y10, float z10, float x11, float y11, float z11, float x01,
		float y01, float z01, float normalX, float normalY, float normalZ) {
		super.rect(x00, y00, z00, x10, y10, z10, x11, y11, z11, x01, y01, z01, normalX, normalY, normalZ);
		int light;
		if (mbim.getChunk() == null)
			return;
		else
			light = mbim.getCurLight();
		if (transparent) {
			mbim.getTlightarray().add(light);
			mbim.getTlightarray().add(light);
			mbim.getTlightarray().add(light);
			mbim.getTlightarray().add(light);
		} else {
			mbim.getSlightarray().add(light);
			mbim.getSlightarray().add(light);
			mbim.getSlightarray().add(light);
			mbim.getSlightarray().add(light);
		}
	}
}
