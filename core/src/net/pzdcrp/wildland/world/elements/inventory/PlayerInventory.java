package net.pzdcrp.wildland.world.elements.inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

import net.pzdcrp.wildland.GameInstance;
import net.pzdcrp.wildland.data.MBIM;
import net.pzdcrp.wildland.data.Pair;
import net.pzdcrp.wildland.player.Player;
import net.pzdcrp.wildland.world.elements.blocks.Block;
import net.pzdcrp.wildland.world.elements.entities.Entity;
import net.pzdcrp.wildland.world.elements.inventory.items.Item;
import net.pzdcrp.wildland.world.elements.inventory.items.NoItem;

public class PlayerInventory extends IInventory {
	private Map<Integer,Item> items = new ConcurrentHashMap<Integer,Item>();
	private int chs = 0;//0-9
	
	public PlayerInventory(Entity owner) {
		super(owner);
		for (int i = 0; i < 40; i++) {
			addItem(new NoItem(this), i);
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
	        	modelInstance.transform.set(GameInstance.mCamera.invProjectionView);
	        	modelInstance.transform.rotate(Vector3.X, 35);
	    	    modelInstance.transform.rotate(Vector3.Y, -45);
	    	    modelInstance.transform.rotate(Vector3.Z, 0);
	    	    modelInstance.transform.translate(-.715f+.075f*index, -1.13f, .63f-.075f*index);
	    	    modelInstance.transform.scale(.05f, .10f, .05f);
	    	    hotbar[index] = modelInstance;
        	}
        }
    }
    
    @Override
    public Item getSlot(int index) {
    	return items.get(index);
    }
    
    public static final Texture slot = GameInstance.getTexture("slot");
    public static final Texture selectedSlot = GameInstance.getTexture("sslot");
    public ModelInstance[] hotbar = new ModelInstance[10];
    @Override
    public void render() {
    	//test();
    	for (int i = 0; i < 10; i++) {
    		if (this.chs == i) {
    			GameInstance.spriteBatch.draw(selectedSlot, i*64+3.75f*i, 15, 64, 64);
    		} else {
    			GameInstance.spriteBatch.draw(slot, i*64+3.75f*i, 15, 64, 64);
    		}
    		if (hotbar[i] != null) {
    			//System.out.println("render "+hotbar[i].transform.getTranslation(new Vector3()).toString());
    			GameInstance.modelBatch.render(hotbar[i]);
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
