package org.filho.sergio.projections.strategies;

import org.filho.sergio.projections.query.QueryBuilder;

import javax.persistence.criteria.Selection;
import java.util.Iterator;
import java.util.List;

public interface PropertyStrategy {

    List<Selection<?>> getSelection(QueryBuilder queryBuilder);

    Object convertRow(Iterator<Object> row);

}
