package org.filho.sergio.projections.pojo;

import lombok.Data;
import org.filho.sergio.projections.Projection;
import org.filho.sergio.projections.Property;

@Data
public class DetailCourseDTO implements Projection {

    @Property
    private SimpleAuthorDTO author;

    @Property
    private String name;

}
