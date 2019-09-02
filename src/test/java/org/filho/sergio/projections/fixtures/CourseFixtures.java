package org.filho.sergio.projections.fixtures;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import org.filho.sergio.projections.entities.Course;

public class CourseFixtures implements TemplateLoader {

    @Override
    public void load() {
        Fixture.of(Course.class).addTemplate("valid", new Rule() {
            {
                add("name", name());
                add("active", true);
                add("views", 17565L);
            }
        });
    }
}
