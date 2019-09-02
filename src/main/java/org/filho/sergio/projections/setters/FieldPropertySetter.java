package org.filho.sergio.projections.setters;

import java.lang.reflect.Field;

public class FieldPropertySetter implements PropertySetter {

    private final Field field;

    public FieldPropertySetter(final Field field) {
        this.field = field;
    }

    @Override
    public void set(Object object, final Object value) throws ReflectiveOperationException {
        field.setAccessible(true);
        field.set(object, value);
    }
}
