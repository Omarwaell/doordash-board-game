package game.view;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class ReflectionHelper {

    public static int getInt(Object obj, String field) {
        try {
            Class<?> c = obj.getClass();
            while (c != null) {
                try {
                    Field f = c.getDeclaredField(field);
                    f.setAccessible(true);
                    return f.getInt(obj);
                } catch (NoSuchFieldException e) { c = c.getSuperclass(); }
            }
        } catch (Exception ignored) {}
        return 0;
    }

    public static boolean getBool(Object obj, String field) {
        try {
            Class<?> c = obj.getClass();
            while (c != null) {
                try {
                    Field f = c.getDeclaredField(field);
                    f.setAccessible(true);
                    return f.getBoolean(obj);
                } catch (NoSuchFieldException e) { c = c.getSuperclass(); }
            }
        } catch (Exception ignored) {}
        return false;
    }

    public static Object getObj(Object obj, String field) {
        try {
            Class<?> c = obj.getClass();
            while (c != null) {
                try {
                    Field f = c.getDeclaredField(field);
                    f.setAccessible(true);
                    return f.get(obj);
                } catch (NoSuchFieldException e) { c = c.getSuperclass(); }
            }
        } catch (Exception ignored) {}
        return null;
    }

    public static Object invokeNoArg(Object obj, String method) {
        try {
            Class<?> c = obj.getClass();
            while (c != null) {
                try {
                    Method m = c.getDeclaredMethod(method);
                    m.setAccessible(true);
                    return m.invoke(obj);
                } catch (NoSuchMethodException e) { c = c.getSuperclass(); }
            }
        } catch (Exception ignored) {}
        return null;
    }
}