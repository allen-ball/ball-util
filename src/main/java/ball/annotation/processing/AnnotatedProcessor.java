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
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static java.util.stream.Collectors.toSet;
import static javax.lang.model.util.ElementFilter.constructorsIn;
import static javax.tools.Diagnostic.Kind.ERROR;
import static lombok.AccessLevel.PROTECTED;

/**
 * Abstract {@link javax.annotation.processing.Processor} base class for
 * processing {@link Annotation}s specified by @{@link For}.  Provides
 * built-in support for a number of {@link Annotation} types.
 *
 * @see AnnotatedElementMustBe
 * @see AnnotatedTypeMustExtend
 * @see AnnotatedTypeMustHaveNoArgsConstructor
 * @see AnnotationValueMustConvertTo
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@NoArgsConstructor(access = PROTECTED) @ToString
public abstract class AnnotatedProcessor extends AbstractProcessor {
    private final List<Class<? extends Annotation>> list =
        Arrays.asList(getClass().getAnnotation(For.class).value());

    /**
     * Method to get the {@link List} of supported {@link Annotation}
     * {@link Class}es.
     *
     * @return  The {@link List} of supported {@link Annotation}
     *          {@link Class}es.
     */
    protected List<Class<? extends Annotation>> getSupportedAnnotationTypeList() {
        return list;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set =
            getSupportedAnnotationTypeList()
            .stream()
            .map(Class::getCanonicalName)
            .collect(toSet());

        return set;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        annotations.stream().forEach(t -> process(roundEnv, t));

        return true;
    }

    private void process(RoundEnvironment roundEnv, TypeElement annotation) {
        try {
            roundEnv.getElementsAnnotatedWith(annotation)
                .stream()
                .peek(new AnnotatedElementMustBeConsumer(annotation))
                .peek(new AnnotatedTypeMustExtendConsumer(annotation))
                .peek(new AnnotatedTypeMustHaveNoArgsConstructorConsumer(annotation))
                .peek(new AnnotationValueMustConvertToConsumer(annotation))
                .forEach(t -> process(roundEnv, annotation, t));
        } catch (Throwable throwable) {
            print(ERROR, null, throwable);
        }
    }

    /**
     * Callback method to process an annotated {@link Element}.  Default
     * implementation does nothing.
     *
     * @param   roundEnv        The {@link RoundEnvironment}.
     * @param   annotation      The annotation {@link TypeElement}.
     * @param   element         The annotated {@link Element}.
     */
    protected void process(RoundEnvironment roundEnv,
                           TypeElement annotation, Element element) {
    }

    @AllArgsConstructor @ToString
    private class AnnotatedElementMustBeConsumer implements Consumer<Element> {
        private final TypeElement annotation;

        @Override
        public void accept(Element element) {
            AnnotatedElementMustBe meta =
                annotation.getAnnotation(AnnotatedElementMustBe.class);

            if (meta != null) {
                if (element.getKind() != meta.value()) {
                    print(ERROR, element,
                          "%s annotated with @%s but is not a %s",
                          element, annotation.getSimpleName(), meta.value());
                }
            }
        }
    }

    @AllArgsConstructor @ToString
    private class AnnotatedTypeMustExtendConsumer implements Consumer<Element> {
        private final TypeElement annotation;

        @Override
        public void accept(Element element) {
            /*
             * Cannot simply get the Annotation value:
             * meta.value() throws MirroredTypeException
             *
             * Class<?> superclass = (meta != null) ? meta.value() : null;
             * AnnotatedTypeMustExtend meta = annotation.getAnnotation(AnnotatedTypeMustExtend.class);
             */
            AnnotationMirror meta =
                getAnnotationMirror(annotation, AnnotatedTypeMustExtend.class);

            if (meta != null) {
                AnnotationValue value = getAnnotationValue(meta, "value");
                TypeElement superclass =
                    (TypeElement) types.asElement((TypeMirror) value.getValue());

                if (! types.isAssignable(element.asType(), superclass.asType())) {
                    print(ERROR, element,
                          "%s annotated with @%s but does not extend %s",
                          element,
                          annotation.getSimpleName(),
                          superclass.getQualifiedName());
                }
            }
        }
    }

    @AllArgsConstructor @ToString
    private class AnnotatedTypeMustHaveNoArgsConstructorConsumer implements Consumer<Element> {
        private final TypeElement annotation;

        @Override
        public void accept(Element element) {
            AnnotatedTypeMustHaveNoArgsConstructor meta =
                annotation.getAnnotation(AnnotatedTypeMustHaveNoArgsConstructor.class);

            if (meta != null) {
                boolean found =
                    constructorsIn(element.getEnclosedElements())
                    .stream()
                    .filter(t -> t.getModifiers().contains(meta.value()))
                    .anyMatch(t -> t.getParameters().isEmpty());

                if (! found) {
                    print(ERROR, element,
                          "%s annotated with @%s but does not have a %s no-argument constructor",
                          element, annotation.getSimpleName(), meta.value());
                }
            }
        }
    }

    @AllArgsConstructor @ToString
    private class AnnotationValueMustConvertToConsumer implements Consumer<Element> {
        private final TypeElement annotation;

        @Override
        public void accept(Element element) {
            AnnotationMirror meta =
                getAnnotationMirror(annotation,
                                    AnnotationValueMustConvertTo.class);

            if (meta != null) {
                AnnotationValue value = getAnnotationValue(meta, "value");
                TypeElement to =
                    (TypeElement)
                    types.asElement((TypeMirror) value.getValue());
                String method =
                    (String) getAnnotationValue(meta, "method").getValue();
                String name =
                    (String) getAnnotationValue(meta, "name").getValue();
                AnnotationMirror mirror =
                    getAnnotationMirror(element, annotation);
                AnnotationValue from = null;

                try {
                    from = getAnnotationValue(mirror, name);

                    Class<?> type =
                        Class.forName(to.getQualifiedName().toString());

                    if (! method.isEmpty()) {
                        type.getMethod(method, from.getValue().getClass())
                            .invoke(null, from.getValue());
                    } else {
                        type.getConstructor(from.getValue().getClass())
                            .newInstance(from.getValue());
                    }
                } catch (Exception exception) {
                    Throwable throwable = exception;

                    while (throwable instanceof InvocationTargetException) {
                        throwable = throwable.getCause();
                    }

                    print(ERROR, element,
                          "@%s: Cannot convert %s to %s: %s",
                          annotation.getSimpleName(),
                          from, to.getQualifiedName(), throwable.getMessage());
                }
            }
        }
    }
}
