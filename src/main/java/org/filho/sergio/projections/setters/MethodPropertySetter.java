package org.filho.sergio.projections.setters;

import java.lang.reflect.Method;

public class MethodPropertySetter implements PropertySetter {

    private final Method method;

    public MethodPropertySetter(final Method method) {
        this.method = method;
    }

    @Override
    public void set(final Object object, final Object value) throws ReflectiveOperationException {
        try {
            method.invoke(object, value);
        } catch (IllegalArgumentException e) {
            final String valueClass = value.getClass().getSimpleName();
            final String methodName = String.format("%s.%s", method.getDeclaringClass().getSimpleName(), method.getName());
            final String parameterType = method.getParameterTypes()[0].getSimpleName();
            final String message = String.format("Method with name %s has parameter type %s and got %s", methodName, parameterType, valueClass);
            throw new IllegalArgumentException(message, e);
        }
    }
}
