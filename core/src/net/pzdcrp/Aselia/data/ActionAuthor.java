package net.pzdcrp.Aselia.data;

import net.pzdcrp.Aselia.utils.GameU;

public enum ActionAuthor {
	mob, command, player, world;

	public static ActionAuthor fromByte(byte b) {
		switch (b) {
			case 0:
				return mob;
			case 1:
				return command;
			case 2:
				return player;
			case 3:
				return world;
			default:
				GameU.end("unknown AA byte code: "+b);
				return null;
		}
	}

	public static byte toByte(ActionAuthor a) {
		switch (a) {
			case mob:
				return 0;
			case command:
				return 1;
			case player:
				return 2;
			case world:
				return 3;
			default:
				GameU.end("unknown AA type: "+a.toString());
				return -1;
		}
	}
}
