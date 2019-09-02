package org.filho.sergio.projections.fixtures;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import org.filho.sergio.projections.entities.Author;

public class AuthorFixtures implements TemplateLoader {

    @Override
    public void load() {
        Fixture.of(Author.class).addTemplate("valid", new Rule() {
            {
                add("name", name());
            }
        });
    }
}
