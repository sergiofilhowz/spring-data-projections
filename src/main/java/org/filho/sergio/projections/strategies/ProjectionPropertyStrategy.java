package org.filho.sergio.projections.strategies;

import org.filho.sergio.projections.Projection;
import org.filho.sergio.projections.Property;
import org.filho.sergio.projections.ReflectionUtils;
import org.filho.sergio.projections.query.QueryBuilder;
import org.filho.sergio.projections.setters.FieldPropertySetter;
import org.filho.sergio.projections.setters.MethodPropertySetter;
import org.filho.sergio.projections.setters.PropertySetter;
import org.springframework.data.util.Pair;

import javax.persistence.criteria.Selection;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static org.filho.sergio.projections.ReflectionUtils.getFields;

public class ProjectionPropertyStrategy<T extends Projection> extends BasePropertyStrategy {

    private final Class<T> projectionClass;
    private List<Pair<PropertySetter, PropertyStrategy>> fields;

    public ProjectionPropertyStrategy(final Class<T> projectionClass) {
        this(null, projectionClass);
    }

    private ProjectionPropertyStrategy(final String referenceName, final Class<T> projectionClass) {
        super(referenceName);
        this.projectionClass = projectionClass;

        this.fields = getFields(projectionClass).stream()
                .filter(field -> field.getAnnotation(Property.class) != null)
                .map(this::configureAcessor)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private Pair<PropertySetter, PropertyStrategy> configureAcessor(Field field) {
        final Property property = field.getAnnotation(Property.class);
        final String propertyName = getFromPropertyOrName(property, field.getName());
        final Method method = ReflectionUtils.getSetterForProperty(field);
        final PropertySetter setter = method == null
                ? new FieldPropertySetter(field)
                : new MethodPropertySetter(method);

        if (Projection.class.isAssignableFrom(field.getType())) {
            return Pair.of(setter, new ProjectionPropertyStrategy(propertyName, field.getType()));
        }
        return Pair.of(setter, new SimplePropertyStrategy(referenceName, property, field.getName()));
    }

    @Override
    public List<Selection<?>> getSelection(final QueryBuilder queryBuilder) {
        return this.fields.stream()
                .map(field -> field.getSecond().getSelection(queryBuilder))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @Override
    public T convertRow(final Iterator<Object> row) {
        try {
            final T dto = projectionClass.newInstance();
            for (final Pair<PropertySetter, PropertyStrategy> pair : fields) {
                final PropertySetter setter = pair.getFirst();
                setter.set(dto, pair.getSecond().convertRow(row));
            }
            return dto;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
