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
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static ball.util.Walker.walk;
import static java.util.Collections.disjoint;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.tools.Diagnostic.Kind.ERROR;
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
                ElementKind[] value =
                    getClass().getAnnotation(ForElementKinds.class).value();

                kinds.retainAll(Arrays.asList(value));
            }

            if (getClass().isAnnotationPresent(WithModifiers.class)) {
                EnumSet<Modifier> modifiers = EnumSet.allOf(Modifier.class);
                Modifier[] value =
                    getClass().getAnnotation(WithModifiers.class).value();

                modifiers.retainAll(Arrays.asList(value));
                criteria.add(t -> modifiers.containsAll(t.getModifiers()));
            }

            if (getClass().isAnnotationPresent(WithoutModifiers.class)) {
                EnumSet<Modifier> modifiers = EnumSet.allOf(Modifier.class);
                Modifier[] value =
                    getClass().getAnnotation(WithoutModifiers.class).value();

                modifiers.retainAll(Arrays.asList(value));
                criteria.add(t -> disjoint(modifiers, t.getModifiers()));
            }

            if (getClass().isAnnotationPresent(ForSubclassesOf.class)) {
                Class<?> superclass =
                    getClass().getAnnotation(ForSubclassesOf.class).value();

                kinds.retainAll(ForSubclassesOf.ELEMENT_KINDS);
                criteria.add(t -> isAssignable(t.asType(), superclass));
            }

            if (getClass().isAnnotationPresent(MustImplement.class)) {
                MustImplement annotation =
                    getClass().getAnnotation(MustImplement.class);
                Class<?>[] types = annotation.value();

                checks.add(new MustImplementCheck(annotation, types));
            }
        } catch (Exception exception) {
            print(ERROR, exception);
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

    @AllArgsConstructor @ToString
    private class MustImplementCheck extends Check {
        private final Annotation annotation;
        private final Class<?>[] types;

        @Override
        public void accept(Element element) {
            if (! element.getModifiers().contains(ABSTRACT)) {
                for (Class<?> type : types) {
                    if (! isAssignable(element.asType(), type)) {
                        print(ERROR, element,
                              "%s annotated with @%s but does not implement %s",
                              element,
                              annotation.annotationType().getSimpleName(),
                              type.getName());
                    }
                }
            }
        }
    }
}
