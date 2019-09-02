package org.filho.sergio.projections.setters;

public interface PropertySetter {

    void set(Object object, Object value) throws ReflectiveOperationException;

}
