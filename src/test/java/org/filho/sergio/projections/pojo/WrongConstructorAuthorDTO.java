package org.filho.sergio.projections.pojo;

import lombok.Data;
import org.filho.sergio.projections.Projection;
import org.filho.sergio.projections.Property;

@Data
public class WrongConstructorAuthorDTO implements Projection {

	@Property
	private String name;

	public WrongConstructorAuthorDTO(String name) {
		this.name = name;
	}

}
