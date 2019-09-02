package org.filho.sergio.projections.strategies;

import org.filho.sergio.projections.Property;

import static java.util.Optional.ofNullable;

abstract class BasePropertyStrategy implements PropertyStrategy {

    final String referenceName;

    BasePropertyStrategy(final String referenceName) {
        this.referenceName = referenceName;
    }

    private String getProperty(final String property) {
        return ofNullable(referenceName).map(p -> p + "." + property).orElse(property);
    }

    String getFromPropertyOrName(final Property property, final String fieldName) {
        final String prop = property.value().isEmpty() ? fieldName : property.value();
        return getProperty(prop);
    }

}
