package net.pzdcrp.Aselia.world.elements.inventory;

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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.pzdcrp.Aselia.Hpb;
import net.pzdcrp.Aselia.data.BlockFace;
import net.pzdcrp.Aselia.data.DamageSource;
import net.pzdcrp.Aselia.data.TextField;
import net.pzdcrp.Aselia.data.Vector3D;
import net.pzdcrp.Aselia.multiplayer.packets.client.ingame.ClientClickBlockPacket;
import net.pzdcrp.Aselia.multiplayer.packets.client.ingame.ClientPlaceBlockPacket;
import net.pzdcrp.Aselia.multiplayer.packets.client.ingame.ClientSetHotbarSlotPacket;
import net.pzdcrp.Aselia.multiplayer.packets.client.inventory.ClientCloseInventoryPacket;
import net.pzdcrp.Aselia.multiplayer.packets.client.inventory.ClientInventoryActionPacket;
import net.pzdcrp.Aselia.multiplayer.packets.client.inventory.ClientOpenPlayerInventoryPacket;
import net.pzdcrp.Aselia.multiplayer.packets.server.inventory.ServerSetSlotPacket;
import net.pzdcrp.Aselia.multiplayer.packets.server.inventory.ServerSetupInventoryPacket;
import net.pzdcrp.Aselia.player.Player;
import net.pzdcrp.Aselia.utils.GameU;
import net.pzdcrp.Aselia.utils.MathU;
import net.pzdcrp.Aselia.utils.VectorU;
import net.pzdcrp.Aselia.world.elements.entities.Entity;
import net.pzdcrp.Aselia.world.elements.entities.ItemEntity;
import net.pzdcrp.Aselia.world.elements.inventory.items.Item;
import net.pzdcrp.Aselia.world.elements.inventory.items.NoItem;
import net.pzdcrp.Aselia.world.elements.storages.ItemStorage;

public class PlayerInventory implements IInventory {
	private Map<Integer,Item> items = new ConcurrentHashMap<>();
	public ItemStorage openedStorage; //link
	private int chs = 0;//0-9
	private Player owner;
	public boolean isOpened = false;
	public static final Item EMPTY = new NoItem();
	public HandCraftingGUI craftboard;

	public PlayerInventory(Player owner) {
		for (int i = -1; i < 40; i++) {
			items.put(i, EMPTY);
		}
		this.owner = owner;
		craftboard = new HandCraftingGUI(this);
		if (owner.world.isLocal()) {
			font = Hpb.mutex.getFont(20);
			currentText = new TextField(font);
			GlyphLayout g = new GlyphLayout();
			g.setText(font, "1234567890");
			fontheight = g.height;
			onResize();
		}
	}

	/**client side*/
	public void setSlotFromPacketOnClient(int index, Item item) {
		if (index >= 60) {
			openedStorage.setFromPacket(index, item);
		} else {
			items.replace(index, item);
		}
	}

	/**server side*/
	public void setSlotOnServer(int index, Item item) {
		if (index >= 60) {
			openedStorage.setSlotSilentOnServer(index, item, owner.nickname);
		} else {
			items.replace(index, item);
		}
		owner.sendSelfPacket(new ServerSetSlotPacket(index, item));
	}

    @Override
	public void onRClick() {
    	if ((owner.currentAimEntity != null) || (owner.currentAimBlock == null)) return;
		Vector3D clickedPos = VectorU.fromFace(
				owner.currentAimBlock.pos,
				owner.currentAimFace
			);
		if (owner.currentAimBlock.clickable() && !owner.down) {
			Hpb.session.send(new ClientClickBlockPacket(owner.currentAimBlock.pos));
		} else {
			Hpb.session.send(new ClientPlaceBlockPacket(clickedPos, owner.currentAimFace, owner.currentaimpoint));
		}
	}

    public void onRClick(Vector3D pos, BlockFace face, Vector3D origin) {
    	getSlot(getCurrentSlotInt()).placeBlockAction(pos, face, origin, owner);
    }

    /**server side*/
    public void wasteHandItem() {
    	Item handitem = items.get(chs);
    	if (handitem.count == 1) {
    		setSlotOnServer(chs, EMPTY);
    	} else {
    		handitem.count--;
    		setSlotOnServer(chs, handitem);
    	}
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
		if (index >= 60) {
			return openedStorage.getslot(index);
		} else {
			return items.get(index);
		}
    }

