package org.filho.sergio.projections.query;

import org.filho.sergio.projections.Projection;
import org.filho.sergio.projections.strategies.ProjectionPropertyStrategy;

import javax.persistence.criteria.Selection;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ProjectionQueryStrategy<T extends Projection> implements QueryStrategy<T> {

    private final ProjectionPropertyStrategy<T> propertyStrategy;

    public ProjectionQueryStrategy(final Class<T> projectionClass) {
        propertyStrategy = new ProjectionPropertyStrategy<>(projectionClass);
    }

    public List<Selection<?>> createSelection(final QueryBuilder queryBuilder) {
        return propertyStrategy.getSelection(queryBuilder);
    }

    public T convertRow(final Object[] row) {
        final List<Object> list = Arrays.asList(row);
        final Iterator<Object> iterator = list.iterator();
        return propertyStrategy.convertRow(iterator);
    }

}
