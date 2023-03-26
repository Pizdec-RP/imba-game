package net.pzdcrp.wildland.world.elements.blocks;

import java.util.Map;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import net.pzdcrp.wildland.GameInstance;
import net.pzdcrp.wildland.data.BlockFace;
import net.pzdcrp.wildland.data.Pair;
import net.pzdcrp.wildland.data.Vector3D;
import net.pzdcrp.wildland.utils.ModelUtils;

public class OakLog extends FacingBlock {
	public static String tname = "oaklog";
	public OakLog(Vector3D pos, BlockFace blockface) {
		super(pos, tname, blockface);
	}
	
	@Override
	public void addModel(boolean py, boolean ny, boolean nx, boolean px, boolean nz, boolean pz, Map<String, Pair> modelsById) {
		if (!modelsById.containsKey(String.valueOf("oaktop"))) {
			ModelBuilder mb = new ModelBuilder();
			mb.begin();
			modelsById.put(
					"oaktop",
					new Pair(
							mb.part(
								"cube", 
								GL20.GL_TRIANGLES, 
								VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates,
								new Material(
							    		TextureAttribute.createDiffuse(GameInstance.getTexture(tname)),
						    			IntAttribute.createCullFace(GL20.GL_FRONT),
						    			new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
							    )
							),
							mb
					)
			);
		}
		if (!modelsById.containsKey(String.valueOf("oakbottom"))) {
			ModelBuilder mb = new ModelBuilder();
			mb.begin();
			modelsById.put(
					"oakbottom",
					new Pair(
							mb.part(
								"cube", 
								GL20.GL_TRIANGLES, 
								VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates,
								new Material(
							    		TextureAttribute.createDiffuse(GameInstance.getTexture(tname)),
						    			IntAttribute.createCullFace(GL20.GL_FRONT),
						    			new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
							    )
							),
							mb
					)
			);
		}
		if (!py || !ny || !nx || !px || !nz || !pz) {
			Vector3 pos = this.pos.floor().add(0.5).translate();
			Pair pair;
		    if (blockface == BlockFace.PY || blockface == BlockFace.NY) {
		    	//top texture
				pair = modelsById.get("oaktop");
				pair.mpb.setUVRange(0, 0.5f, 1, 1);
				ModelUtils.setTransform(pos);
		    	if (!py) ModelUtils.buildTopX(pair.mpb);//PY
		    	if (!ny) ModelUtils.buildBottomX(pair.mpb);//NY
		    	//bottom texture
			    pair = modelsById.get("oakbottom");
			    pair.mpb.setUVRange(0, 0, 1, 0.5f);
			    ModelUtils.setTransform(pos);
			    if (!nx) ModelUtils.buildLeftY(pair.mpb);//NX
			    if (!px) ModelUtils.buildRightY(pair.mpb);//PX
			    if (!nz) ModelUtils.buildFrontY(pair.mpb);//NZ
			    if (!pz) ModelUtils.buildBackY(pair.mpb);//PZ
		    } else if (blockface == BlockFace.PZ || blockface == BlockFace.NZ) {
		    	pair = modelsById.get("oaktop");
		    	pair.mpb.setUVRange(0, 0.5f, 1, 1);
				ModelUtils.setTransform(pos);
				//top texture
				if (!pz) ModelUtils.buildBackX(pair.mpb);
				if (!nz) ModelUtils.buildFrontX(pair.mpb);
		    	//bottom texture
			    pair = modelsById.get("oakbottom");
			    pair.mpb.setUVRange(0, 0, 1, 0.5f);
			    ModelUtils.setTransform(pos);
			    if (!px) ModelUtils.buildRightZ(pair.mpb);
			    if (!nx) ModelUtils.buildLeftZ(pair.mpb);
			    if (!py) ModelUtils.buildTopZ(pair.mpb);
			    if (!ny) ModelUtils.buildBottomZ(pair.mpb);
		    } else if (blockface == BlockFace.NX || blockface == BlockFace.PX) {
		    	pair = modelsById.get("oaktop");
		    	pair.mpb.setUVRange(0, 0.5f, 1, 1);
				ModelUtils.setTransform(pos);
				if (!nx) ModelUtils.buildLeftY(pair.mpb);
			    if (!px) ModelUtils.buildRightY(pair.mpb);
		    	//bottom texture
			    pair = modelsById.get("oakbottom");
			    pair.mpb.setUVRange(0, 0, 1, 0.5f);
			    ModelUtils.setTransform(pos);
			    if (!nz) ModelUtils.buildFrontX(pair.mpb);
			    if (!pz) ModelUtils.buildBackX(pair.mpb);
			    if (!py) ModelUtils.buildTopX(pair.mpb);
			    if (!ny) ModelUtils.buildBottomX(pair.mpb);
		    }
		}
	}
	
	@Override
	public BlockType getType() {
		return BlockType.solid;
	}
	
	@Override
	public Block clone() {
		return new OakLog(this.pos, this.blockface);
	}
}	