	public static BitmapFont font;
	public static float fontheight;
	public void displaySlot(int id, float x, float y, float width, float height) {
		Item item = getSlot(id);
		if (item instanceof NoItem) return;
		Texture t = item.getTexture();
        Hpb.spriteBatch.draw(t, x, y, width, height);
        if (item.count == 1) return;
        font.draw(Hpb.spriteBatch, Integer.toString(item.count), x, y+fontheight);
	}

    public static final Texture slot = Hpb.mutex.getOTexture("slot");
    public static final Texture selectedSlot = Hpb.mutex.getOTexture("sslot");
    public static float
    		x = 0,
    		y = 30,
    		slotWidth = 64f,
    		spacing = 3f,
    		frameWidth = 10 * (slotWidth + spacing),
    		fullinvyalign = y+slotWidth+5f;
    @Override
    public void render() {
    	for (int i = 0; i < 10; i++) {
            float slotX = x + i * (slotWidth + spacing);
            float slotY = y;

            if (this.chs == i) {
                Hpb.spriteBatch.draw(selectedSlot, slotX, slotY, slotWidth, slotWidth);
            } else {
                Hpb.spriteBatch.draw(slot, slotX, slotY, slotWidth, slotWidth);
            }
            displaySlot(i, slotX + spacing, slotY + spacing, slotWidth - spacing * 2, slotWidth - spacing * 2);
        }
    }

