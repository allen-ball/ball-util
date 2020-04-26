package ball.lang.reflect;
/*-
 * ##########################################################################
 * Utilities
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2020 Allen D. Ball
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ##########################################################################
 */
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.EnumSet;
import java.util.stream.Stream;
import javax.lang.model.element.Modifier;

import static java.util.stream.Collectors.joining;

/**
 * {@link java.lang.reflect} utility methods.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public interface JavaLangReflectMethods {

    /**
     * Method to translate {@link Class} and {@link Member}
     * {@link java.lang.reflect.Modifier} flags to an {@link EnumSet} of
     * {@link Modifier}s.
     *
     * @param   modifiers       The {@code int} representing the modifiers.
     *
     * @return  The {@link EnumSet} of {@link Modifier}s.
     */
    default EnumSet<Modifier> asModifierSet(int modifiers) {
        EnumSet<Modifier> set = EnumSet.noneOf(Modifier.class);

        Stream.of(java.lang.reflect.Modifier.toString(modifiers)
                  .split("[\\p{Space}]+"))
            .filter(t -> (! t.isEmpty()))
            .map(String::toUpperCase)
            .map(Modifier::valueOf)
            .forEach(set::add);

        return set;
    }

    /**
     * See {@link #asModifierSet(int)}.
     *
     * @param   type            The {@link Class}.
     *
     * @return  The {@link EnumSet} of {@link Modifier}s.
     */
    default EnumSet<Modifier> getModifierSetFor(Class<?> type) {
        return asModifierSet(type.getModifiers());
    }

    /**
     * See {@link #asModifierSet(int)}.
     *
     * @param   member          The {@link Member}.
     *
     * @return  The {@link EnumSet} of {@link Modifier}s.
     */
    default EnumSet<Modifier> getModifierSetFor(Member member) {
        return asModifierSet(member.getModifiers());
    }

    /**
     * Dispatches call to {@link #declaration(Field)} or
     * {@link #declaration(Method)} as appropriate.
     *
     * @param   member          The target {@link Member}.
     *
     * @return  {@link String}
     */
    default String declaration(Member member) {
        String string = null;

        if (member instanceof Field) {
            string = declaration((Field) member);
        } else if (member instanceof Method) {
            string = declaration((Method) member);
        } else {
            throw new IllegalArgumentException(String.valueOf(member));
        }

        return string;
    }

    /**
     * Method to generate a {@link Field} declaration.
     *
     * @param   field           The target {@link Field}.
     *
     * @return  {@link String}
     */
    default String declaration(Field field) {
        String string =
            modifiers(field.getModifiers())
            + " " + type(field.getGenericType())
            + " " + field.getName();

        return string.trim();
    }

    /**
     * Method to generate a {@link Method} declaration.
     *
     * @param   method          The target {@link Method}.
     *
     * @return  {@link String}
     */
    default String declaration(Method method) {
        String string =
            modifiers(method.getModifiers())
            + " " + type(method.getGenericReturnType())
            + " " + method.getName();

        string +=
            Stream.of(method.getParameters())
            .map(t -> declaration(t))
            .collect(joining(", ", "(", ")"));

        return string.trim();
    }

    /**
     * Method to generate a {@link Parameter} declaration.
     *
     * @param   parameter       The target {@link Parameter}.
     *
     * @return  {@link String}
     */
    default String declaration(Parameter parameter) {
        String string =
            modifiers(parameter.getModifiers())
            + " " + type(parameter.getParameterizedType())
            + " " + parameter.getName();

        return string.trim();
    }

    /**
     * Method to generate modifiers for {@code declaration()} methods.
     *
     * @param   modifiers       See {@link Modifier}.
     *
     * @return  {@link String}
     */
    default String modifiers(int modifiers) {
        return java.lang.reflect.Modifier.toString(modifiers);
    }

    /**
     * Method to generate types for {@code declaration()} methods.
     *
     * @param   type            The target {@link Type}.
     *
     * @return  {@link String}
     */
    default String type(Type type) {
        String string = null;

        if (type instanceof ParameterizedType) {
            string =
                Stream.of(((ParameterizedType) type).getActualTypeArguments())
                .map(t -> type(t))
                .collect(joining(",",
                                 type(((ParameterizedType) type).getRawType())
                                 + "<",
                                 ">"));
        } else if (type instanceof Class<?>) {
            string = ((Class<?>) type).getSimpleName();
        } else {
            string = type.getTypeName();
        }

        return string;
    }
}
