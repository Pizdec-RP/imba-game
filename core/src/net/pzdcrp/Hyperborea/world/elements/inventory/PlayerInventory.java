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
		}
	}
	
	//private MBIM m;
	@Override
    public void addItem(Item item, int index) {
        if (items.containsKey(index)) {
        	items.replace(index, item);
        } else {
        	items.put(index, item);
        }
        if (!(item instanceof NoItem) && index < 10) {
        	Thotbar[index] = item.getTexture();
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
		if (owner.currentAimEntity != null) {
			owner.currentAimEntity.hit(owner, getSlot(getCurrentSlotInt()).getDamage());
		} else {
			if (owner.currentAimBlock == null) return;
			owner.placeBlock(new Air(owner.currentAimBlock.pos));
		}
	}
    
    @Override
    public Item getSlot(int index) {
    	return items.get(index);
    }
    
    public static final Texture slot = Hpb.mutex.getOTexture("slot");
    public static final Texture selectedSlot = Hpb.mutex.getOTexture("sslot");
    public Texture[] Thotbar = new Texture[10];
    private static float 
    		x = 10,
    		y = 30, 
    		slotWidth = 64f,
    		slotHeight = 64f,
    		spacing = 3.75f;
    @Override
    public void render() {
    	for (int i = 0; i < 10; i++) {
            float slotX = x + i * (slotWidth + spacing);
            float slotY = y;

            if (this.chs == i) {
                Hpb.spriteBatch.draw(selectedSlot, slotX, slotY, slotWidth, slotHeight);
            } else {
                Hpb.spriteBatch.draw(slot, slotX, slotY, slotWidth, slotHeight);
            }

            Texture t = Thotbar[i];
            if (t != null)
                Hpb.spriteBatch.draw(t, slotX + spacing, slotY + spacing, slotWidth - spacing * 2, slotHeight - spacing * 2);
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
