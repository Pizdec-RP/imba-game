package net.pzdcrp.Aselia.data;

public class AABBList {
	private AABB[] list;

	public AABBList(AABB... list) {
		this.list = list;
	}
	
	public AABB[] get() {
		return list;
	}
	
	public boolean collide(AABB other) {
		for (AABB aabb : list) {
			if (aabb.collide(other)) return true;
		}
		return false;
	}
	
	public boolean collide(AABBList other) {
		for (AABB aabb : list) {
			for (AABB oaabb : other.get())
			if (aabb.collide(oaabb)) return true;
		}
		return false;
	}
}
