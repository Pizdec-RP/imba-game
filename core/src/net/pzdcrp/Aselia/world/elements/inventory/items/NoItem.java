package net.pzdcrp.Aselia.world.elements.inventory.items;

public class NoItem extends Item {
	public NoItem() {
		super(0);
	}

	@Override
	public Item clone(int count) {
		return new NoItem();
	}
}