    public int getCursoredSlot() {
    	int x = Gdx.input.getX();
		int y = Gdx.graphics.getHeight() - Gdx.input.getY();

    	int slot = 0;
    	boolean finded = false;
		for (float[] pos : slotposmap) {
			if (x >= pos[0] && x <= pos[2] && y >= pos[1] && y <= pos[3]) {
				finded = true;
				break;
			}
			slot++;
		}
		if (!finded) {
			if (openedStorage != null) {
				slot = 60;
				for (float[] pos : openedStorage.getSlotmap()) {
					if (x >= pos[0] && x <= pos[2] && y >= pos[1] && y <= pos[3]) {
						finded = true;
						break;
					}
					slot++;
				}
			}
		}
		if (finded) {
			return slot;
		} else {
			return -2;
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
		if (!finded) {
			if (openedStorage == null) {
				craftboard.onActionOnClient(x,y, button);
			} else {
				slot = 60;
				for (float[] pos : openedStorage.getSlotmap()) {
					if (x >= pos[0] && x <= pos[2] && y >= pos[1] && y <= pos[3]) {
						finded = true;
						break;
					}
					slot++;
				}
			}
		}
		//System.out.println(x+" "+y+" "+down+" "+button+" "+slot+" "+finded);
		if (!finded || !down) return;
		GameU.log("sent inv action slot: "+slot+" down: "+down+" button: "+button);
		Hpb.session.send(new ClientInventoryActionPacket(slot, down, button));
    }

    /**server side*/
    public void doActionByPacketOnServer(int slot, boolean down, int button) {
    	GameU.log("got inv action slot: "+slot+" down: "+down+" button: "+button);
    	Item cursoritem = items.get(-1);
    	boolean cursorIsEmpty = cursoritem.id == 0;
    	Item islot = getSlot(slot);
    	if (down) {
    		if (button == Input.Buttons.LEFT) {
    			if (islot.id == 0) {
	    			if (cursorIsEmpty) {//левый клик по пустому слоту с пустым курсором
	    				//ни к чему не приводит
	    			} else {//левый клик по пустому слоту с заполненым курсором (ложим стак из курсора в слот)
	    				//протестировано
	    				this.setSlotOnServer(slot, cursoritem);//перекидываем item з курсора в нажатый слот
	    				this.setSlotOnServer(-1, EMPTY);//очищаем курсор
	    			}
    			} else {
    				if (cursorIsEmpty) {//левый клик по заполненому слоту с пустым курсором (берем стак в курсор)
    					//протестировано
    					setSlotOnServer(-1, islot);//ложим в курсор item
    					setSlotOnServer(slot, EMPTY);//очищаем нажатый слот
    				} else {//левый клик по заполненому слоту с заполненым курсором
    					if (cursoritem.id == islot.id) {//в слотах одинаковые ресурсы
    						int можноПоложить = islot.stackSize() - islot.count;
    						if (можноПоложить == 0) {//нельзя ничо положить (меняем местами ресурсы)
    							//протестировано
    							setSlotOnServer(-1, islot);//в курсор ложим item из слота
        						setSlotOnServer(slot, cursoritem);//в слот ложим item из курсора
    						} else if (можноПоложить >= cursoritem.count) {//можно положить все что есть в курсоре
    							//протестировано
    							islot.count += cursoritem.count;//прибавляем из курсора в слот
    							setSlotOnServer(slot, islot);///обновляем у клиента (если вызовет ошибку то юзать clone на islot)
    							setSlotOnServer(-1, EMPTY);//очищаем курсор так как все перемещено в слот
    						} else {//можно положить только часть из курсора
    							//протестировано
    							cursoritem.count -= можноПоложить; //убираем эту часть из курсора
    							setSlotOnServer(-1, cursoritem);//обновляем у клиента
    							islot.count += можноПоложить;//добавляем эту часть в слот
    							setSlotOnServer(slot, islot);//обновляем у клиента
    						}
    					} else {//в слотах разные ресурсы (меняем местами ресурсы)
    						//протестировано
    						setSlotOnServer(-1, islot);//в курсор ложим item из слота
    						setSlotOnServer(slot, cursoritem);//в слот ложим item из курсора
    					}
    				}
    			}
    		} else if (button == Input.Buttons.RIGHT) {
    			if (islot.id == 0) {
	    			if (cursorIsEmpty) {//правый клик по пустому слоту с пустым курсором
	    				//ни к чему не приводит
	    				//протестировано
	    			} else {//слот пустой, курсор-нет
	    				if (cursoritem.count == 1) {//в курсоре 1 предмет, помещаем предмет из курсора в слот
	    					//протестировано
	    					setSlotOnServer(-1, EMPTY);
							setSlotOnServer(slot, cursoritem);
						} else {//добавляем в пустой слот 1 предмет из курсора
							//протестировано
							cursoritem.count--;
							setSlotOnServer(-1, cursoritem);
							setSlotOnServer(slot, cursoritem.clone(1));
						}
	    			}
    			} else {
    				if (cursorIsEmpty) {//берется в курсор пол стака, если предмет в слоте 1 то он тоже ложится в курсор, если кол-во в слоте не четное то большая часть идет в курсор
    					if (islot.count == 1) {//1 предмет в слоте (меняем местами)
    						//протестировано
    						setSlotOnServer(slot, EMPTY);
    						setSlotOnServer(-1, islot);
    					} else if (islot.count % 2 == 1) {//нечет в слоте (делим по полам +1 в курсор)
    						//протестировано
    						int halfcount = (islot.count-1)/2;
    						setSlotOnServer(slot, islot.clone(halfcount));
    						setSlotOnServer(-1, islot.clone(halfcount+1));
    					} else {//чет в слоте (делим по полам)
    						//протестировано
    						int halfcount = islot.count/2;
    						setSlotOnServer(slot, islot.clone(halfcount));
    						setSlotOnServer(-1, islot.clone(halfcount));
    					}
    				} else {
    					if (cursoritem.id == islot.id) {//ресурсы одинаковые
    						if (islot.count < islot.stackSize()) {//в слот еще еможно плоложить чтото
    							if (cursoritem.count == 1) {//в курсоре 1 предмет
    								//протестировано
    								setSlotOnServer(-1, EMPTY);//
    								islot.count++;
    								setSlotOnServer(slot, islot);
    							} else {
    								//протестировано
    								cursoritem.count--;
    								setSlotOnServer(-1, cursoritem);
    								islot.count++;
    								setSlotOnServer(slot, islot);
    							}
    						} else {//слот забит
    							//протестировано
    							//ничего не происходит
    						}
    					} else {//в слотах разные предметы (меняем местами предметы)
    						//протестировано
    						setSlotOnServer(-1, islot);//в курсор ложим предмет из слота
    						setSlotOnServer(slot, cursoritem);//в слот ложим предмет из курсора
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
            slotposmap.add(new float[] {slotX, slotY, slotX+slotWidth, slotY+slotWidth});
    	}
    	for (int i = 10; i < 40; i++) {
    		int insideAlignedIndex = i % 10;//0-9
    		int insideAlignedHeightIndex = i / 10;
    		float slotX = x + insideAlignedIndex * (slotWidth + spacing);
            float slotY = fullinvyalign + insideAlignedHeightIndex * (slotWidth + spacing);
            slotposmap.add(new float[] {slotX, slotY, slotX+slotWidth, slotY+slotWidth});
    	}

        if (openedStorage != null) {
    		openedStorage.reloadBounds();
    	}

        craftboard.onResize();
    }

    private TextField currentText;
    private int marginx = 6, marginy = 3;
    public void renderItemInfobar(Item item, int x, int y) {
    	if (item.getDescription() == null) {
    		currentText.setText(item.getName());
    	} else {
    		currentText.setText(item.getName()+"\n"+item.getDescription());
    	}
    	float width = marginx * 2 + currentText.width;
    	float height = marginy * 2 + currentText.height;

    	Texture t = Hpb.mutex.getOTexture("hrzbtn");

    	Hpb.spriteBatch.draw(t, x-width, y-height, width, height);
    	currentText.render(Hpb.spriteBatch, x+marginx-width, y-marginy);
    }

    //max slot index = 127

    /**client side*/
    public void renderFull() {
    	if (openedStorage != null) {
    		openedStorage.render();
    	} else {
    		//render crafting
    		this.craftboard.render();
    	}
    	for (int i = 10; i < 40; i++) {
    		int insideAlignedIndex = i % 10;//0-9
    		int insideAlignedHeightIndex = i / 10;
    		float slotX = x + insideAlignedIndex * (slotWidth + spacing);
            float slotY = fullinvyalign + insideAlignedHeightIndex * (slotWidth + spacing);

            Hpb.spriteBatch.draw(slot, slotX, slotY, slotWidth, slotWidth);
            displaySlot(i, slotX + spacing, slotY + spacing, slotWidth - spacing * 2, slotWidth - spacing * 2);
    	}
    	int sx = Gdx.input.getX();
    	int sy = Gdx.graphics.getHeight() - Gdx.input.getY();
    	if (items.get(-1).id != 0) {
    		displaySlot(-1, sx - slotWidth/2 + spacing, sy - slotWidth/2 + spacing, slotWidth - spacing * 2, slotWidth - spacing * 2);
    	} else {
    		int slotundercursor = getCursoredSlot();
    		if (slotundercursor != -2 && getSlot(slotundercursor).id != 0) {
    			renderItemInfobar(getSlot(slotundercursor), sx, sy);
    		}
    	}
    }

    public boolean canMerge(Item ifrom) {
    	Item avableitem = null;
    	int nearestempty = -2;
    	for (int i = 0; i < 40; i++) {
    		Item item = getSlot(i);
    		if (nearestempty == -2 && item instanceof NoItem) {
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
    	int avableslot = -2;
    	int nearestempty = -2;
    	for (int i = 0; i < 40; i++) {
    		Item item = items.get(i);
    		if (nearestempty == -2 && item instanceof NoItem) {
    			nearestempty = i;
    		}
    		if (item.id == ifrom.id && item.count < item.stackSize()) {
    			avableitem = item;
    			avableslot = i;
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
    		setSlotOnServer(avableslot, avableitem);
    	}
    	return true;
    }

    @Override
    public int getCurrentSlotInt() {
		return chs;
	}

    public Item getHandItem() {
    	return items.get(chs);
    }

    @Override
    public void setCurrentSlotInt(int i) {
    	if (owner.world.isLocal()) {
    		Hpb.session.send(new ClientSetHotbarSlotPacket((byte)i));
    	}
    	chs = i;
	}

    public boolean correctSlot(int slot) {
    	if (items.containsKey(slot)) return true;
    	if (openedStorage != null) {
    		if (openedStorage.items.containsKey(slot)) return true;
    	}
    	return false;
    }

    public int getEmptySlot() {
    	for (int i = 0; i < 40; i++) {
    		Item item = items.get(i);
    		if (item.id == 0) return i;
    	}
    	return -2;
    }

    public void putInInventoryOrDrop(int slot) {
    	if ((slot >= 0 && slot < 40) || items.get(slot).id == 0) {
    		GameU.end("putInInventoryOrDrop called on wrong slot "+slot);
    		return;
    	}
    	Item item = items.get(slot);
    	int eslot = getEmptySlot();
    	setSlotOnServer(slot, EMPTY);
    	if (eslot != -2) {
    		setSlotOnServer(eslot, item);
    		return;
    	}
    	ItemEntity entity = new ItemEntity(owner.getEyeLocation(), item, owner.world, Entity.genLocalId());
    	entity.vel.x += 0.6 * Math.sin(owner.yaw) * Math.cos(owner.pitch);
    	entity.vel.z += 0.6 * -Math.cos(owner.yaw) * Math.cos(owner.pitch);
    	entity.vel.y += 0.6 * Math.sin(owner.pitch);
    	owner.world.spawnEntity(entity);
    }

    public void dropHandItem(boolean stack) {
    	if (items.get(chs).id == 0) return;
    	Item item;
    	if (stack) {
    		item = items.get(chs);
    		setSlotOnServer(chs, EMPTY);
    	} else {
    		Item beforeitem = items.get(chs);
    		if (beforeitem.count <= 1) {
    			setSlotOnServer(chs, EMPTY);
    		} else {
    			beforeitem.count--;
    			setSlotOnServer(chs, beforeitem);
    		}
    		item = beforeitem.clone(1);
    	}
    	ItemEntity entity = new ItemEntity(owner.getEyeLocation(), item, owner.world, Entity.genLocalId());
    	entity.vel.x += 0.6 * Math.sin(owner.yaw) * Math.cos(owner.pitch);
    	entity.vel.z += 0.6 * -Math.cos(owner.yaw) * Math.cos(owner.pitch);
    	entity.vel.y += 0.6 * Math.sin(owner.pitch);

    	owner.world.spawnEntity(entity);
    }

    @Override
    /**server side*/
    public void dropAllItems() {
		for (Entry<Integer, Item> eitem : items.entrySet()) {
			if (eitem.getValue().id != 0) {
				ItemEntity entity;
				owner.world.spawnEntity(entity = new ItemEntity(owner.pos.add(0, 0.2f, 0), eitem.getValue(), owner.world, Entity.genLocalId()));
				entity.vel.y = 0.02f;
				entity.vel.x = MathU.rndf(-0.1f, 0.1f);
				entity.vel.z = MathU.rndf(-0.1f, 0.1f);
				items.replace(eitem.getKey(), EMPTY);
			}
		}
		owner.sendSelfPacket(new ServerSetupInventoryPacket(owner.castedInv.getItems()));
	}

	@Override
	public Player getOwner() {
		return owner;
	}
	/**both side*/
	public void open() {
		this.isOpened = true;
		if (owner.world.isLocal())
			Hpb.session.send(new ClientOpenPlayerInventoryPacket());
			Gdx.input.setCursorCatched(false);
	}

	/**both side*/
	public void open(ItemStorage is) {
		if (isOpened) {
			close();
		}
		this.isOpened = true;
		this.openedStorage = is;
		if (owner.world.isLocal()) {
			Gdx.input.setCursorCatched(false);
			is.reloadBounds();
		} else {
			is.open(owner);
		}
	}


	/**both side*/
	public void close() {
		this.isOpened = false;
		if (owner.world.isLocal()) {
			Hpb.session.send(new ClientCloseInventoryPacket());
			Gdx.input.setCursorCatched(true);
		} else {
			if (items.get(-1).id != 0) {
				putInInventoryOrDrop(-1);
			}
			if (openedStorage != null) {
				openedStorage.close(owner);
			}
		}
		openedStorage = null;
	}

	public JsonElement toJson() {
		JsonObject jinv = new JsonObject();
		String items = "";
		for (Entry<Integer, Item> item : this.items.entrySet()) {
			if (item.getValue() instanceof NoItem || item.getKey() == -1) continue;
			if (!items.equals("")) items += "_";
			items += item.getKey()+"-"+item.getValue().toString();
		}
		jinv.addProperty("items", items);
		return jinv;
	}

	public void fromJson(JsonObject jinv) {
		String items = jinv.get("items").getAsString();
		if (!items.equals("")) {
			for (String substr : items.split("_")) {
				GameU.log(substr);
				String[] itempack = substr.split("-");
				GameU.log(itempack.length);
				GameU.arrayPrint(itempack);
				this.items.put(Integer.parseInt(itempack[0]),
						Item.fromString(itempack[1]));
			}
		}
	}

	/**client side*/
	public Map<Integer, Item> getItems() {
		return items;
	}

	/**client side*/
	public void setItems(Map<Integer, Item> items) {
		this.items = items;//overwrite
	}
}