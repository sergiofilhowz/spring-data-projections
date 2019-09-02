package org.filho.sergio.projections;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class Projector<T, DTO extends Projection> {

    private final ProjectionRepository projectionRepository;
    private final Class<T> entityClass;
    private final Class<DTO> projectionClass;

    Projector(final ProjectionRepository projectionRepository,
              final Class<T> entityClass,
              final Class<DTO> projectionClass) {
        this.projectionRepository = projectionRepository;
        this.entityClass = entityClass;
        this.projectionClass = projectionClass;
    }

    public Page<DTO> list(final Pageable pageable, final Specification<T> specification) {
        return projectionRepository.queryProjection(entityClass, projectionClass, specification, pageable);
    }

    public List<DTO> list(final Specification<T> specification) {
        return projectionRepository.queryProjection(entityClass, projectionClass, specification);
    }

    public List<DTO> list() {
        return projectionRepository.queryProjection(entityClass, projectionClass);
    }

    public Page<DTO> list(final Pageable pageable) {
        return projectionRepository.queryProjection(entityClass, projectionClass, pageable);
    }

}
