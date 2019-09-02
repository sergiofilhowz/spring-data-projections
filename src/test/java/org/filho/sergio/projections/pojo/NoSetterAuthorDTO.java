package org.filho.sergio.projections.pojo;

import lombok.Getter;
import org.filho.sergio.projections.Projection;
import org.filho.sergio.projections.Property;

@Getter
public class NoSetterAuthorDTO implements Projection {

    @Property
    private String name;

}
