package org.filho.sergio.projections.setters;

import java.lang.reflect.Field;

public class FieldPropertySetter implements PropertySetter {

    private final Field field;

    public FieldPropertySetter(final Field field) {
        this.field = field;
    }

    @Override
    public void set(Object object, final Object value) throws ReflectiveOperationException {
        try {
            field.setAccessible(true);
            field.set(object, value);
        } catch (IllegalArgumentException e) {
            final String valueClass = value.getClass().getSimpleName();
            final String methodName = String.format("%s.%s", field.getDeclaringClass().getSimpleName(), field.getName());
            final String parameterType = field.getType().getSimpleName();
            final String message = String.format("Field with name %s has type %s and got %s", methodName, parameterType, valueClass);
            throw new IllegalArgumentException(message, e);
        }
    }
}
