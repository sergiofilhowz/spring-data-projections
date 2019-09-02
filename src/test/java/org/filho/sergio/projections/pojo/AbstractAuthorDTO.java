package org.filho.sergio.projections.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.filho.sergio.projections.Projection;
import org.filho.sergio.projections.Property;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractAuthorDTO implements Projection {

    @Property
    private String name;

}
