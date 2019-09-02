package org.filho.sergio.projections.setters;

import java.lang.reflect.Method;

public class MethodPropertySetter implements PropertySetter {

    private final Method method;

    public MethodPropertySetter(final Method method) {
        this.method = method;
    }

    @Override
    public void set(final Object object, final Object value) throws ReflectiveOperationException {
        method.invoke(object, value);
    }
}
