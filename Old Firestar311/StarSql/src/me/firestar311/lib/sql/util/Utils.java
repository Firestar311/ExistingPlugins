package me.firestar311.lib.sql.util;

import java.lang.reflect.Field;
import java.util.*;

public class Utils {
    private static Map<Class<?>, Set<Field>> cachedFields = new HashMap<>();

    public static Set<Field> getClassFields(Class<?> clazz) {
        if (cachedFields.containsKey(clazz)) {
            return cachedFields.get(clazz);
        }
        Set<Field> fields = new LinkedHashSet<>(Arrays.asList(clazz.getDeclaredFields()));
        if (clazz.getSuperclass() != null) {
            getClassFields(clazz.getSuperclass(), fields);
        }
        if (cachedFields.containsKey(clazz)) {
            cachedFields.get(clazz).addAll(fields);
        } else {
            cachedFields.put(clazz, fields);
        }
        return fields;
    }

    public static Set<Field> getClassFields(Class<?> clazz, Set<Field> fields) {
        if (cachedFields.containsKey(clazz)) {
            fields.addAll(cachedFields.get(clazz));
            return fields;
        }
        if (fields == null) {
            fields = new LinkedHashSet<>();
        }

        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        if (clazz.getSuperclass() != null) {
            getClassFields(clazz.getSuperclass(), fields);
        }
        if (cachedFields.containsKey(clazz)) {
            cachedFields.get(clazz).addAll(fields);
        } else {
            cachedFields.put(clazz, fields);
        }
        return fields;
    }
}
