package org.filho.sergio.projections;

import org.filho.sergio.projections.entities.Course;
import org.filho.sergio.projections.pojo.SimpleCourseDTO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
public class ProjectorTest {

    @Mock
    private ProjectionRepository projectionRepository;

    private Projector<Course, SimpleCourseDTO> projector;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        projector = new Projector<>(projectionRepository, Course.class, SimpleCourseDTO.class);
    }

    @Test
    public void testListWithPageable() {
        final Pageable pageable = mock(Pageable.class);
        final Page<SimpleCourseDTO> page = mock(Page.class);

        when(projectionRepository.queryProjection(same(Course.class), same(SimpleCourseDTO.class), any(Pageable.class))).thenReturn(page);
        final Page<SimpleCourseDTO> list = projector.list(pageable);

        verify(projectionRepository, times(1)).queryProjection(same(Course.class), same(SimpleCourseDTO.class), same(pageable));
        assertThat(list).isSameAs(page);
    }

    @Test
    public void testListWithPageableAndSpecification() {
        final Pageable pageable = mock(Pageable.class);
        final Specification<Course> specification = mock(Specification.class);
        final Page<SimpleCourseDTO> page = mock(Page.class);

        when(projectionRepository.queryProjection(same(Course.class), same(SimpleCourseDTO.class), any(Pageable.class), any(Specification.class))).thenReturn(page);
        final Page<SimpleCourseDTO> list = projector.list(pageable, specification);

        verify(projectionRepository, times(1)).queryProjection(same(Course.class), same(SimpleCourseDTO.class), same(pageable), same(specification));
        assertThat(list).isSameAs(page);
    }

    @Test
    public void testListWithSpecification() {
        final Specification<Course> specification = mock(Specification.class);
        final List<SimpleCourseDTO> page = mock(List.class);

        when(projectionRepository.queryProjection(same(Course.class), same(SimpleCourseDTO.class), any(Specification.class))).thenReturn(page);
        final List<SimpleCourseDTO> list = projector.list(specification);

        verify(projectionRepository, times(1)).queryProjection(same(Course.class), same(SimpleCourseDTO.class), same(specification));
        assertThat(list).isSameAs(page);
    }

    @Test
    public void testList() {
        final List<SimpleCourseDTO> page = mock(List.class);

        when(projectionRepository.queryProjection(same(Course.class), same(SimpleCourseDTO.class))).thenReturn(page);
        final List<SimpleCourseDTO> list = projector.list();

        verify(projectionRepository, times(1)).queryProjection(same(Course.class), same(SimpleCourseDTO.class));
        assertThat(list).isSameAs(page);
    }

}