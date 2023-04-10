package net.pzdcrp.Hyperborea.world.elements.blocks;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.BlockFace;
import net.pzdcrp.Hyperborea.data.MBIM;
import net.pzdcrp.Hyperborea.data.Pair;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.utils.ModelUtils;

public class OakLog extends FacingBlock {
	public static String tname = "oaklog";
	public OakLog(Vector3D pos, BlockFace blockface) {
		super(pos, tname, blockface);
	}
	
	@Override
	public void addModel(boolean py, boolean ny, boolean nx, boolean px, boolean nz, boolean pz, MBIM mbim) {
		MeshPartBuilder top = mbim.obtain("oaktop", tname);
		MeshPartBuilder bottom = mbim.obtain("oakbottom",tname);
		if (!py || !ny || !nx || !px || !nz || !pz) {
			ModelUtils.setTransform(pos);
		    if (blockface == BlockFace.PY || blockface == BlockFace.NY) {
		    	//top texture
				top.setUVRange(0, 0.5f, 1, 1);
		    	if (!py) ModelUtils.buildTopX(top);//PY
		    	if (!ny) ModelUtils.buildBottomX(top);//NY
		    	//bottom texture
			    bottom.setUVRange(0, 0, 1, 0.5f);
			    if (!nx) ModelUtils.buildLeftY(bottom);//NX
			    if (!px) ModelUtils.buildRightY(bottom);//PX
			    if (!nz) ModelUtils.buildFrontY(bottom);//NZ
			    if (!pz) ModelUtils.buildBackY(bottom);//PZ
		    } else if (blockface == BlockFace.PZ || blockface == BlockFace.NZ) {
		    	top.setUVRange(0, 0.5f, 1, 1);
				//top texture
				if (!pz) ModelUtils.buildBackX(top);
				if (!nz) ModelUtils.buildFrontX(top);
		    	//bottom texture
				bottom.setUVRange(0, 0, 1, 0.5f);
			    if (!px) ModelUtils.buildRightPZ(bottom);
			    if (!nx) ModelUtils.buildLeftZ(bottom);
			    if (!py) ModelUtils.buildTopZ(bottom);
			    if (!ny) ModelUtils.buildBottomZ(bottom);
		    } else if (blockface == BlockFace.NX || blockface == BlockFace.PX) {
		    	top.setUVRange(0, 0.5f, 1, 1);
				if (!nx) ModelUtils.buildLeftY(top);
			    if (!px) ModelUtils.buildRightY(top);
		    	//bottom texture
			    bottom.setUVRange(0, 0, 1, 0.5f);
			    if (!nz) ModelUtils.buildFrontX(bottom);
			    if (!pz) ModelUtils.buildBackX(bottom);
			    if (!py) ModelUtils.buildTopX(bottom);
			    if (!ny) ModelUtils.buildBottomX(bottom);
		    }
		}
	}
	
	@Override
	public BlockType getType() {
		return BlockType.solid;
	}
	
	@Override
	public Block clone(Vector3D poss) {
		return new OakLog(poss,this.blockface);
	}
}	

