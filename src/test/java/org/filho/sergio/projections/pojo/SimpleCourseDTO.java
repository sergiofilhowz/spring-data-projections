package org.filho.sergio.projections.pojo;

import lombok.Data;
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
