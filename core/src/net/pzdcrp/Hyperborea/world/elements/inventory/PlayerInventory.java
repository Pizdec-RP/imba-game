package net.pzdcrp.Hyperborea.world.elements.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.DamageSource;
import net.pzdcrp.Hyperborea.data.MBIM;
import net.pzdcrp.Hyperborea.data.Pair;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.player.Player;
import net.pzdcrp.Hyperborea.utils.GameU;
import net.pzdcrp.Hyperborea.utils.VectorU;
import net.pzdcrp.Hyperborea.world.elements.blocks.Air;
import net.pzdcrp.Hyperborea.world.elements.blocks.Block;
import net.pzdcrp.Hyperborea.world.elements.entities.Entity;
import net.pzdcrp.Hyperborea.world.elements.inventory.items.Item;
import net.pzdcrp.Hyperborea.world.elements.inventory.items.NoItem;

public class PlayerInventory implements IInventory {
	private Map<Integer,Item> items = new ConcurrentHashMap<Integer,Item>();
	private int chs = 0;//0-9
	private Entity owner;
	public boolean isOpened = false;
	public static final Item EMPTY = new NoItem();
	private Player castedplayer;
	
	public PlayerInventory(Entity owner) {
		for (int i = 0; i < 40; i++) {
			addItem(EMPTY, i);
		}
		this.owner = owner;
		this.castedplayer = (Player) owner;
		font = Hpb.mutex.getFont(20);
		GlyphLayout g = new GlyphLayout();
		g.setText(font, "1234567890");
		fontheight = g.height;
		onResize();
	}
	
	//private MBIM m;
	@Override
    public void addItem(Item item, int index) {
		if (item == null) GameU.end("nullitem");
        if (items.containsKey(index)) {
        	items.replace(index, item);
        } else {
        	items.put(index, item);
        }
    }
	
    @Override
	public void onRClick() {
    	if (owner.currentAimEntity != null) {
    		owner.currentAimEntity.onPlayerClick(castedplayer);
    		return;
    	}
		if (owner.currentAimBlock == null) return;
		Vector3D clickedPos = VectorU.fromFace(
				owner.currentAimBlock.pos,
				owner.currentAimFace
			);
		if (owner.currentAimBlock.onClick(owner)) return;
		getSlot(getCurrentSlotInt()).placeBlockAction(clickedPos, castedplayer);
	}
    
    public void wasteHandItem() {
    	Item handitem = items.get(chs);
    	if (handitem.count == 1) {
    		items.replace(chs, EMPTY);
    	} else {
    		handitem.count--;
    	}
    }
    
    public void setHandItem(Item item) {
    	items.replace(chs, item);
    }
	
	@Override
	@Deprecated
	public void onLClick() {
		if (owner.currentAimEntity != null) {
			owner.currentAimEntity.hit(DamageSource.Hit, getSlot(getCurrentSlotInt()).getDamage());
		} else {
			if (owner.currentAimBlock == null) return;
			Hpb.world.breakBlock(owner.currentAimBlock.pos);
		}
	}
    
	@Override
    public Item getSlot(int index) {
    	return items.get(index);
    }
	
	private BitmapFont font;
	private float fontheight;
	private void displaySlot(int id, float x, float y, float width, float height) {
		Item item = items.get(id);
		if (item instanceof NoItem) return;
		Texture t = item.getTexture();
        Hpb.spriteBatch.draw(t, x, y, width, height);
        font.draw(Hpb.spriteBatch, Integer.toString(item.count), x, y+fontheight);
	}
    
