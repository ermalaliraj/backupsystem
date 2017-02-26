package com.ea.bs.ru.util;

import java.sql.Timestamp;

public class Utils {
	
	public static Timestamp now() {
		return new Timestamp(System.currentTimeMillis());
	}

	public static boolean isValidAddress(String ip) {
		String arr[] = ip.split(ip);
		if (arr.length != 4){
			return false;
		}
		for (int i = 0; i < arr.length; i++) {
			try {
				int partValue = Integer.parseInt(arr[i]);
				if (partValue < 0 || partValue > 255){
					return false;
				}
			} catch (NumberFormatException ex) {
				return false;
			}
		}
		return true;
	}

}
