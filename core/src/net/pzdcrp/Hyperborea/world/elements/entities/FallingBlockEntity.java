package net.pzdcrp.Hyperborea.world.elements.entities;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.AABB;
import net.pzdcrp.Hyperborea.data.EntityType;
import net.pzdcrp.Hyperborea.data.MBIM;
import net.pzdcrp.Hyperborea.data.Pair;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.world.elements.blocks.Block;

public class FallingBlockEntity extends Entity {
	
	public int id;
	public ModelInstance model;
	private Map<String, Pair> p;
	private MBIM m;
	
	public FallingBlockEntity(Vector3D pos, int bid) {
		super(pos, new AABB(0,0,0,1,1,1), EntityType.fallingBlock);
		this.id = bid;
		p = new HashMap<String, Pair>();
		m = new MBIM(p,null);//TODO сделать один единственый билдер моделей
		//блоки должны иметь параметр подбираемости
		//такие как вода подбираться не должны а значит создание ентити блока с айди такого блока должно выдавать ошибку
		//должен быть метод в классе блока который будет билдить ModelInstance по вызову метода без аргументов
		//по хорошему вообще не использовать этот тип ентити
		Block.getAbstractBlock(bid).addModel(false,false,false,false,false,false, m);
		Pair firstValue = null;//java moment
		for (Pair value : p.values()) {
		    firstValue = value;
		    break;
		}
		model = new ModelInstance(firstValue.mb.end());
	}
	
	@Override
	public byte maxhp() {
		return Byte.MIN_VALUE;
	}
	
	@Override
	public void render() {
		if (model == null) {
			this.despawn();
			return;
		}
		this.model.transform.setTranslation(Hpb.lerp((float)beforepos.x, (float)pos.x),Hpb.lerp((float)beforepos.y, (float)pos.y),Hpb.lerp((float)beforepos.z, (float)pos.z));
		Hpb.render(model);
	}
	
	@Override
	public void tick() throws Exception {
		super.tick();
		if (this.coly) {
			this.despawn();
			Hpb.world.setBlock(Block.blockById(id, this.beforepos.func_vf()));
			return;
		}
	}
	
}
