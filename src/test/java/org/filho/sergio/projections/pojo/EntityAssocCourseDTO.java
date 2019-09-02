package org.filho.sergio.projections.pojo;

import lombok.Data;
import org.filho.sergio.projections.Projection;
import org.filho.sergio.projections.Property;
import org.filho.sergio.projections.entities.Author;

@Data
public class EntityAssocCourseDTO implements Projection {

    @Property
    private Author author;

    @Property
    private String name;

}
