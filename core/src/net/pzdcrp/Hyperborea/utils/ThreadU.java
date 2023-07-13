package net.pzdcrp.Hyperborea.utils;

public class ThreadU {
	public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
	
	public static void tracer() {
		for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
		    System.out.println(ste);
		}
	}
	
	public static void end(String msg) {
		System.out.println(msg+"\n    \\|/");
		tracer();
		System.exit(0);
	}
}