    public static final Texture slot = Hpb.mutex.getOTexture("slot");
    public static final Texture selectedSlot = Hpb.mutex.getOTexture("sslot");
    public static float 
    		x = 0,
    		y = 30,
    		slotWidth = 64f,
    		slotHeight = 64f,
    		spacing = 3f,
    		frameWidth = 10 * (slotWidth + spacing),
    		fullinvyalign = y+slotHeight+5f;
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
            if (i == pickedSlot) continue;
            displaySlot(i, slotX + spacing, slotY + spacing, slotWidth - spacing * 2, slotHeight - spacing * 2);
        }
    }
    
    private int pickedSlot = -1;
    public void onMouseClick(int x, int y, boolean down, int button) {
    	if (!isOpened) return;
    	y = Gdx.graphics.getHeight() - y;
    	int slot = 0;
    	boolean finded = false;
		for (float[] pos : slotposmap) {
			if (x >= pos[0] && x <= pos[2] && y >= pos[1] && y <= pos[3]) {
				finded = true;
				break;
			}
			slot++;
		}
		//System.out.println(x+" "+y+" "+down+" "+button+" "+slot+" "+finded);
		if (!finded) return;
    	if (down) {
    		if (button == Input.Buttons.LEFT) {
				if (pickedSlot == -1) {
					if (!(items.get(slot) instanceof NoItem)) {
						pickedSlot = slot;
					}
				} else {
					mergeItems(pickedSlot, slot, items.get(pickedSlot).count);
					pickedSlot = -1;
				}
				return;
    		}
    	}
    }
    
    public boolean canMerge(Item ifrom) {
    	Item avableitem = null;
    	int nearestempty = -1;
    	for (Entry<Integer, Item> i : items.entrySet()) {
    		if (nearestempty == -1 && i.getValue() instanceof NoItem) {
    			nearestempty = i.getKey();
    		}
    		if (i.getValue().id == ifrom.id && i.getValue().count < i.getValue().stackSize()) {
    			avableitem = i.getValue();
    		}
    	}
    	if (avableitem == null) {
    		if (nearestempty == -1) return false;
    		return true;
    	} else {
    		return true;
    	}
    }
    
    public void mergeFromItemEntity(Item ifrom) {
    	Item avableitem = null;
    	int nearestempty = -1;
    	for (Entry<Integer, Item> i : items.entrySet()) {
    		if (nearestempty == -1 && i.getValue() instanceof NoItem) {
    			nearestempty = i.getKey();
    		}
    		if (i.getValue().id == ifrom.id && i.getValue().count < i.getValue().stackSize()) {
    			avableitem = i.getValue();
    		}
    	}
    	if (avableitem == null) {
    		if (nearestempty == -1) return;
    		items.replace(nearestempty, avableitem = ifrom.clone(0));
    	}
    	if (avableitem != null) {
    		int canmerge = avableitem.stackSize()-avableitem.count;
    		if (ifrom.count <= canmerge) {
    			avableitem.count += ifrom.count;
    			ifrom.count = 0;
    		} else {
    			avableitem.count += canmerge;
    			ifrom.count -= canmerge;
    		}
    	}
    }
    
    public void mergeItems(int from, int to, int count) {
    	Item ito = items.get(to);
    	Item ifrom = items.get(from);
    	if (ito instanceof NoItem) {
    		items.replace(to, ifrom);
    		items.replace(from, EMPTY);
    	} else {
    		if (ito.count >= ito.stackSize()) {
    			//reset pickedSlot 
    		} else {
        		int canmerge = ito.stackSize()-ito.count;
        		if (ifrom.count <= canmerge) {
        			ito.count += ifrom.count;
        			items.replace(from, EMPTY);
        		} else {
        			ito.count += canmerge;
        			ifrom.count -= canmerge;
        			if (ifrom.count < 1) items.replace(from, EMPTY);
        		}
    		}
    	}
    }
    
    List<float[]> slotposmap = new ArrayList<>();
    public void onResize() {
    	slotposmap.clear();
    	for (int i = 0; i < 10; i++) {
            float slotX = x + i * (slotWidth + spacing);
            float slotY = y;
            slotposmap.add(new float[] {slotX, slotY, slotX+slotWidth, slotY+slotHeight});
    	}
    	for (int i = 10; i < 40; i++) {
    		int insideAlignedIndex = i % 10;//0-9
    		int insideAlignedHeightIndex = i / 10;
    		float slotX = x + insideAlignedIndex * (slotWidth + spacing);
            float slotY = fullinvyalign + insideAlignedHeightIndex * (slotHeight + spacing);
            slotposmap.add(new float[] {slotX, slotY, slotX+slotWidth, slotY+slotHeight});
    	}
    }
    
    public void renderFull(int animalign) {
    	for (int i = 10; i < 40; i++) {
    		int insideAlignedIndex = i % 10;//0-9
    		int insideAlignedHeightIndex = i / 10;
    		float slotX = animalign + insideAlignedIndex * (slotWidth + spacing);
            float slotY = fullinvyalign + insideAlignedHeightIndex * (slotHeight + spacing);
            
            Hpb.spriteBatch.draw(slot, slotX, slotY, slotWidth, slotHeight);
            if (i == pickedSlot) continue;
            displaySlot(i, slotX + spacing, slotY + spacing, slotWidth - spacing * 2, slotHeight - spacing * 2);
    	}
    	if (pickedSlot != -1) {
    		float sx = Gdx.input.getX() - slotWidth/2;
    		float sy = (Gdx.graphics.getHeight() - Gdx.input.getY()) - slotHeight/2;
    		displaySlot(pickedSlot, sx + spacing, sy + spacing, slotWidth - spacing * 2, slotHeight - spacing * 2);
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
    
    @Override
    public void dropAllItems() {
		
	}

	@Override
	public Entity getOwner() {
		return owner;
	}
	
	public void open() {
		this.isOpened = true;
		Gdx.input.setCursorCatched(false);
	}
	
	public void close() {
		this.isOpened = false;
		Gdx.input.setCursorCatched(true);
	}

	public JsonElement toJson() {
		JsonObject jinv = new JsonObject();
		JsonArray items = new JsonArray(40);
		for (Entry<Integer, Item> item : this.items.entrySet()) {
			if (item.getValue() instanceof NoItem) continue;
			items.add(item.getValue().toString());
		}
		jinv.add("items", items);
		return jinv;
	}

	public void fromJson(JsonObject jinv) {
		int i = 0;
		for (JsonElement element : jinv.get("items").getAsJsonArray()) {
			Item item = Item.fromString(element.getAsString());
			System.out.println("added item "+item.toString());
			//if (item instanceof NoItem) continue;//debug only
			addItem(item, i);
			i++;
		}
	}
}
