package net.pzdcrp.wildland.world.elements.entities;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;

import net.pzdcrp.wildland.GameInstance;
import net.pzdcrp.wildland.data.AABB;
import net.pzdcrp.wildland.data.EntityType;
import net.pzdcrp.wildland.data.MBIM;
import net.pzdcrp.wildland.data.Pair;
import net.pzdcrp.wildland.data.Vector3D;
import net.pzdcrp.wildland.world.elements.blocks.Block;

public class FallingBlockEntity extends Entity {
	
	public int id;
	public ModelInstance model;
	public Vector3D visualPos = new Vector3D(0,0,0);
	private Map<String, Pair> p;
	private MBIM m;
	
	public FallingBlockEntity(Vector3D pos, int bid) {
		super(pos, new AABB(0,0,0,1,1,1), EntityType.fallingBlock);
		this.id = bid;
		p.clear();
		p = new HashMap<String, Pair>();
		m = new MBIM(p);
		Block.getAbstractBlock(bid).addModel(false,false,false,false,false,false, m);
		Pair firstValue = null;//java moment
		for (Pair value : p.values()) {
		    firstValue = value;
		    break;
		}
		model = new ModelInstance(firstValue.mb.end());
	}
	
	
	@Override
	public void render(ModelBatch batch) {
		if (model == null) {
			this.despawn();
			return;
		}
		GameInstance.world.lerp(visualPos, beforepos, pos);
		this.model.transform.setTranslation((float)visualPos.x,(float)visualPos.y,(float)visualPos.z);
		batch.render(model);
	}
	
	@Override
	public void tick() {
		System.out.println("------");
		super.tick();
		if (this.coly) {
			this.despawn();
			GameInstance.world.setBlock(Block.blockById(id, this.beforepos.func_vf()));
			return;
		}
	}
	
}
