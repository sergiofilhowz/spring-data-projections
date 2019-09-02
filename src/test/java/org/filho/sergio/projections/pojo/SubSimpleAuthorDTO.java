package org.filho.sergio.projections.pojo;

import lombok.Getter;
import lombok.Setter;
import org.filho.sergio.projections.Property;

@Getter@Setter
public class SubSimpleAuthorDTO extends SimpleAuthorDTO {

    @Property
    private String uuid;

}
