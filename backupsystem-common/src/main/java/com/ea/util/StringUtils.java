package com.ea.util;

public class StringUtils {

	
	public static boolean isEmptyString(String str){
		if(str == null || str.length() == 0){
			return true;
		} else {
			return false;
		}
	}
}
