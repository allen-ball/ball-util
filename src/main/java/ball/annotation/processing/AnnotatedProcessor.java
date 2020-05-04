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
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Pattern;
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
 * @see AnnotatedTypeMustExtend
 * @see AnnotatedTypeMustHaveNoArgsConstructor
 * @see AnnotationValueMustBePattern
 * @see AnnotationValueMustBeURI
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@NoArgsConstructor(access = PROTECTED) @ToString
public abstract class AnnotatedProcessor extends AbstractProcessor {
    private final List<Class<? extends Annotation>> list;

    {
        try {
            list = Arrays.asList(getClass().getAnnotation(For.class).value());
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

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
                .peek(new AnnotatedTypeMustExtendConsumer(annotation))
                .peek(new AnnotatedTypeMustHaveNoArgsConstructorConsumer(annotation))
                .peek(new AnnotationValueMustBePatternConsumer(annotation))
                .peek(new AnnotationValueMustBeURIConsumer(annotation))
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
            AnnotationMirror mirror =
                getAnnotationMirror(annotation, AnnotatedTypeMustExtend.class);

            if (mirror != null) {
                AnnotationValue value =
                    getAnnotationElementValue(mirror, "value");
                TypeElement superclass =
                    (TypeElement) types.asElement((TypeMirror) value.getValue());

                if (superclass != null) {
                    if (! types.isAssignable(element.asType(), superclass.asType())) {
                        print(ERROR, element,
                              "%s annotated with @%s but does not extend %s",
                              element.getKind(),
                              annotation.getSimpleName(),
                              superclass.getQualifiedName());
                    }
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
                          element.getKind(), annotation.getSimpleName(),
                          meta.value());
                }
            }
        }
    }

    @AllArgsConstructor @ToString
    private class AnnotationValueMustBePatternConsumer implements Consumer<Element> {
        private final TypeElement annotation;

        @Override
        public void accept(Element element) {
            AnnotationValueMustBePattern meta =
                annotation.getAnnotation(AnnotationValueMustBePattern.class);

            if (meta != null) {
                AnnotationMirror mirror =
                    getAnnotationMirror(element, annotation);
                AnnotationValue value = null;

                try {
                    value = getAnnotationElementValue(mirror, meta.value());
                    Pattern.compile((String) value.getValue());
                } catch (Exception exception) {
                    print(ERROR, element,
                          "@%s: Cannot compile %s to %s: %s",
                          annotation.getSimpleName(),
                          value, Pattern.class.getName(),
                          exception.getMessage());
                }
            }
        }
    }

    @AllArgsConstructor @ToString
    private class AnnotationValueMustBeURIConsumer implements Consumer<Element> {
        private final TypeElement annotation;

        @Override
        public void accept(Element element) {
            AnnotationValueMustBeURI meta =
                annotation.getAnnotation(AnnotationValueMustBeURI.class);

            if (meta != null) {
                AnnotationMirror mirror =
                    getAnnotationMirror(element, annotation);
                AnnotationValue value = null;

                try {
                    value = getAnnotationElementValue(mirror, meta.value());
                    new URI((String) value.getValue());
                } catch (Exception exception) {
                    print(ERROR, element,
                          "@%s: Cannot convert %s to %s: %s",
                          annotation.getSimpleName(),
                          value, URI.class.getName(),
                          exception.getMessage());
                }
            }
        }
    }
}
