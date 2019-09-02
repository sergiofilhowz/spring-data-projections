package org.filho.sergio.projections.pojo;

import lombok.Data;
import org.filho.sergio.projections.Projection;
import org.filho.sergio.projections.Property;

@Data
public class WrongAuthorDTO implements Projection {

    @Property
    private Long name;

}
