package org.filho.sergio.projections.query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import java.util.HashMap;
import java.util.Map;

public class QueryBuilder {

    protected final Root<?> root;
    protected final CriteriaQuery query;
    protected final CriteriaBuilder criteriaBuilder;

    private final Map<String, Path<?>> pathsCache = new HashMap<>();

    public QueryBuilder(final Root<?> root, final CriteriaQuery query, final CriteriaBuilder criteriaBuilder) {
        this.root = root;
        this.query = query;
        this.criteriaBuilder = criteriaBuilder;
    }

    public Path<?> getPath(final String property, final JoinType joinType) {
        return pathsCache.computeIfAbsent(property, prop -> {
            if (prop.contains(".")) {
                final String[] split = prop.split("\\.");
                From<?, ?> join = root;
                for (int i = 0; i + 1 < split.length; i++) {
                    join = join.join(split[i], joinType);
                }
                return join.get(split[split.length - 1]);
            } else {
                return root.get(prop);
            }
        });
    }

}
