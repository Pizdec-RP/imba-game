package net.pzdcrp.Hyperborea.data;

import net.pzdcrp.Hyperborea.utils.GameU;

public enum BlockFace {
	PX,PY,PZ,NX,NY,NZ;
	
	public static BlockFace fromByte(byte b) {
		switch (b) {
			case 0:
				return PX;
			case 1:
				return PY;
			case 2:
				return PZ;
			case 3:
				return NX;
			case 4:
				return NY;
			case 5:
				return NZ;
			default:
				GameU.end("unknown BF byte code: "+b);
				return null;
		}
	}
	
	public static byte toByte(BlockFace a) {
		switch (a) {
			case PX:
				return 0;
			case PY:
				return 1;
			case PZ:
				return 2;
			case NX:
				return 3;
			case NY:
				return 4;
			case NZ:
				return 5;
			default:
				GameU.end("unknown BF type: "+a.toString());
				return -1;
		}
	}
}
