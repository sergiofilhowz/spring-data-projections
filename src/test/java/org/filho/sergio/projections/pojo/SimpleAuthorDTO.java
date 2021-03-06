package org.filho.sergio.projections.pojo;

import lombok.Data;
import org.filho.sergio.projections.Projection;
import org.filho.sergio.projections.Property;

@Data
public class SimpleAuthorDTO implements Projection {

    @Property
    private String name;

    @Property("name")
    private String nameWithDifferentProperty;

}
