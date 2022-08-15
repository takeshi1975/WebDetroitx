package org.minion.detroitx;

public class FilenameUtils {
	public static String getName(String filename) {
		String [] buffer = filename.split("\\");
		int len = buffer.length - 1;
		return buffer[len];
	}
}
