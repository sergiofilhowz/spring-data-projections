package org.filho.sergio.projections;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.FixtureFactoryLoader;
import org.filho.sergio.projections.entities.Author;
import org.filho.sergio.projections.entities.Course;
import org.filho.sergio.projections.pojo.AbstractAuthorDTO;
import org.filho.sergio.projections.pojo.DetailCourseDTO;
import org.filho.sergio.projections.pojo.EntityAssocCourseDTO;
import org.filho.sergio.projections.pojo.InvalidPropertyNameAuthorDTO;
import org.filho.sergio.projections.pojo.NoSetterAuthorDTO;
import org.filho.sergio.projections.pojo.SimpleAuthorDTO;
import org.filho.sergio.projections.pojo.SimpleCourseDTO;
import org.filho.sergio.projections.pojo.SinglePropertyAuthorDTO;
import org.filho.sergio.projections.pojo.SubSimpleAuthorDTO;
import org.filho.sergio.projections.pojo.WrongAuthorDTO;
import org.filho.sergio.projections.pojo.WrongConstructorAuthorDTO;
import org.filho.sergio.projections.query.ProjectionQueryStrategy;
import org.filho.sergio.projections.query.QueryBuilder;
import org.filho.sergio.projections.query.QueryStrategy;
import org.filho.sergio.projections.repositories.AuthorRepository;
import org.filho.sergio.projections.repositories.CourseRepository;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Selection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = JpaConfig.class, loader = AnnotationConfigContextLoader.class)
public class ProjectionRepositoryTest {

    @Autowired private AuthorRepository authorRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private ProjectionRepository projectionRepository;
    @Autowired private ProjectorFactory factory;

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
    public void shouldListSimple() {
        final Author author = authorRepository.save(Fixture.from(Author.class).gimme("valid"));
        final Author secondAuthor = authorRepository.save(Fixture.from(Author.class).gimme("valid"));
        final QueryStrategy<SimpleAuthorDTO> strategy = new ProjectionQueryStrategy<>(SimpleAuthorDTO.class);

        List<SimpleAuthorDTO> list = projectionRepository.query(Author.class, strategy);
        assertThat(list).hasSize(2);
        assertThat(list.get(0).getName()).isEqualTo(author.getName());
        assertThat(list.get(0).getNameWithDifferentProperty()).isEqualTo(author.getName());
        assertThat(list.get(1).getName()).isEqualTo(secondAuthor.getName());
        assertThat(list.get(1).getNameWithDifferentProperty()).isEqualTo(secondAuthor.getName());

        list = projectionRepository.queryProjection(Author.class, SimpleAuthorDTO.class);
        assertThat(list).hasSize(2);
        assertThat(list.get(0).getName()).isEqualTo(author.getName());
        assertThat(list.get(0).getNameWithDifferentProperty()).isEqualTo(author.getName());
        assertThat(list.get(1).getName()).isEqualTo(secondAuthor.getName());
        assertThat(list.get(1).getNameWithDifferentProperty()).isEqualTo(secondAuthor.getName());
    }

