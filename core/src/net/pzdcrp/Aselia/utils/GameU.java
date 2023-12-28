package net.pzdcrp.Aselia.utils;

import java.util.List;

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
		int i = 0;
		System.out.println("tracing from thread: "+Thread.currentThread().getName());
		for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
		    if (i > 2) {
		    	System.err.println("    "+ste);
		    }
		    i++;
		}
	}

	public static void end(String msg) {
		System.out.println(msg+"\n    \\|/");
		tracer();
		System.exit(0);
	}

	public static <T> void arrayPrint(T[] arr) {
		String s = "";
		for (T element : arr) {
			s += element.toString()+" ";
		}
		System.out.println(s);
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

	public static void err(String s) {
		System.err.println(s);
	}

	public static void err(int s) {
		System.err.println(s);
	}

	public static <T> String arrayString(String s, List<T> arr) {
		StringBuilder sb = new StringBuilder(s+": [");
		for (T element : arr) {
			sb.append(element.toString()).append(", ");
		}
		sb.append("]");
		return sb.toString();
	}
}
