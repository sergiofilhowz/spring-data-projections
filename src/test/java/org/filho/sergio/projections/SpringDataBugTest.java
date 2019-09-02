package org.filho.sergio.projections;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.loader.FixtureFactoryLoader;
import org.filho.sergio.projections.entities.Author;
import org.filho.sergio.projections.entities.Course;
import org.filho.sergio.projections.pojo.SpringProjectionCourse;
import org.filho.sergio.projections.repositories.AuthorRepository;
import org.filho.sergio.projections.repositories.CourseRepository;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.filho.sergio.projections.ProjectionRepositoryTest.author;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JpaConfig.class}, loader = AnnotationConfigContextLoader.class)
public class SpringDataBugTest {

    @Autowired private AuthorRepository authorRepository;
    @Autowired private CourseRepository courseRepository;

    @BeforeClass
    public static void startUp() {
        FixtureFactoryLoader.loadTemplates("org.filho.sergio.projections.fixtures");
    }

    @After
    public void clear() {
        courseRepository.deleteAll();
        authorRepository.deleteAll();
    }

    @Test
    public void shouldListWithAssociations() {
        final Author author = authorRepository.save(Fixture.from(Author.class).gimme("valid"));
        final Author secondAuthor = authorRepository.save(Fixture.from(Author.class).gimme("valid"));
        final Course course = courseRepository.save(Fixture.from(Course.class).gimme("valid", author(author)));
        final Course secondCourse = courseRepository.save(Fixture.from(Course.class).gimme("valid", author(secondAuthor)));

        final Page<SpringProjectionCourse> list = courseRepository.findAllProjectedByActiveTrue(PageRequest.of(0, 10));
        assertThat(list).hasSize(2);
        assertThat(list.getContent().get(0).getName()).isEqualTo(course.getName());
        assertThat(list.getContent().get(0).getAuthor().getName()).isEqualTo(author.getName());
        assertThat(list.getContent().get(1).getName()).isEqualTo(secondCourse.getName());
        assertThat(list.getContent().get(1).getAuthor().getName()).isEqualTo(secondAuthor.getName());
    }

    @Test(expected = Throwable.class)
    public void shouldCrash() {
        final Author author = authorRepository.save(Fixture.from(Author.class).gimme("valid"));
        final Author secondAuthor = authorRepository.save(Fixture.from(Author.class).gimme("valid"));
        final Course course = courseRepository.save(Fixture.from(Course.class).gimme("valid", author(author)));
        courseRepository.save(Fixture.from(Course.class).gimme("valid", author(secondAuthor)));

        final Pageable pageable = PageRequest.of(0, 10);
        final Specification<Course> spec = (Specification<Course>)
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("name"), course.getName());
        final Page<Course> list = courseRepository.findAllByActiveTrue(spec, pageable);
        assertThat(list.getContent()).hasSize(1);
        assertThat(list.getContent().get(0).getName()).isEqualTo(course.getName());
        assertThat(list.getContent().get(0).getAuthor().getName()).isEqualTo(author.getName());

        courseRepository.findAllByActiveTrue(spec, pageable);
    }

}
