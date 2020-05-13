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
import ball.annotation.ServiceProviderFor;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Collections;
import java.util.EnumSet;
import java.util.stream.Stream;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.util.stream.Collectors.toCollection;
import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.WARNING;

/**
 * {@link AnnotatedNoAnnotationProcessor}
 * {@link java.lang.annotation.Annotation} to specify super-{@link Class}
 * criteria.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE })
@AnnotatedTypeMustExtend(AnnotatedNoAnnotationProcessor.class)
public @interface ForSubclassesOf {
    Class<?> value();

    /**
     * {@link ElementKind} subset if {@link ForElementKinds} is specified.
     */
    public static final EnumSet<ElementKind> ELEMENT_KINDS =
        Stream.of(ElementKind.values())
        .filter(t -> (t.isClass() || t.isInterface()))
        .collect(toCollection(() -> EnumSet.noneOf(ElementKind.class)));

    /**
     * {@link Processor} implementation.
     */
    @ServiceProviderFor({ Processor.class })
    @For({ ForSubclassesOf.class })
    @NoArgsConstructor @ToString
    public static class ProcessorImpl extends AnnotatedProcessor {
        @Override
        public void process(RoundEnvironment roundEnv,
                            TypeElement annotation, Element element) {
            super.process(roundEnv, annotation, element);

            ForElementKinds kinds =
                element.getAnnotation(ForElementKinds.class);

            if (kinds != null) {
                EnumSet<ElementKind> set = EnumSet.noneOf(ElementKind.class);

                Collections.addAll(set, kinds.value());

                if (! set.removeAll(ELEMENT_KINDS)) {
                    print(ERROR, element,
                          "%s annotated with @%s and @%s but does not specify one of %s",
                          element.getKind(),
                          annotation.getSimpleName(),
                          ForElementKinds.class.getSimpleName(),
                          ELEMENT_KINDS);
                }

                if (! set.isEmpty()) {
                    print(WARNING, element,
                          "%s annotated with @%s and @%s; %s will be ignored",
                          element.getKind(),
                          annotation.getSimpleName(),
                          ForElementKinds.class.getSimpleName(), set);
                }
            }
        }
    }
}
