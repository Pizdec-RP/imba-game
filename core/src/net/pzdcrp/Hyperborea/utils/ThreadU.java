package net.pzdcrp.Hyperborea.utils;

public class ThreadU {
	public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
}
