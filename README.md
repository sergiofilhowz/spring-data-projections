# spring-data-projections
Library to ease creation of high performance Entity projections

## Why do I need this library?
If you try to use Spring Data Projections, you will be stuck in a bug where projections doesn't work properly with Specifications. So you can only list your custom projections with Spring Data Specifications.

## What are the advantages of using this library?
This library is lightweight and creates dinamically a SQL Query with only the attributes selected.

## How can I create a new Projection with this project?

First lets assume you have two entities:
```java
@Data
@Entity
@Table(name = "author")
public class Author {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true, length = 42)
    private String uuid;

    @Column
    private String name;

    @OneToMany(mappedBy = "author")
    private List<Course> courses;

    @PrePersist
    public void beforeSave() {
        uuid = UUID.randomUUID().toString();
    }

}
```
 
And

```java
@Data
@Entity
@Table(name = "course")
public class Course {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String name;

    @Column
    private Long views;

    @Column(nullable = false, unique = true, length = 42)
    private String uuid;

    @Column
    private Boolean active;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;

    @PrePersist
    public void beforeSave() {
        uuid = UUID.randomUUID().toString();
    }

}
```

With Spring Data Projections you can create projections like this:

Courses:
```json
{
    "authorName": "John Doe",
    "name": "The ultimate guide to Java",
    "views": 1000000
}
```

And the code to enable this:

```java
import org.filho.sergio.projections.Projection;
import org.filho.sergio.projections.Property;

@Data
public class SimpleCourseDTO implements Projection {

    @Property("author.name")
    private String authorName;

    @Property
    private String name;

    @Property
    private Long views;

}
```

`Projection` is an interface just to enable the projection to be processed from the library.

### The result query will be

```sql
SELECT c.name, c.views, a.name
FROM course c
INNER JOIN author a ON c.author_id = a.id;
```

And the properties will be setted on the attributes properly.

### To list or get this projection

You will need a Projection Repository in your context. To boot up you will need to create a configuration class.

```java
import org.filho.sergio.projections.ProjectionRepository;
import org.filho.sergio.projections.ProjectionRepositoryImpl;import org.filho.sergio.projections.ProjectorFactory;

@Configuration
public class SpringDataProjectionsConfiguration {

    // You can create a ProjectionRepository
    @Bean
    public ProjectionRepository projectionRepository(final EntityManager entityManager) {
        return new ProjectionRepositoryImpl(entityManager);
    }

    // Optionally, you can create a ProjectorFactory
    @Bean
    public ProjectorFactory projectorFactory(final ProjectionRepository projectionRepository) {
        return new ProjectorFactory(projectionRepository);
    }
}
```

Then you will just need to use it inside your services

```java
@Service
public class CourseService {

    private final ProjectionRepository projectionRepository;
    private final Projector<Course, SimpleCourseDTO> simpleCourseProjector;

    @Autowired
    public CourseService(final ProjectionRepository projectionRepository,
                         final ProjectorFactory projectorFactory) {
        this.projectionRepository = projectionRepository;
        this.simpleCourseProjector = projectorFactory.createInstance(Course.class, SimpleCourseDTO.class);
    }

    // with projection repository

    public List<SimpleCourseDTO> listCourses() {
        return projectionRepository.queryProjection(Course.class, SimpleCourseDTO.class);
    }
    
    public Page<SimpleCourseDTO> listCourses(final Pageable pageable) {
        return projectionRepository.queryProjection(Course.class, SimpleCourseDTO.class, pageable);
    }

    public Page<SimpleCourseDTO> listCourses(final Pageable pageable, final Specification<Course> specification) {
        return projectionRepository.queryProjection(Course.class, SimpleCourseDTO.class, pageable, specification);
    }

    public List<SimpleCourseDTO> listCourses(final Specification<Course> specification) {
        return projectionRepository.queryProjection(Course.class, SimpleCourseDTO.class, specification);
    }

    // with projector

    public List<SimpleCourseDTO> listCoursesWithProjector() {
        return this.simpleCourseProjector.list();
    }

    public Page<SimpleCourseDTO> listCoursesWithProjector(final Pageable pageable) {
        return this.simpleCourseProjector.list(pageable);
    }

    public Page<SimpleCourseDTO> listCoursesWithProjector(final Pageable pageable, final Specification<Course> specification) {
        return this.simpleCourseProjector.list(pageable, specification);
    }

    public List<SimpleCourseDTO> listCoursesWithProjector(final Specification<Course> specification) {
        return this.simpleCourseProjector.list(specification);
    }
}
```