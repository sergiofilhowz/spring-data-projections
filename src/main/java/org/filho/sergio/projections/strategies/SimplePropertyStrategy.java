package org.filho.sergio.projections.strategies;

import org.filho.sergio.projections.Property;
import org.filho.sergio.projections.query.QueryBuilder;

import javax.persistence.criteria.Selection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class SimplePropertyStrategy extends BasePropertyStrategy {

    private final Property property;
    private final String fieldName;

    SimplePropertyStrategy(final String referenceName, final Property property, final String fieldName) {
        super(referenceName);
        this.property = property;
        this.fieldName = fieldName;
    }

    @Override
    public List<Selection<?>> getSelection(final QueryBuilder queryBuilder) {
        final String propertyName = getFromPropertyOrName(property, fieldName);
        return Collections.singletonList(queryBuilder.getPath(propertyName, property.joinType()));
    }

    @Override
    public Object convertRow(final Iterator row) {
        return row.next();
    }
}
