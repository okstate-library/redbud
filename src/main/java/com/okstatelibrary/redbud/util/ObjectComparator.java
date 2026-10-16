package com.okstatelibrary.redbud.util;

import java.lang.reflect.Field;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ObjectComparator {

    public static void findNewObjects(Object oldObj, Object newObj)
            throws IllegalAccessException {

        if (oldObj == null || newObj == null
                || oldObj.getClass() != newObj.getClass()) {
            throw new IllegalArgumentException(
                    "Objects must be non-null and of the same type"
            );
        }

        for (Field field : newObj.getClass().getDeclaredFields()) {

            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            field.setAccessible(true);

            Object oldValue = field.get(oldObj);
            Object newValue = field.get(newObj);

            if (oldValue == null && newValue != null
                    && !isSimpleType(field.getType())) {

                System.out.println(
                        "New object property: " + field.getName()
                                + ", Type: " + field.getType().getSimpleName()
                                + ", Value: " + newValue
                );
            }
        }
    }

    private static boolean isSimpleType(Class<?> type) {
        return type.isPrimitive()
                || type.isEnum()
                || type == String.class
                || Number.class.isAssignableFrom(type)
                || type == Boolean.class
                || type == Character.class
                || type == java.util.UUID.class
                || type == java.time.LocalDate.class
                || type == java.time.LocalDateTime.class;
    }
}
