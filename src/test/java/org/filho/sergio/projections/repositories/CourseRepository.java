package org.filho.sergio.projections.repositories;

import org.filho.sergio.projections.entities.Course;
import org.filho.sergio.projections.pojo.SpringProjectionCourse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends CrudRepository<Course, Long> {

    Page<SpringProjectionCourse> findAllProjectedByActiveTrue(Pageable pageable);

    Page<SpringProjectionCourse> findAllProjectedByActiveTrue(Specification<Course> spec, Pageable pageable);

    Page<Course> findAllByActiveTrue(Specification<Course> spec, Pageable pageable);

}
