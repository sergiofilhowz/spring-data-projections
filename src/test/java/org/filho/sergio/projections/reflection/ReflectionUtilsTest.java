package org.filho.sergio.projections.reflection;

import org.assertj.core.api.Assertions;
import org.filho.sergio.projections.ReflectionUtils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ReflectionUtilsTest {

    @Test
    public void testGetFields() {
        final List<Field> fields = ReflectionUtils.getFields(MockClass.class);
        assertThat(fields).hasSize(2);

        fields.sort(Comparator.comparing(Field::getName));

        final Field name = fields.get(0);
        final Field nickname = fields.get(1);

        assertThat(nickname.getName()).isEqualTo("nickname");
        assertThat(name.getName()).isEqualTo("name");

        final Method setterName = ReflectionUtils.getSetterForProperty(name);
        final Method setterNickname = ReflectionUtils.getSetterForProperty(nickname);

        assertThat(setterName).isNull();
        assertThat(setterNickname).isNotNull();
        assertThat(setterNickname.getName()).isEqualTo("setNickname");
    }

}