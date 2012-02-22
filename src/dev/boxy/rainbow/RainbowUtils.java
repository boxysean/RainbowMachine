package dev.boxy.rainbow;

public class RainbowUtils {
	
	public static int getInt(String[] args, int idx, int def) {
		if (idx >= args.length) {
			return def;
		} else {
			return Integer.parseInt(args[idx]);
		}
	}

}
