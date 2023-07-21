package net.pzdcrp.Hyperborea.utils;

public class GameU {
	public static boolean debug = true;
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
	
	public static <T> void arrayPrint(T[] arr) {
		for (T element : arr) {
			System.out.print(element.toString()+" ");
		}
		System.out.println();
	}
	
	public static <T> void arrayPrint(String s, T[] arr) {
		System.out.print(s);
		for (T element : arr) {
			System.out.print(element.toString()+" ");
		}
		System.out.println();
	}
	
	public static void log(Object o) {
		System.out.println(o.toString());
	}
	
	public static void log(String s) {
		System.out.println(s);
	}
	
	public static void d(Object o) {
		if (debug)
			System.out.println(o.toString());
	}
	
	public static void d(String s) {
		if (debug)
			System.out.println(s);
	}
}
