package com.github.maxopoly.WurstCivTools.misc;

import java.lang.reflect.Field;

public class ReflectionHelper {
	public static Object getFieldValue(Object obj, String fieldName) {
		Field field;
		
		try {
			field = obj.getClass().getDeclaredField(fieldName);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			return null;
		} catch (SecurityException e) {
			e.printStackTrace();
			return null;
		}
		
		field.setAccessible(true);
		
		try {
			return field.get(obj);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
