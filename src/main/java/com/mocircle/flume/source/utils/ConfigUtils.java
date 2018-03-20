package com.mocircle.flume.source.utils;

import org.apache.commons.lang.StringUtils;

public final class ConfigUtils {

	private ConfigUtils() {
	}

	public static String[] parseAsStringArray(String str, String seperator) {
		String[] array = StringUtils.split(str, seperator);
		for (int i = 0; i < array.length; i++) {
			array[i] = array[i].trim();
		}
		return array;
	}

	public static long[] parseAsLongArray(String str, String seperator) {
		String[] array = StringUtils.split(str, seperator);
		long[] result = new long[array.length];
		for (int i = 0; i < array.length; i++) {
			result[i] = Long.parseLong(array[i].trim());
		}
		return result;
	}

}
