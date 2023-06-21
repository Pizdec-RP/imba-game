package net.pzdcrp.Hyperborea.world.elements.blocks;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.google.gson.JsonObject;

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.AABB;
import net.pzdcrp.Hyperborea.data.DM;
import net.pzdcrp.Hyperborea.data.MBIM;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.data.MBIM.offset;
import net.pzdcrp.Hyperborea.extended.SexyMeshBuilder;
import net.pzdcrp.Hyperborea.utils.ModelUtils;

public class Water extends Liquifyable {
	public static String tname = "water";
	
	public Water(Vector3D pos, int liquidLevel) {
		super(pos, tname, liquidLevel);
	}
	
	int beforell = this.ll;
	public boolean ableToTick = true;
	private int tickcd = 20;
	@Override
	public void tick() {
		if (!ableToTick) return;
		if (tickcd > 0) {
			tickcd--;
			return;
		}
		beforell = this.ll;
		System.out.println("tick pos: "+this.pos.toString());
		tickcd = 10;
		//ищем предисточник
		Block up = world.getBlock(pos.add(0, 1, 0));
		if (up instanceof Water) {
			if (this.ll != DM.maxll) this.setLl(DM.maxll-1);
		} else {
			boolean b0 = false;
			byte maxlvl = -1;
			if (this.ll == DM.maxll) {//если этот блок и есть источник
				b0 = true;
			} else {//если нет то продолжаем искать
				Block[] var = new Block[] {
					world.getBlock(pos.add(1, 0, 0)),
					world.getBlock(pos.add(-1, 0, 0)),
					world.getBlock(pos.add(0, 0, 1)),
					world.getBlock(pos.add(0, 0, -1))};
				for (Block temp : var) {
					if (temp instanceof Water) {
						Water tw = (Water) temp;
						if (tw.ll == (this.ll+1)) {//источник найден и он на 1 лвл больше этого блока 
							b0 = true;
							//ableToTick=false;//он должен растечься в этом тике а в следующем стать неактивным
						} else if (tw.ll > (this.ll+1)) {//если лвл гораздо больше то сглаживаем этот блок относительно источника
							if (maxlvl < tw.ll) maxlvl = (byte)tw.ll;
							//ableToTick=false;//он должен растечься в этом тике а в следующем стать неактивным
						}
					}
				}
			}
			if (this.ll != DM.maxll) {//если этот блок не источник и источника поблизости нет то убавляем нопемногу уровень
				if (!b0) {//
					setLl(this.ll-1);
					this.callChunkUpdate();
				} else {// если же источник есть и есть рядом с ним блок с большим уровнем то надо на 1 лвл меньше этого блкоа сделать
					if (maxlvl != -1) {
						setLl(maxlvl-1);
						this.callChunkUpdate();
					}//если блока с лвл больше не найдено то выше уже делается обработка при источнике или без него
				}
			}
		}
		boolean leakedUnder = false;
		//вода течет в стороны, перменные назначеные выше не должны использоваться
		Block under = world.getBlock(pos.add(0, -1, 0));
		if (under instanceof Liquifyable) {
			Liquifyable lu = (Liquifyable)under;
			if (lu.ll != DM.maxll-1) {
				lu.setLl(DM.maxll-1);
				this.callChunkUpdate();
				leakedUnder = true;
			}
		}
		if (this.ll == DM.maxll || (!leakedUnder && this.ll != DM.maxll)) {
			Block[] neighbors = new Block[] {
					world.getBlock(pos.add(1, 0, 0)),
					world.getBlock(pos.add(-1, 0, 0)),
					world.getBlock(pos.add(0, 0, 1)),
					world.getBlock(pos.add(0, 0, -1))};
			for (Block n : neighbors) {//чекаем возможность для текучести и хуярим
				if (n instanceof Liquifyable) {
					Liquifyable ln = (Liquifyable) n;
					if (ln.ll < this.ll-1) {//блоки рядом должны быть на 1 лвл меньше своего источника
						ln.setLl(this.ll-1);
						ln.callChunkUpdate();
					}
				}
			}
		}
		if (beforell == this.ll) {
			ableToTick=false;
		}
	}
	
