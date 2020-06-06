package ball.annotation.processing;
/*-
 * ##########################################################################
 * Utilities
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2008 - 2020 Allen D. Ball
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
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static ball.util.Walker.walk;
import static java.util.Collections.disjoint;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.WARNING;
import static lombok.AccessLevel.PROTECTED;

/**
 * Abstract {@link javax.annotation.processing.Processor} base class for
 * processing "no" {@link java.lang.annotation.Annotation} ({@code "*"}).
 *
 * @see ForElementKinds
 * @see ForSubclassesOf
 * @see MustImplement
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@NoArgsConstructor(access = PROTECTED) @ToString
public abstract class AnnotatedNoAnnotationProcessor extends AbstractProcessor {
    protected final List<Predicate<Element>> criteria = new ArrayList<>();
    protected final List<Consumer<Element>> checks = new ArrayList<>();

    /**
     * See {@link ForElementKinds}.
     *
     * @return  The {@link EnumSet} of {@link ElementKind}s specified by the
     *          annotation ({@code null} if no annotation present).
     */
    protected EnumSet<ElementKind> getForElementKinds() {
        EnumSet<ElementKind> value = null;

        if (getClass().isAnnotationPresent(ForElementKinds.class)) {
            ElementKind[] array =
                getClass().getAnnotation(ForElementKinds.class).value();

            value = toEnumSet(array);
        }

        return value;
    }

    /**
     * See {@link WithModifiers}.
     *
     * @return  The {@link EnumSet} of {@link Modifier}s specified by the
     *          annotation ({@code null} if no annotation present).
     */
    protected EnumSet<Modifier> getWithModifiers() {
        EnumSet<Modifier> value = null;

        if (getClass().isAnnotationPresent(WithModifiers.class)) {
            Modifier[] array =
                getClass().getAnnotation(WithModifiers.class).value();

            value = toEnumSet(array);
        }

        return value;
    }

    /**
     * See {@link WithoutModifiers}.
     *
     * @return  The {@link EnumSet} of {@link Modifier}s specified by the
     *          annotation ({@code null} if no annotation present).
     */
    protected EnumSet<Modifier> getWithoutModifiers() {
        EnumSet<Modifier> value = null;

        if (getClass().isAnnotationPresent(WithoutModifiers.class)) {
            Modifier[] array =
                getClass().getAnnotation(WithoutModifiers.class).value();

            value = toEnumSet(array);
        }

        return value;
    }

    /**
     * See {@link ForSubclassesOf}.
     *
     * @return  The {@link Class} specified by the annotation ({@code null}
     *          if no annotation present).
     */
    protected Class<?> getForSubclassesOf() {
        Class<?> value = null;

        if (getClass().isAnnotationPresent(ForSubclassesOf.class)) {
            value = getClass().getAnnotation(ForSubclassesOf.class).value();
        }

        return value;
    }

    /**
     * See {@link MustImplement}.
     *
     * @return  The array of {@link Class}es specified by the annotation
     *          ({@code null} if no annotation present).
     */
    protected Class<?>[] getMustImplement() {
        Class<?>[] value = null;

        if (getClass().isAnnotationPresent(MustImplement.class)) {
            value = getClass().getAnnotation(MustImplement.class).value();
        }

        return value;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton("*");
    }

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        try {
            EnumSet<ElementKind> kinds = EnumSet.allOf(ElementKind.class);

            criteria.add(t -> kinds.contains(t.getKind()));

            if (getClass().isAnnotationPresent(ForElementKinds.class)) {
                kinds.retainAll(getForElementKinds());
            }

            if (getClass().isAnnotationPresent(WithModifiers.class)) {
                criteria.add(withModifiers(getWithModifiers()));
            }

            if (getClass().isAnnotationPresent(WithoutModifiers.class)) {
                criteria.add(withoutModifiers(getWithoutModifiers()));
            }

            if (getClass().isAnnotationPresent(ForSubclassesOf.class)) {
                kinds.retainAll(ForSubclassesOf.ELEMENT_KINDS);
                criteria.add(isAssignableTo(getForSubclassesOf()));
            }

            if (getClass().isAnnotationPresent(MustImplement.class)) {
                criteria.add(new MustImplementCriterion());
            }
        } catch (Exception exception) {
            criteria.clear();
            criteria.add(t -> false);

            print(WARNING, "%s disabled", getClass().getName());
            /* print(WARNING, exception); */
        }
    }

    /**
     * @return  {@code false} always.
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        try {
            walk(roundEnv.getRootElements(), Element::getEnclosedElements)
                .filter(criteria.stream().reduce(t -> true, Predicate::and))
                .peek(checks.stream().reduce(t -> {}, Consumer::andThen))
                .forEach(t -> process(roundEnv, t));
        } catch (Throwable throwable) {
            print(ERROR, throwable);
        }

        return false;
    }

    /**
     * Method to process each {@link Element}.  Default implementation does
     * nothing.
     *
     * @param   roundEnv        The {@link RoundEnvironment}.
     * @param   element         The {@link Element}.
     */
    protected void process(RoundEnvironment roundEnv, Element element) {
    }

    @NoArgsConstructor @ToString
    private class MustImplementCriterion extends Criterion<Element> {
        private final Class<?>[] types = getMustImplement();

        @Override
        public boolean test(Element element) {
            boolean match = withoutModifiers(ABSTRACT).test(element);

            if (match) {
                for (Class<?> type : types) {
                    if (! isAssignableTo(type).test(element)) {
                        match &= false;

                        print(ERROR, element,
                              "@%s: @%s does not implement %s",
                              MustImplement.class.getSimpleName(),
                              element.getKind(), type.getName());
                    }
                }
            }

            return match;
        }
    }
}
