package net.pzdcrp.Hyperborea.world.elements.blocks;

import java.util.HashMap;
import java.util.Map;

import net.pzdcrp.Hyperborea.data.AABB;
import net.pzdcrp.Hyperborea.data.Vector3D;

public class Liquifyable extends Block {
	public static final Map<Integer, Double> heightMap = new HashMap<>() {{
		put(0, 0d);
		put(1, 0.25d);
		put(2, 0.50d);
		put(3, 0.75d);
		put(4, 1d);
	}};
	
	public int ll;//liquid level
	
	public Liquifyable(Vector3D pos) {
		super(pos, null);
		this.ll = 0;
	}
	
	public Liquifyable(Vector3D pos, String texture, int ll) {
		super(pos, texture, new AABB(pos.x,pos.y,pos.z,pos.x+1,pos.y+heightMap.get(ll),pos.z+1));
		this.ll = ll;
	}
	
	public int liqlide(int cur) {//слияние
		//System.out.println("before:"+this.ll);
		int newlev = cur + this.ll;
		int toreturn = 0;
		if (newlev > 4) {//если выходит за макс
			toreturn = newlev-4;//задаем остаток
			newlev = 4;//ставим новый уровень
		}
		if (newlev != this.ll) {//если он всетаки поменялся
			if (this instanceof Air) {
				world.setBlock(new Water(pos, newlev));
			} else {
				this.ll = newlev;//задаем новый уровень
				this.updateCurrentChunkModel();//и обновляем модель
			}
		}
		//System.out.println("newll:"+this.liquidLevel+" ost:"+toreturn+" onpos:"+pos.toString());
		return toreturn;
	}
	
}
