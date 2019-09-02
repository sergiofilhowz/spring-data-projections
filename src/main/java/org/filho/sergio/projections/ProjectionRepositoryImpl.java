package org.filho.sergio.projections;

import org.filho.sergio.projections.query.ProjectionQueryStrategy;
import org.filho.sergio.projections.query.QueryBuilder;
import org.filho.sergio.projections.query.QueryStrategy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Component
public class ProjectionRepositoryImpl implements ProjectionRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private final Map<Class<?>, QueryStrategy<?>> projectionStrategies = new HashMap<>();

    public ProjectionRepositoryImpl() {
        super();
    }

    public ProjectionRepositoryImpl(final EntityManager entityManager) {
        this();
        this.entityManager = entityManager;
    }

    @Override
    public <DTO extends Projection> List<DTO> query(final Class<?> rootClass, final QueryStrategy<DTO> queryStrategy) {
        return query(rootClass, null, queryStrategy);
    }

    @Override
    public <T, DTO extends Projection> List<DTO> query(final Class<T> rootClass, final Specification<T> specification, final QueryStrategy<DTO> queryStrategy) {
        return query(rootClass, specification, null, queryStrategy);
    }

    @Override
    public <T, DTO extends Projection> List<DTO> query(final Class<T> rootClass, final Specification<T> specification,
                                                       final Pageable pageable, final QueryStrategy<DTO> queryStrategy) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Object[]> query = builder.createQuery(Object[].class);
        final Root<T> root = query.from(rootClass);
        final QueryBuilder queryBuilder = new QueryBuilder(root, query, builder);
        query.multiselect(queryStrategy.createSelection(queryBuilder));
        ofNullable(specification).ifPresent(spec -> query.where(spec.toPredicate(root, query, builder)));

        if (pageable != null && pageable.getSort().isSorted()) {
            query.orderBy(pageable.getSort().stream().map(order -> {
                final Path<?> sortPath = queryBuilder.getPath(order.getProperty(), JoinType.INNER);
                return order.isAscending() ? builder.asc(sortPath) : builder.desc(sortPath);
            }).collect(Collectors.toList()));
        }

        final TypedQuery<Object[]> typedQuery = entityManager.createQuery(query);

        if (pageable != null && pageable.isPaged()) {
            typedQuery.setFirstResult((int) pageable.getOffset());
            typedQuery.setMaxResults(pageable.getPageSize());
        }

        final List resultList = typedQuery.getResultList();
        final List<DTO> result = new ArrayList<>(resultList.size());
        for (final Object obj : resultList) {
            final Object[] row = mapToObjectArray(obj);
            result.add(queryStrategy.convertRow(row));
        }
        return result;
    }

    /*
     * Thanks to hibernate, single selection queries does not return Object[] per row
     * even on TypedQuery
     */
    private Object[] mapToObjectArray(Object row) {
        return Object[].class.isAssignableFrom(row.getClass())
                ? (Object[]) row
                : new Object[]{row};
    }

    @Override
    @SuppressWarnings("unchecked")
    public <DTO extends Projection> List<DTO> queryProjection(final Class<?> rootClass, final Class<DTO> projectionClass) {
        return queryProjection(rootClass, projectionClass, (Specification) null);
    }

    @Override
    public <T, DTO extends Projection> List<DTO> queryProjection(final Class<T> rootClass,
                                                                 final Class<DTO> projectionClass, final Specification<T> specification) {
        final QueryStrategy<DTO> strategy = resolveQueryStrategy(projectionClass);
        return query(rootClass, specification, strategy);
    }

    @Override
    public <DTO extends Projection> Page<DTO> queryProjection(final Class<?> rootClass, final Class<DTO> projectionClass, final Pageable pageable) {
        return queryProjection(rootClass, projectionClass, pageable, null);
    }

    @Override
    public <T, DTO extends Projection> Page<DTO> queryProjection(
            final Class<T> rootClass, final Class<DTO> projectionClass,
            final Pageable pageable, final Specification<T> specification) {
        final QueryStrategy<DTO> strategy = resolveQueryStrategy(projectionClass);
        return pagedQuery(rootClass, specification, pageable, strategy);
    }

    private <T, DTO extends Projection> Page<DTO> pagedQuery(final Class<T> rootClass,
                                                             final Specification<T> specification, final Pageable pageable, final QueryStrategy<DTO> queryStrategy) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> query = builder.createQuery(Long.class);
        final Root<T> root = query.from(rootClass);
        query.select(builder.count(root));
        ofNullable(specification).ifPresent(spec -> query.where(spec.toPredicate(root, query, builder)));
        final long count = entityManager.createQuery(query).getSingleResult();

        if (count == 0L) {
            return new PageImpl<>(Collections.emptyList(), pageable, count);
        }

        final List<DTO> list = query(rootClass, specification, pageable, queryStrategy);
        return new PageImpl<>(list, pageable, count);
    }

    @SuppressWarnings("unchecked")
    private <DTO extends Projection> QueryStrategy<DTO> resolveQueryStrategy(final Class<DTO> projectionClass) {
        return (QueryStrategy<DTO>) projectionStrategies
                .computeIfAbsent(projectionClass, clazz -> new ProjectionQueryStrategy(clazz));
    }

}
