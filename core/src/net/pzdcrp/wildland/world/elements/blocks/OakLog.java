package net.pzdcrp.wildland.world.elements.blocks;

import java.util.Map;

import net.pzdcrp.wildland.data.BlockFace;
import net.pzdcrp.wildland.data.Pair;
import net.pzdcrp.wildland.data.Vector3D;

public class OakLog extends FacingBlock {
	static String tname = "oaklog";
	
	public OakLog(Vector3D pos, BlockFace blockface) {
		super(pos, tname, blockface);
	}
	
	@Override
	public void addModel(boolean py, boolean ny, boolean nx, boolean px, boolean nz, boolean pz, Map<String, Pair> modelsById) {
		//добавление модели щас сделай
		
	}
	
	@Override
	public BlockType getType() {
		return BlockType.solid;
	}
	
	@Override
	public Block clone() {
		return new OakLog(this.pos,null);
	}
}	

