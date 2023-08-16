package net.pzdcrp.Hyperborea.world.elements.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.DamageSource;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.multiplayer.packets.ClientInventoryActionPacket;
import net.pzdcrp.Hyperborea.multiplayer.packets.ServerSetSlotPacket;
import net.pzdcrp.Hyperborea.player.Player;
import net.pzdcrp.Hyperborea.utils.GameU;
import net.pzdcrp.Hyperborea.utils.VectorU;
import net.pzdcrp.Hyperborea.world.elements.inventory.items.Item;
import net.pzdcrp.Hyperborea.world.elements.inventory.items.NoItem;

public class PlayerInventory implements IInventory {
	private Map<Integer,Item> items = new ConcurrentHashMap<Integer,Item>();
	private int chs = 0;//0-9
	private Player owner;
	public boolean isOpened = false;
	public static final Item EMPTY = new NoItem();
	
	public PlayerInventory(Player owner) {
		for (int i = -1; i < 40; i++) {
			addItem(EMPTY, i);
		}
		this.owner = owner;
		if (owner.world.isLocal()) {
			font = Hpb.mutex.getFont(20);
			GlyphLayout g = new GlyphLayout();
			g.setText(font, "1234567890");
			fontheight = g.height;
			onResize();
		}
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
	
	/**client side*/
	public void setSlotFromPacketOnClient(int index, Item item) {
		items.replace(index, item);
	}
	
	/**server side*/
	public void setSlotOnServer(int index, Item item) {
		items.replace(index, item);
		owner.sendSelfPacket(new ServerSetSlotPacket(index, item));
	}
	
    @Override
	public void onRClick() {
    	if (owner.currentAimEntity != null) {
    		owner.currentAimEntity.onPlayerClick(owner);
    		return;
    	}
		if (owner.currentAimBlock == null) return;
		Vector3D clickedPos = VectorU.fromFace(
				owner.currentAimBlock.pos,
				owner.currentAimFace
			);
		if (owner.currentAimBlock.onClick(owner)) return;
		getSlot(getCurrentSlotInt()).placeBlockAction(clickedPos, owner);
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
            displaySlot(i, slotX + spacing, slotY + spacing, slotWidth - spacing * 2, slotHeight - spacing * 2);
        }
    }
    
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
		if (!finded || down) return;
		GameU.log("sent inv action slot: "+slot+" down: "+down+" button: "+button);
		Hpb.session.send(new ClientInventoryActionPacket(slot, down, button));
    }
    
    /**server side*/
    public void doActionByPacketOnServer(int slot, boolean down, int button) {
    	GameU.log("got inv action slot: "+slot+" down: "+down+" button: "+button);
    	Item cursoritem = items.get(-1);
    	boolean cursorIsEmpty = cursoritem.id == 0;
    	Item islot = items.get(slot);
    	if (down) {
    		if (button == Input.Buttons.LEFT) {
    			if (islot.id == 0) {
	    			if (cursorIsEmpty) {//левый клик по пустому слоту с пустым курсором
	    				GameU.log("1");
	    				//ни к чему не приводит
	    			} else {//левый клик по пустому слоту с заполненым курсором (ложим стак из курсора в слот)
	    				GameU.log("2");//протестировано
	    				this.setSlotOnServer(slot, cursoritem);//перекидываем item з курсора в нажатый слот
	    				this.setSlotOnServer(-1, EMPTY);//очищаем курсор
	    			}
    			} else {
    				if (cursorIsEmpty) {//левый клик по заполненому слоту с пустым курсором (берем стак в курсор)
    					GameU.log("3");//протестировано
    					setSlotOnServer(-1, islot);//ложим в курсор item
    					setSlotOnServer(slot, EMPTY);//очищаем нажатый слот
    				} else {//левый клик по заполненому слоту с заполненым курсором
    					if (cursoritem.id == islot.id) {//в слотах одинаковые ресурсы
    						GameU.log("4.0");
    						int можноПоложить = islot.stackSize() - islot.count;
    						if (можноПоложить == 0) {//нельзя ничо положить (меняем местами ресурсы)
    							GameU.log("4.1");
    							setSlotOnServer(-1, islot);//в курсор ложим item из слота
        						setSlotOnServer(slot, cursoritem);//в слот ложим item из курсора
    						} else if (можноПоложить >= cursoritem.count) {//можно положить все что есть в курсоре
    							GameU.log("4.2");
    							islot.count += cursoritem.count;//прибавляем из курсора в слот
    							setSlotOnServer(slot, islot);///обновляем у клиента (если вызовет ошибку то юзать clone на islot)
    							setSlotOnServer(-1, EMPTY);//очищаем курсор так как все перемещено в слот
    						} else {//можно положить только часть из курсора
    							GameU.log("4.3");
    							cursoritem.count -= можноПоложить; //убираем эту часть из курсора
    							setSlotOnServer(-1, cursoritem);//обновляем у клиента
    							islot.count += можноПоложить;//добавляем эту часть в слот
    							setSlotOnServer(slot, islot);//обновляем у клиента
    						}
    					} else {//в слотах разные ресурсы (меняем местами ресурсы)
    						GameU.log("5");//протестировано
    						setSlotOnServer(-1, islot);//в курсор ложим item из слота
    						setSlotOnServer(slot, cursoritem);//в слот ложим item из курсора
    					}
    				}
    			}
    		}
    	}
	}
    
    
    List<float[]> slotposmap = new ArrayList<>();
    /**client side*/
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
    /**client side*/
    public void renderFull(int animalign) {
    	for (int i = 10; i < 40; i++) {
    		int insideAlignedIndex = i % 10;//0-9
    		int insideAlignedHeightIndex = i / 10;
    		float slotX = animalign + insideAlignedIndex * (slotWidth + spacing);
            float slotY = fullinvyalign + insideAlignedHeightIndex * (slotHeight + spacing);
            
            Hpb.spriteBatch.draw(slot, slotX, slotY, slotWidth, slotHeight);
            displaySlot(i, slotX + spacing, slotY + spacing, slotWidth - spacing * 2, slotHeight - spacing * 2);
    	}
    	if (items.get(-1).id != 0) {
    		float sx = Gdx.input.getX() - slotWidth/2;
    		float sy = (Gdx.graphics.getHeight() - Gdx.input.getY()) - slotHeight/2;
    		displaySlot(-1, sx + spacing, sy + spacing, slotWidth - spacing * 2, slotHeight - spacing * 2);
    	}
    }
    
    public boolean canMerge(Item ifrom) {
    	Item avableitem = null;
    	int nearestempty = -2;
    	for (int i = 0; i < 40; i++) {
    		Item item = items.get(i);
    		if (nearestempty == -1 && item instanceof NoItem) {
    			nearestempty = i;
    		}
    		if (item.id == ifrom.id && item.count < item.stackSize()) {
    			avableitem = item;
    		}
    	}
    	if (avableitem == null) {
    		if (nearestempty == -2) return false;
    		return true;
    	} else {
    		return true;
    	}
    }
    
    public boolean mergeFromItemEntity(Item ifrom) {
    	Item avableitem = null;
    	int nearestempty = -2;
    	for (int i = 0; i < 40; i++) {
    		Item item = items.get(i);
    		if (nearestempty == -2 && item instanceof NoItem) {
    			nearestempty = i;
    		}
    		if (item.id == ifrom.id && item.count < item.stackSize()) {
    			avableitem = item;
    		}
    	}
    	if (avableitem == null) {
    		if (nearestempty == -2) return false;
    		this.setSlotOnServer(nearestempty, avableitem = ifrom.clone(0));
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
    	return true;
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
		GameU.end("under construction");
	}

	@Override
	public Player getOwner() {
		return owner;
	}
	/**client side*/
	public void open() {
		this.isOpened = true;
		Gdx.input.setCursorCatched(false);
	}
	/**client side*/
	public void close() {
		this.isOpened = false;
		Gdx.input.setCursorCatched(true);
	}

	public JsonElement toJson() {
		JsonObject jinv = new JsonObject();
		JsonArray items = new JsonArray();
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
	
	public Map<Integer, Item> getItems() {
		return items;
	}
	
	/**client side*/
	public void setItems(Map<Integer, Item> items) {
		this.items = items;
	}
}

/*
    
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
    
    public void transferSlotOnServerByPacket(int from, int to) {
		if (from == to) GameU.end("same slots. from: "+from+" to: "+to);
		Item ifrom = items.get(from);
		if (ifrom.id == 0) return;//clicked in empty slot
		Item ito = items.get(to);
		setSlotOnServer(to, ifrom);
		if (ito.id != 0) {
			setSlotOnServer(from, ito);
		} else {
			setSlotOnServer(from, EMPTY);
		}
	}
	
	public void transferSlotOnClient(int from, int to) {
		if (from == to) GameU.end("same slots. from: "+from+" to: "+to);
		Item ifrom = items.get(from);
		if (ifrom.id == 0) return;//clicked in empty slot
		Item ito = items.get(to);
		items.replace(to, ifrom);//посылать пакеты не нужноы
		if (ito.id != 0) {
			items.replace(from, ito);//посылать пакеты не нужно
		} else {
			items.replace(from, EMPTY);//посылать пакеты не нужно
		}
		Hpb.session.send(new ClientTransferSlotPacket(from, to));
	}
 */
