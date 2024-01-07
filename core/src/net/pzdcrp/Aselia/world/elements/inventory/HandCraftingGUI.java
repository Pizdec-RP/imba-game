package net.pzdcrp.Aselia.world.elements.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

import net.pzdcrp.Aselia.Hpb;
import net.pzdcrp.Aselia.data.TextField;
import net.pzdcrp.Aselia.multiplayer.packets.client.inventory.ClientCraftRequestPacket;
import net.pzdcrp.Aselia.multiplayer.packets.server.ingame.ServerNotificationPacket;
import net.pzdcrp.Aselia.utils.GameU;
import net.pzdcrp.Aselia.world.elements.inventory.items.*;

public class HandCraftingGUI {
	static int ri = 0;
	public static final List<Recipe> x22 = new ArrayList<>() {{
		add(new Recipe(ri++, new PlanksItem(4), new Item[] {new OakLogItem(1)}));
		add(new Recipe(ri++, new CrateItem(1), new Item[] {new PlanksItem(10)}));
		add(new Recipe(ri++, new OakSlabItem(2), new Item[] {new PlanksItem(1)}));
		add(new Recipe(ri++, new DirtItem(1), new Item[] {new MudItem(4)}));
	}};
	private boolean needupdate = false;
	private Texture background;
	private float insideAlignedHeightIndex = 0, xPosOfRecipeInfo = 0, width = 0, height = 0, recipeInfoWidth = 0;
	PlayerInventory host;

	public HandCraftingGUI(PlayerInventory host) {
		this.host = host;
	}

	public void onResize() {
		needupdate = true;
	}

	List<float[]> slotposmap = new ArrayList<>();
	private void onResizeP() {
		recname = new TextField(PlayerInventory.font);
		width = PlayerInventory.frameWidth-PlayerInventory.spacing;
		height = PlayerInventory.spacing + layers * (PlayerInventory.slotWidth + PlayerInventory.spacing);
		Pixmap pmp = new Pixmap((int) width, (int) height, Pixmap.Format.RGBA8888);
		pmp.setColor(Color.BLACK);
		pmp.fillRectangle(0, 0, 1, pmp.getHeight());
		pmp.fillRectangle(0, 0, pmp.getWidth(), 1);
		pmp.fillRectangle(pmp.getWidth()-1, 0, 1, pmp.getHeight());
		pmp.fillRectangle(0, pmp.getHeight()-1, pmp.getWidth(), 1);
		background = new Texture(pmp);

		insideAlignedHeightIndex = PlayerInventory.fullinvyalign + 4 * (PlayerInventory.slotWidth + PlayerInventory.spacing) + PlayerInventory.spacing;
		xPosOfRecipeInfo = PlayerInventory.x + swidth * (PlayerInventory.slotWidth + PlayerInventory.spacing);
		recipeInfoWidth = PlayerInventory.x+width-xPosOfRecipeInfo;
	}

	public void displaySlot(Item item, float x, float y) {
		if (item instanceof NoItem) return;
		Texture t = item.getTexture();
        Hpb.spriteBatch.draw(t, x, y, PlayerInventory.slotWidth, PlayerInventory.slotWidth);
        if (item.count == 1) return;
        PlayerInventory.font.draw(Hpb.spriteBatch, Integer.toString(item.count), x, y+PlayerInventory.fontheight);
	}

	public static void scroll(float ay) {
		if (scrolled == 0 && ay <= 0) scrolled = 0;
		else {
			scrolled += ay;
		}
	}