    @Test
    public void shouldListSinglePropertyProjections() {
        final Author author = authorRepository.save(Fixture.from(Author.class).gimme("valid"));
        final List<SinglePropertyAuthorDTO> list = projectionRepository
                .queryProjection(Author.class, SinglePropertyAuthorDTO.class);
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getName()).isEqualTo(author.getName());
    }

    @Test
    public void shouldListProjectionsWithoutSetter() {
        final Author author = authorRepository.save(Fixture.from(Author.class).gimme("valid"));
        final List<NoSetterAuthorDTO> list = projectionRepository
                .queryProjection(Author.class, NoSetterAuthorDTO.class);
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getName()).isEqualTo(author.getName());
    }

    @Test
    public void shouldListWithAssociations() {
        final Author author = authorRepository.save(Fixture.from(Author.class).gimme("valid"));
        final Author secondAuthor = authorRepository.save(Fixture.from(Author.class).gimme("valid"));
        final Course course = courseRepository.save(Fixture.from(Course.class).gimme("valid", author(author)));
        final Course secondCourse = courseRepository.save(Fixture.from(Course.class).gimme("valid", author(secondAuthor)));

        final List<SimpleCourseDTO> list = projectionRepository.queryProjection(Course.class, SimpleCourseDTO.class);
        assertThat(list).hasSize(2);
        assertThat(list.get(0).getName()).isEqualTo(course.getName());
        assertThat(list.get(0).getAuthorName()).isEqualTo(author.getName());
        assertThat(list.get(0).getViews()).isEqualTo(course.getViews());
        assertThat(list.get(1).getName()).isEqualTo(secondCourse.getName());
        assertThat(list.get(1).getAuthorName()).isEqualTo(secondAuthor.getName());
        assertThat(list.get(1).getViews()).isEqualTo(secondCourse.getViews());
    }

    @Test
    public void shouldListWithAssociationEntity() {
        final Author author = authorRepository.save(Fixture.from(Author.class).gimme("valid"));
        final Author secondAuthor = authorRepository.save(Fixture.from(Author.class).gimme("valid"));
        final Course course = courseRepository.save(Fixture.from(Course.class).gimme("valid", author(author)));
        final Course secondCourse = courseRepository.save(Fixture.from(Course.class).gimme("valid", author(secondAuthor)));

        final List<EntityAssocCourseDTO> list = projectionRepository.queryProjection(Course.class, EntityAssocCourseDTO.class);
        assertThat(list).hasSize(2);
        assertThat(list.get(0).getName()).isEqualTo(course.getName());
        assertThat(list.get(0).getAuthor()).isInstanceOf(Author.class);
        assertThat(list.get(0).getAuthor().getName()).isEqualTo(author.getName());
        assertThat(list.get(1).getName()).isEqualTo(secondCourse.getName());
        assertThat(list.get(1).getAuthor()).isInstanceOf(Author.class);
        assertThat(list.get(1).getAuthor().getName()).isEqualTo(secondAuthor.getName());
    }

    @Test
    public void shouldListWithEntireAssociations() {
        final Author author = authorRepository.save(Fixture.from(Author.class).gimme("valid"));
        final Author secondAuthor = authorRepository.save(Fixture.from(Author.class).gimme("valid"));
        final Course course = courseRepository.save(Fixture.from(Course.class).gimme("valid", author(author)));
        final Course secondCourse = courseRepository.save(Fixture.from(Course.class).gimme("valid", author(secondAuthor)));

        final List<DetailCourseDTO> list = projectionRepository.queryProjection(Course.class, DetailCourseDTO.class);
        assertThat(list).hasSize(2);
        assertThat(list.get(0).getName()).isEqualTo(course.getName());
        assertThat(list.get(0).getAuthor().getName()).isEqualTo(author.getName());
        assertThat(list.get(1).getName()).isEqualTo(secondCourse.getName());
        assertThat(list.get(1).getAuthor().getName()).isEqualTo(secondAuthor.getName());
    }

    @Test
    public void shouldListWithEntireAssociationsWithCustomQueryStrategy() {
        final Author author = authorRepository.save(Fixture.from(Author.class).gimme("valid"));
        final Author secondAuthor = authorRepository.save(Fixture.from(Author.class).gimme("valid"));
        final Course course = courseRepository.save(Fixture.from(Course.class).gimme("valid", author(author)));
        final Course secondCourse = courseRepository.save(Fixture.from(Course.class).gimme("valid", author(secondAuthor)));

        final List<DetailCourseDTO> list = projectionRepository.query(Course.class, new QueryStrategy<DetailCourseDTO>() {
            @Override
            public List<Selection<?>> createSelection(final QueryBuilder queryBuilder) {
                final List<Selection<?>> list = new ArrayList<>(2);
                list.add(queryBuilder.getPath("name", JoinType.INNER));
                list.add(queryBuilder.getPath("author.name", JoinType.INNER));
                return list;
            }

            @Override
            public DetailCourseDTO convertRow(final Object[] row) {
                final DetailCourseDTO result = new DetailCourseDTO();
                result.setName((String) row[0]);
                result.setAuthor(new SimpleAuthorDTO());
                result.getAuthor().setName((String) row[1]);
                result.getAuthor().setNameWithDifferentProperty((String) row[1]);
                return result;
            }
        });
        assertThat(list).hasSize(2);
        assertThat(list.get(0).getName()).isEqualTo(course.getName());
        assertThat(list.get(0).getAuthor().getName()).isEqualTo(author.getName());
        assertThat(list.get(1).getName()).isEqualTo(secondCourse.getName());
        assertThat(list.get(1).getAuthor().getName()).isEqualTo(secondAuthor.getName());
    }

    @Test
    public void shouldListSimpleInheritance() {
        final Author author = authorRepository.save(Fixture.from(Author.class).gimme("valid"));
        final List<SubSimpleAuthorDTO> list = projectionRepository.queryProjection(Author.class, SubSimpleAuthorDTO.class);
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getName()).isEqualTo(author.getName());
        assertThat(list.get(0).getNameWithDifferentProperty()).isEqualTo(author.getName());
        assertThat(list.get(0).getUuid()).isEqualTo(author.getUuid());
    }

    @Test
    public void shouldListPageable() {
        final Author author = authorRepository.save(Fixture.from(Author.class).gimme("valid"));
        final Author secondAuthor = authorRepository.save(Fixture.from(Author.class).gimme("valid"));
        Page<SimpleAuthorDTO> result = projectionRepository.queryProjection(Author.class,
                SimpleAuthorDTO.class, PageRequest.of(0, 1));
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo(author.getName());
        assertThat(result.getContent().get(0).getNameWithDifferentProperty()).isEqualTo(author.getName());

        result = projectionRepository.queryProjection(Author.class,
                SimpleAuthorDTO.class, PageRequest.of(1, 1));
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo(secondAuthor.getName());
        assertThat(result.getContent().get(0).getNameWithDifferentProperty()).isEqualTo(secondAuthor.getName());

        result = projectionRepository.queryProjection(Author.class,
                SimpleAuthorDTO.class, PageRequest.of(2, 10));
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(0);
    }

    @Test
    public void shouldListSorted() {
        final Projector<Author, SimpleAuthorDTO> projector = factory.createInstance(Author.class, SimpleAuthorDTO.class);

        final Author author = authorRepository.save(Fixture.from(Author.class).gimme("valid"));
        final Author secondAuthor = authorRepository.save(Fixture.from(Author.class).gimme("valid"));
        final List<Author> sorted = Arrays.asList(author, secondAuthor);
        sorted.sort(Comparator.comparing(Author::getName));

        Page<SimpleAuthorDTO> result = projector.list(PageRequest.of(0, 2, Sort.Direction.ASC, "name"));
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getName()).isEqualTo(sorted.get(0).getName());
        assertThat(result.getContent().get(1).getName()).isEqualTo(sorted.get(1).getName());

        result = projector.list(PageRequest.of(0, 2, Sort.Direction.DESC, "name"));
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getName()).isEqualTo(sorted.get(1).getName());
        assertThat(result.getContent().get(1).getName()).isEqualTo(sorted.get(0).getName());
    }

    @Test
    public void shouldListEmpty() {
        final Page<SimpleAuthorDTO> result = projectionRepository
                .queryProjection(Author.class, SimpleAuthorDTO.class, PageRequest.of(0, 2));
        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.getContent()).hasSize(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldListWithInvalidProperty() {
        authorRepository.save(Fixture.from(Author.class).gimme("valid"));
        projectionRepository.queryProjection(Author.class, InvalidPropertyNameAuthorDTO.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldHandleProblem() {
        authorRepository.save(Fixture.from(Author.class).gimme("valid"));
        projectionRepository.queryProjection(Author.class, WrongAuthorDTO.class);
    }

    @Test(expected = RuntimeException.class)
    public void shouldHandleProblemOnConstructor() {
        authorRepository.save(Fixture.from(Author.class).gimme("valid"));
        projectionRepository.queryProjection(Author.class, WrongConstructorAuthorDTO.class);
    }

    @Test(expected = RuntimeException.class)
    public void shouldHandleProblemAbstractClass() {
        authorRepository.save(Fixture.from(Author.class).gimme("valid"));
        projectionRepository.queryProjection(Author.class, AbstractAuthorDTO.class);
    }

    static Rule author(final Author author) {
        return new AuthorRule(author);
    }

    private static class AuthorRule extends Rule {
        private AuthorRule(final Author author) {
            add("author", author);
        }
    }

}
