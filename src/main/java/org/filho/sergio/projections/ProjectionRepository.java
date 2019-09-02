package org.filho.sergio.projections;

import org.filho.sergio.projections.query.QueryStrategy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ProjectionRepository {

    <DTO extends Projection> List<DTO> query(Class<?> rootClass, QueryStrategy<DTO> queryStrategy);

    <T, DTO extends Projection> List<DTO> query(Class<T> rootClass,
                                                Specification<T> specification, QueryStrategy<DTO> queryStrategy);

    <T, DTO extends Projection> List<DTO> query(Class<T> rootClass, Specification<T> specification,
                                                Pageable pageable, QueryStrategy<DTO> queryStrategy);

    <DTO extends Projection> List<DTO> queryProjection(Class<?> rootClass, Class<DTO> projectionClass);

    <T, DTO extends Projection> List<DTO> queryProjection(Class<T> rootClass, Class<DTO> projectionClass,
                                                          Specification<T> specification);

    <DTO extends Projection> Page<DTO> queryProjection(Class<?> rootClass, Class<DTO> projectionClass, Pageable pageable);

    <T, DTO extends Projection> Page<DTO> queryProjection(Class<T> rootClass, Class<DTO> projectionClass,
                                                          Specification<T> specification, Pageable pageable);

}
