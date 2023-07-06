package net.pzdcrp.Hyperborea.world.elements.blocks;

import java.util.HashMap;
import java.util.Map;

import net.pzdcrp.Hyperborea.data.AABB;
import net.pzdcrp.Hyperborea.data.ActionAuthor;
import net.pzdcrp.Hyperborea.data.DM;
import net.pzdcrp.Hyperborea.data.Vector3D;

public class Liquifyable extends Block {
	public static final Map<Integer, Float> heightMap = new HashMap<>() {{
		put(1, 0.167f);
		put(2, 0.34f);
		put(3, 0.5f);
		put(4, 0.67f);
		put(5, 0.83f);
		put(6, 1.0f);
		put(7, 1.0f);//источник
	}};
	
	public int ll;//liquid level
	
	public Liquifyable(Vector3D pos) {
		super(pos, null);
		this.ll = 0;
	}
	
	public void setLl(int newll) {
		boolean b0 = this instanceof Air;
		if (newll == 0) {
			if (!b0) world.setBlock(new Air(this.pos), ActionAuthor.world);
			return;
		}
		this.ll = newll;
		for (Block block1 : getSides()) {
			block1.onNeighUpdate();
		}
		if (b0) {
			world.setBlock(new Water(this.pos, this.ll), ActionAuthor.world);
		}
	}
	
	public Liquifyable(Vector3D pos, String texture, int ll) {
		super(pos, texture, new AABB(pos.x,pos.y,pos.z,pos.x+1,pos.y+heightMap.get(ll),pos.z+1));
		this.ll = ll;
	}
	
	/*public int liqlide(int cur) {//слияние
		//System.out.println("before:"+this.ll);
		int newlev = cur + this.ll;
		int toreturn = 0;
		if (newlev > DM.maxll) {//если выходит за макс
			toreturn = newlev-DM.maxll;//задаем остаток
			newlev = DM.maxll;//ставим новый уровень
		}
		if (newlev != this.ll) {//если он всетаки поменялся
			if (this instanceof Air) {
				world.setBlock(new Water(pos, newlev));
			} else {
				this.ll = newlev;//задаем новый уровень
				this.callChunkUpdate();//и обновляем модель
			}
		}
		//System.out.println("newll:"+this.liquidLevel+" ost:"+toreturn+" onpos:"+pos.toString());
		return toreturn;
	}*/
	
}
