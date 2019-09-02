package org.filho.sergio.projections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProjectorFactory {

    private final ProjectionRepository projectionRepository;

    @Autowired
    public ProjectorFactory(final ProjectionRepository projectionRepository) {
        this.projectionRepository = projectionRepository;
    }

    public <T, DTO extends Projection> Projector<T, DTO> createInstance(
            final Class<T> entityClass, final Class<DTO> projectionClass) {
        return new Projector<>(projectionRepository, entityClass, projectionClass);
    }

}
