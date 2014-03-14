package com.sun.tools.javac.parser;

import java.lang.reflect.Field;


class PrivateAccessField {
	@SuppressWarnings("unchecked")
    public static final <T> T getField(Object object, String name) {
        try {
            Field field = object.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return (T) field.get(object);
        } catch (Exception ex) {
            throw new AssertionError(ex);
        }
    }
	public static boolean setField(Object object,String name,Object value){
		
		try {
			Field field = object.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(object, value);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public static boolean setFieldInt(Object object,String name,int value){
		
		try {
			Field field = object.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.setInt(object, value);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public static boolean setFieldChar(Object object,String name,char value){
		
		try {
			Field field = object.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.setChar(object, value);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
