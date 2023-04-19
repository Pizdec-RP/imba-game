package net.pzdcrp.Hyperborea.world.elements.inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.MBIM;
import net.pzdcrp.Hyperborea.data.Pair;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.player.Player;
import net.pzdcrp.Hyperborea.utils.VectorU;
import net.pzdcrp.Hyperborea.world.elements.blocks.Air;
import net.pzdcrp.Hyperborea.world.elements.blocks.Block;
import net.pzdcrp.Hyperborea.world.elements.entities.Entity;
import net.pzdcrp.Hyperborea.world.elements.inventory.items.Item;
import net.pzdcrp.Hyperborea.world.elements.inventory.items.NoItem;

public class PlayerInventory extends IInventory {
	private Map<Integer,Item> items = new ConcurrentHashMap<Integer,Item>();
	private int chs = 0;//0-9
	
	public PlayerInventory(Entity owner) {
		super(owner);
		for (int i = 0; i < 40; i++) {
			addItem(new NoItem(this), i);
			if (i < 10) {
				Bhotbar[i] = 0;
			}
		}
		p = new HashMap<String, Pair>();
		m = new MBIM(p);
	}
	
	private Map<String, Pair> p;
	private MBIM m;
	@Override
    public void addItem(Item item, int index) {
        if (items.containsKey(index)) {
        	items.replace(index, item);
        } else {
        	items.put(index, item);
        }
        if (index < 10) {
        	if (item.isModel()) {
	        	Block n = Block.blockByItem(item);
	        	if (n.isRenderable()) {
	        		p.clear();
	        		n.addModel(false, false, false, false, false, false, m);
	        		Pair firstValue = null;//java moment
	        		for (Pair value : p.values()) {
	        		    firstValue = value;
	        		    break;
	        		}
		        	ModelInstance modelInstance = new ModelInstance(firstValue.mb.end());
		        	modelInstance.transform.set(Hpb.mCamera.invProjectionView);
		        	modelInstance.transform.rotate(Vector3.X, 35);
		    	    modelInstance.transform.rotate(Vector3.Y, -45);
		    	    modelInstance.transform.rotate(Vector3.Z, 0);
		    	    modelInstance.transform.translate(-.715f+.075f*index, -1.13f, .63f-.075f*index);
		    	    modelInstance.transform.scale(.05f, .10f, .05f);
		    	    modelInstance.userData = new Object[] {"noshader"};
		    	    Mhotbar[index] = modelInstance;
		    	    Bhotbar[index] = 1;
	        	}
        	} else {
        		Thotbar[index] = item.getTexture();
        		Bhotbar[index] = 2;
        	}
        }
    }
	
	@Override
	public void onRClick() {
		if (owner.currentAimBlock == null) return;
		Vector3D clickedPos = VectorU.fromFace(
				owner.currentAimBlock.pos,
				owner.currentAimFace
			);
		if (owner.currentAimBlock.onClick(owner)) return;
		getSlot(getCurrentSlotInt()).onRClick(clickedPos);
	}
	
	@Override
	public void onLClick() {
		System.out.println(111111);
		if (owner.currentAimEntity != null) {
			owner.currentAimEntity.hit(owner, getSlot(getCurrentSlotInt()).getDamage());
		} else {
			System.out.println(22222);
			if (owner.currentAimBlock == null) return;
			System.out.println(3333333);
			owner.placeBlock(new Air(owner.currentAimBlock.pos));
		}
	}
    
    @Override
    public Item getSlot(int index) {
    	return items.get(index);
    }
    
    public static final Texture slot = Hpb.getTexture("slot");
    public static final Texture selectedSlot = Hpb.getTexture("sslot");
    public ModelInstance[] Mhotbar = new ModelInstance[10];
    public Texture[] Thotbar = new Texture[10];
    public int[] Bhotbar = new int[10];
    @Override
    public void render() {
    	//test();
    	for (int i = 0; i < 10; i++) {
    		if (this.chs == i) {
    			Hpb.spriteBatch.draw(selectedSlot, i*64+3.75f*i, 15, 64, 64);
    		} else {
    			Hpb.spriteBatch.draw(slot, i*64+3.75f*i, 15, 64, 64);
    		}
    		if (Bhotbar[i] == 1) {
    			//System.out.println("render "+hotbar[i].transform.getTranslation(new Vector3()).toString());
    			Hpb.modelBatch.render(Mhotbar[i]);
    		} else if (Bhotbar[i] == 2) {
    			Hpb.spriteBatch.draw(Thotbar[i], i*64+3.75f*i, 15, 64, 64);
    		}
    	}
    }
    
    @Override
    public int getCurrentSlotInt() {
		return chs;
	}
    
    @Override
    public void setCurrentSlotInt(int i) {
		chs = i;
	}
}