	@Override
	public void onNeighUpdate() {
		this.ableToTick = true;
	}
	
	@Override
	public void addModel(boolean py, boolean ny, boolean nx, boolean px, boolean nz, boolean pz, MBIM mbim) {
		SexyMeshBuilder a = mbim.obtain(pos);
		ModelUtils.setTransform(pos);
		float h = heightMap.get(ll).floatValue();
		Hpb.mutex.hookuvr(a, tname, 0, 0, 1, 1);
		Block temp;
		
		if (!ny) {
			mbim.curoffset = offset.ny;
			ModelUtils.buildBottomX(a);
		}
		
		temp = world.getBlock(new Vector3D(pos.x,pos.y+1,pos.z));
		if ((temp instanceof Water && ll != 4) || !py) {
			if (h==1.0f) {
				mbim.curoffset = offset.no;
			} else {
				mbim.curoffset = offset.py;
			}
			a.rect(
			ModelUtils.sp.x, ModelUtils.sp.y+h, ModelUtils.sp.z+1f,
			ModelUtils.sp.x, ModelUtils.sp.y+h, ModelUtils.sp.z,
			ModelUtils.sp.x+1f, ModelUtils.sp.y+h, ModelUtils.sp.z,
			ModelUtils.sp.x+1f, ModelUtils.sp.y+h, ModelUtils.sp.z+1f,
			0, 1, 0);
		}
		Hpb.mutex.hookuvr(a, tname, 0, 0, 1, h);
		temp = world.getBlock(new Vector3D(pos.x-1,pos.y,pos.z));
		if (!nx) {
			mbim.curoffset = offset.nx;
			a.rect(
			ModelUtils.sp.x, ModelUtils.sp.y, ModelUtils.sp.z+1f,
			ModelUtils.sp.x, ModelUtils.sp.y, ModelUtils.sp.z,
			ModelUtils.sp.x, ModelUtils.sp.y+h, ModelUtils.sp.z,
			ModelUtils.sp.x, ModelUtils.sp.y+h, ModelUtils.sp.z+1f,
	        -1, 0, 0);
		} else {
			if (temp instanceof Water && ((Water)temp).ll < this.ll) {
				Water side = (Water)temp;
				mbim.curoffset = offset.nx;
				a.rect(
				ModelUtils.sp.x, ModelUtils.sp.y+Liquifyable.heightMap.get(side.ll), ModelUtils.sp.z+1f,
				ModelUtils.sp.x, ModelUtils.sp.y+Liquifyable.heightMap.get(side.ll), ModelUtils.sp.z,
				ModelUtils.sp.x, ModelUtils.sp.y+h, ModelUtils.sp.z,
				ModelUtils.sp.x, ModelUtils.sp.y+h, ModelUtils.sp.z+1f,
		        -1, 0, 0);
			}
		}
		
		temp = world.getBlock(new Vector3D(pos.x+1,pos.y,pos.z));
		if (!px) {
			mbim.curoffset = offset.px;
			a.rect(
			ModelUtils.sp.x+1f, ModelUtils.sp.y, ModelUtils.sp.z,
			ModelUtils.sp.x+1f, ModelUtils.sp.y, ModelUtils.sp.z+1f,
			ModelUtils.sp.x+1f, ModelUtils.sp.y+h, ModelUtils.sp.z+1f,
			ModelUtils.sp.x+1f, ModelUtils.sp.y+h, ModelUtils.sp.z,
	         1, 0, 0);
		} else {
			if (temp instanceof Water && ((Water)temp).ll < this.ll) {
				mbim.curoffset = offset.px;
				Water side = (Water)temp;
				a.rect(
				ModelUtils.sp.x+1f, ModelUtils.sp.y+Liquifyable.heightMap.get(side.ll), ModelUtils.sp.z,
				ModelUtils.sp.x+1f, ModelUtils.sp.y+Liquifyable.heightMap.get(side.ll), ModelUtils.sp.z+1f,
				ModelUtils.sp.x+1f, ModelUtils.sp.y+h, ModelUtils.sp.z+1f,
				ModelUtils.sp.x+1f, ModelUtils.sp.y+h, ModelUtils.sp.z,
		         1, 0, 0);
			}
		}
		
		temp = world.getBlock(new Vector3D(pos.x,pos.y,pos.z-1));
		if (!nz) {
			mbim.curoffset = offset.nz;
			a.rect(
			ModelUtils.sp.x, ModelUtils.sp.y, ModelUtils.sp.z,
			ModelUtils.sp.x+1f, ModelUtils.sp.y, ModelUtils.sp.z,
			ModelUtils.sp.x+1f, ModelUtils.sp.y+h, ModelUtils.sp.z,
			ModelUtils.sp.x, ModelUtils.sp.y+h, ModelUtils.sp.z,
	        0, 0, -1);
		} else {
			if (temp instanceof Water && ((Water)temp).ll < this.ll) {
				mbim.curoffset = offset.nz;
				Water side = (Water)temp;
				a.rect(
				ModelUtils.sp.x, ModelUtils.sp.y+Liquifyable.heightMap.get(side.ll), ModelUtils.sp.z,
				ModelUtils.sp.x+1f, ModelUtils.sp.y+Liquifyable.heightMap.get(side.ll), ModelUtils.sp.z,
				ModelUtils.sp.x+1f, ModelUtils.sp.y+h, ModelUtils.sp.z,
				ModelUtils.sp.x, ModelUtils.sp.y+h, ModelUtils.sp.z,
		        0, 0, -1);
			}
		}
		
		temp = world.getBlock(new Vector3D(pos.x,pos.y,pos.z+1));
		if (!pz) {
			mbim.curoffset = offset.pz;
			a.rect(
			ModelUtils.sp.x+1f, ModelUtils.sp.y, ModelUtils.sp.z+1f,
			ModelUtils.sp.x, ModelUtils.sp.y, ModelUtils.sp.z+1f,
			ModelUtils.sp.x, ModelUtils.sp.y+h, ModelUtils.sp.z+1f,
			ModelUtils.sp.x+1f, ModelUtils.sp.y+h, ModelUtils.sp.z+1f,
	         0, 0, 1);
		} else {
			if (temp instanceof Water && ((Water)temp).ll < this.ll) {
				mbim.curoffset = offset.pz;
				Water side = (Water)temp;
				a.rect(
				ModelUtils.sp.x+1f, ModelUtils.sp.y+Liquifyable.heightMap.get(side.ll), ModelUtils.sp.z+1f,
				ModelUtils.sp.x, ModelUtils.sp.y+Liquifyable.heightMap.get(side.ll), ModelUtils.sp.z+1f,
				ModelUtils.sp.x, ModelUtils.sp.y+h, ModelUtils.sp.z+1f,
				ModelUtils.sp.x+1f, ModelUtils.sp.y+h, ModelUtils.sp.z+1f,
		         0, 0, 1);
			}
		}
	}
	
	@Override
	public Block clone(Vector3D poss) {
		return new Water(poss,ll);
	}
	
	@Override
	public BlockType getType() {
		return BlockType.transparent;
	}
	
	@Override
	public boolean tickable() {
		return ableToTick;
	}
	
	@Override
	public boolean equals(Object block) {
		if (block instanceof Block) {
			Block b = (Block) block;
			if (block.getClass() == this.getClass() && b.getFace() == this.getFace() && this.ll == ((Water)block).ll) return true;
		}
		return false;
	}
	
	@Override
	public JsonObject toJson() {
		if (!ableToTick) return null;
		JsonObject j = new JsonObject();
		j.addProperty("id", Block.idByBlock(this));
		j.addProperty("a", this.ableToTick);
		return j;
	}
	
	@Override
	public void fromJson(JsonObject data) {
		this.ableToTick = data.get("a").getAsBoolean();
	}
}