	private static int layers = 5, swidth = 5, all = layers * swidth, scrolled = 0;
	private TextField recname;
	public void render() {
		if (needupdate) {
			needupdate = false;
			onResizeP();
		}
		Hpb.spriteBatch.draw(background, PlayerInventory.x, insideAlignedHeightIndex);
		Hpb.spriteBatch.draw(Hpb.backgroundOfEverything, xPosOfRecipeInfo, insideAlignedHeightIndex, recipeInfoWidth, height);
		int curwidth = 0;
		int curlayer = 0;

		for (int i = scrolled*swidth; i < all; i++) {
			if (x22.size() <= i) continue;
			Recipe rec = x22.get(i);
			//if (curlayer >= layers) return;
			if (curwidth == swidth) {
				curwidth = 0;
				curlayer++;
				if (curlayer >= layers) break;
			}

			float x = PlayerInventory.x + curwidth * (PlayerInventory.slotWidth + PlayerInventory.spacing);
			float y = insideAlignedHeightIndex + PlayerInventory.spacing + (curlayer * (PlayerInventory.slotWidth + PlayerInventory.spacing));

			Hpb.spriteBatch.draw(PlayerInventory.slot, x, y, PlayerInventory.slotWidth, PlayerInventory.slotWidth);
			displaySlot(rec.result, x,y);
			curwidth++;
			if (upd) {
				if (cx > x && cx < x+PlayerInventory.slotWidth && cy > y && cy < y+PlayerInventory.slotWidth) {
					upd = false;
					GameU.log("clicked on recipie id: "+i);
					Hpb.session.send(new ClientCraftRequestPacket(rec.id));
				}
			}
			int hx = Gdx.input.getX();
			int hy = Gdx.graphics.getHeight() - Gdx.input.getY();
			if (hx > x && hx < x+PlayerInventory.slotWidth && hy > y && hy < y+PlayerInventory.slotWidth) {
				//GameU.log("g");
				float w = insideAlignedHeightIndex+height-10;
				recname.setText(rec.result.getName());
				recname.render(Hpb.spriteBatch, xPosOfRecipeInfo+10, w);
				w -= recname.height;

				recname.setText("Needs:");
				recname.render(Hpb.spriteBatch, xPosOfRecipeInfo+10, w-10);
				w -= recname.height - 10;

				y = w - PlayerInventory.slotWidth - 25;

				int xx = 0;
				for (Item item : rec.need) {
					//Hpb.spriteBatch.draw(Hpb.mutex.getOTexture("sun"), xPosOfRecipeInfo+(xx*PlayerInventory.slotWidth+10), y, PlayerInventory.slotWidth, PlayerInventory.slotWidth);
					displaySlot(item, xPosOfRecipeInfo+(xx*PlayerInventory.slotWidth+10), y);
					xx++;
				}
			}
		}

		/*if (underCursor != null) {
			//TODO
			GameU.log("uc");
			recname.setText(underCursor.result.getName());
			recname.render(Hpb.spriteBatch, recipeInfoWidth, insideAlignedHeightIndex);
		}*/

	}

	private int cx, cy, button;
	private boolean upd = false;
	//by click
	public void onActionOnClient(int x, int y, int button) {
		this.cx = x;
		this.cy = y;
		this.button = button;
		upd = true;
	}

	//by packet
	public void onActionOnServer(int recid) {
		for (Recipe rec : x22) {
			if (rec.id == recid) {
				Item cursor = host.getSlot(-1);
				boolean add = false;
				if (cursor.id != 0) {
					if (cursor.id != rec.result.id) {
						host.getOwner().sendSelfPacket(new ServerNotificationPacket("нужно сначала освободить слот курсора"));
						return;
					} else {
						if (cursor.count + rec.result.count > cursor.stackSize()) {
							host.getOwner().sendSelfPacket(new ServerNotificationPacket("нужно сначала освободить слот курсора"));
							return;
						}
					}
					add = true;
				}
				Map<Integer, Item> futureslots = new HashMap<>();
				for (Item needitem : rec.need) {
					int neednow = needitem.count;
					for (int index = 0; index < 40; index++) {
						Item i = host.getSlot(index);

						if (i.id == needitem.id) {//айди сходятся
							if (i.count > neednow) {//есть больше чем нужно
								futureslots.put(index, i.clone(i.count-neednow));
								neednow = 0;
							} else if (i.count == neednow) {//есть столько сколько нужно
								futureslots.put(index, PlayerInventory.EMPTY);
								neednow = 0;
							} else if (i.count < neednow) {//есть меньше чем нужно
								futureslots.put(index, PlayerInventory.EMPTY);
								neednow -= i.count;
							}
						}
					}
					if (neednow > 0) {
						host.getOwner().sendSelfPacket(new ServerNotificationPacket("craft.noitems"));
						return;
						//cause no items in inventory
					}
				}
				//all items collected
				for (Entry<Integer, Item> entry : futureslots.entrySet()) {
					host.setSlotOnServer(entry.getKey(), entry.getValue());
				}
				if (add) {
					Item before = host.getSlot(-1);
					host.setSlotOnServer(-1, before.clone(before.count+rec.result.count));
				} else {
					host.setSlotOnServer(-1, rec.result);
				}
				return;
			}
		}
		host.getOwner().sendSelfPacket(new ServerNotificationPacket("craft.nocraft"));
	}

}

class Recipe {
	public Item result;
	public Item[] need;
	public int id;

	public Recipe(int id, Item result, Item[] need) {
		this.id = id;
		this.result = result;
		this.need = need;
	}
}
