package utils;

import java.util.*;

/**
	Provides a stopwatch for rudimentary profiling.
*/
public class Stopwatch {
	
	private static Hashtable times = new Hashtable();

	public static void start (String label) {
		times.put (label, new Long(System.currentTimeMillis()));	
	}

	public static void printTime (String label, String comment) {
		System.err.println (label+" : "+time(label)+" : "+comment );
	}

	public static void printTimeReset (String label, String comment) {
		System.err.println (label+" : "+timeReset(label)+" : "+comment );
	}

	public static long timeReset (String label) {
		long tmp = time (label);
		start (label);
		return tmp;
		
	}

	public static long time (String label) {
		Object obj = times.get (label);
		if (obj != null) {
			Long l = (Long)obj;	
			return System.currentTimeMillis()-l.longValue();
		}

		return -1;
		
		
	}
	
}
