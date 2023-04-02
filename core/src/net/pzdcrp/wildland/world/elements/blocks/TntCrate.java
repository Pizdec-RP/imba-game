package net.pzdcrp.wildland.world.elements.blocks;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Vector3;

import net.pzdcrp.wildland.data.BlockFace;
import net.pzdcrp.wildland.data.MBIM;
import net.pzdcrp.wildland.data.Vector3D;
import net.pzdcrp.wildland.player.Player;
import net.pzdcrp.wildland.utils.ModelUtils;
import net.pzdcrp.wildland.world.elements.blocks.Block.BlockType;
import net.pzdcrp.wildland.world.elements.entities.Entity;

public class TntCrate extends Block {
	public static String tname = "tntcrate";
	public TntCrate(Vector3D pos,BlockFace blockface) {
		super(pos,tname);
	}
	
	@Override
	public void addModel(boolean py, boolean ny, boolean nx, boolean px, boolean nz, boolean pz, MBIM mbim) {
		if (!py || !ny || !nx || !px || !nz || !pz) {
			MeshPartBuilder tnttexture = mbim.obtain("tnt", tname);
			ModelUtils.setTransform(pos);
			tnttexture.setUVRange(0, 0, 0.5f, 0.5f);
	    	if (!py) ModelUtils.buildTopX(tnttexture);//PY
	    	//bottom texture
	    	tnttexture.setUVRange(0, 0.5f, 0.5f, 1);
		    if (!nx) ModelUtils.buildLeftPY(tnttexture);//NX
		    if (!px) ModelUtils.buildRightPY(tnttexture);//PX
		    if (!nz) ModelUtils.buildFrontY(tnttexture);//NZ
		    if (!pz) ModelUtils.buildBackY(tnttexture);//PZ
		    //down texture
		    tnttexture.setUVRange(0.5f, 0.5f, 1, 1);
		    if (!ny) ModelUtils.buildBottomX(tnttexture);//NY
		}
	}
	
	@Override
	public BlockType getType() {
		return BlockType.solid;
	}
	
	@Override
	public Block clone() {
		return new TntCrate(this.pos,null);
	}
	
	@Override
	public boolean onClick(Entity actor) {
		if (actor instanceof Player) {
			Player p = (Player)actor;
			if (p.down) {
				return false;
			} else {
				//activate tnt
				return true;
			}
		} else {
			return false;
		}
	}
}
