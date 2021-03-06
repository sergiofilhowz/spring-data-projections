package org.filho.sergio.projections;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

/**
 * A projector is a pair representation of a Entity/DTO
 * @param <T>   Is the entity class
 * @param <DTO> Is the DTO class
 * @author Sergio Marcelino (sergio@filho.org)
 */
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

    /**
     * @param pageable          Spring Data Pageable
     * @param specification     Spring Data Specification
     * @return returns a filtered paged list with the DTO
     */
    public Page<DTO> list(final Pageable pageable, final Specification<T> specification) {
        return projectionRepository.queryProjection(entityClass, projectionClass, pageable, specification);
    }

    /**
     * @param specification Spring Data Specification
     * @return returns a filtered list with the DTO
     */
    public List<DTO> list(final Specification<T> specification) {
        return projectionRepository.queryProjection(entityClass, projectionClass, specification);
    }

    /**
     * @return returns the complete list with the DTO
     */
    public List<DTO> list() {
        return projectionRepository.queryProjection(entityClass, projectionClass);
    }

    /**
     * @param pageable Spring Data pageable
     * @return returns the paged list with the DTO
     */
    public Page<DTO> list(final Pageable pageable) {
        return projectionRepository.queryProjection(entityClass, projectionClass, pageable);
    }

}
