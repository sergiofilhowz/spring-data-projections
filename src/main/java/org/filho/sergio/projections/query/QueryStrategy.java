package org.filho.sergio.projections.query;

import javax.persistence.criteria.Selection;
import java.util.List;

public interface QueryStrategy<T> {

    List<Selection<?>> createSelection(QueryBuilder queryBuilder);

    T convertRow(Object[] row);

}
