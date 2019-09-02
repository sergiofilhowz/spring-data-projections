package org.filho.sergio.projections;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;

public final class ReflectionUtils {

    public static List<Field> getFields(Class<?> clazz) {
        final List<Field> fields = new LinkedList<>(Arrays.asList(clazz.getDeclaredFields()));
        while (!clazz.getSuperclass().equals(Object.class)) {
            clazz = clazz.getSuperclass();
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        }
        return fields;
    }

    public static Method getSetterForProperty(Field field) {
        final String fieldName = field.getName();
        final String methodName = "set" + LOWER_CAMEL.to(UPPER_CAMEL, fieldName);
        try {
            return field.getDeclaringClass().getMethod(methodName, field.getType());
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

}
