package net.pzdcrp.Aselia.world.elements.blocks;

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

import net.pzdcrp.Aselia.Hpb;
import net.pzdcrp.Aselia.data.BlockFace;
import net.pzdcrp.Aselia.data.BlockModelBuilder;
import net.pzdcrp.Aselia.data.Pair;
import net.pzdcrp.Aselia.data.Vector3D;
import net.pzdcrp.Aselia.data.MBIM.offset;
import net.pzdcrp.Aselia.extended.SexyMeshBuilder;
import net.pzdcrp.Aselia.utils.ModelUtils;

public class OakLog extends FacingBlock {
	public static String tname = "oaklog";
	public OakLog(Vector3D pos, BlockFace blockface) {
		super(pos, tname, blockface);
	}
	
	@Override
	public void addModel(boolean py, boolean ny, boolean nx, boolean px, boolean nz, boolean pz, BlockModelBuilder mbim) {
		SexyMeshBuilder a = mbim.obtain(pos, this.isTransparent());
		if (!py || !ny || !nx || !px || !nz || !pz) {
			ModelUtils.setTransform(pos);
		    if (blockface == BlockFace.PY || blockface == BlockFace.NY) {
		    	//a texture
		    	Hpb.mutex.hookuvr(a, tname, 0, 0.5f, 1, 1);
		    	if (!py) {
		    		mbim.setCuroffset(offset.py);
		    		ModelUtils.buildTopX(a);//PY
		    	}
		    	if (!ny) {
		    		mbim.setCuroffset(offset.ny);
		    		ModelUtils.buildBottomX(a);//NY
		    	}
		    	//a texture
		    	Hpb.mutex.hookuvr(a, tname, 0, 0, 1, 0.5f);
			    if (!nx) {
			    	mbim.setCuroffset(offset.nx);
			    	ModelUtils.buildLeftY(a);//NX
			    }
			    if (!px) {
			    	mbim.setCuroffset(offset.px);
			    	ModelUtils.buildRightY(a);//PX
			    }
			    if (!nz) {
			    	mbim.setCuroffset(offset.nz);
			    	ModelUtils.buildFrontY(a);//NZ
			    }
			    if (!pz) {
			    	mbim.setCuroffset(offset.pz);
			    	ModelUtils.buildBackY(a);//PZ
			    }
		    } else if (blockface == BlockFace.PZ || blockface == BlockFace.NZ) {
		    	Hpb.mutex.hookuvr(a, tname, 0, 0.5f, 1, 1);
				//a texture
				if (!pz) {
					mbim.setCuroffset(offset.pz);
					ModelUtils.buildBackX(a);
				}
				if (!nz) {
					mbim.setCuroffset(offset.nz);
					ModelUtils.buildFrontX(a);
				}
		    	//a texture
				Hpb.mutex.hookuvr(a, tname, 0, 0, 1, 0.5f);
			    if (!px) {
			    	mbim.setCuroffset(offset.px);
			    	ModelUtils.buildRightPZ(a);
			    }
			    if (!nx) {
			    	mbim.setCuroffset(offset.nx);
			    	ModelUtils.buildLeftZ(a);
			    }
			    if (!py) {
			    	mbim.setCuroffset(offset.py);
			    	ModelUtils.buildTopZ(a);
			    }
			    if (!ny) {
			    	mbim.setCuroffset(offset.ny);
			    	ModelUtils.buildBottomZ(a);
			    }
		    } else if (blockface == BlockFace.NX || blockface == BlockFace.PX) {
		    	Hpb.mutex.hookuvr(a, tname, 0, 0.5f, 1, 1);
				if (!nx) {
					mbim.setCuroffset(offset.nx);
					ModelUtils.buildLeftY(a);
				}
			    if (!px) {
			    	mbim.setCuroffset(offset.px);
			    	ModelUtils.buildRightY(a);
			    }
		    	//a texture
			    Hpb.mutex.hookuvr(a, tname, 0, 0, 1, 0.5f);
			    if (!nz) {
			    	mbim.setCuroffset(offset.nz);
			    	ModelUtils.buildFrontX(a);
			    }
			    if (!pz) {
			    	mbim.setCuroffset(offset.pz);
			    	ModelUtils.buildBackX(a);
			    }
			    if (!py) {
			    	mbim.setCuroffset(offset.py);
			    	ModelUtils.buildTopX(a);
			    }
			    if (!ny) {
			    	mbim.setCuroffset(offset.ny);
			    	ModelUtils.buildBottomX(a);
			    }
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
	
	@Override
	public float getResistance() {
		return 2.5f;
	}
}	

