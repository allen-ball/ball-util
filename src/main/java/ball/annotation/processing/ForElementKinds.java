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
import static javax.tools.Diagnostic.Kind.ERROR;

/**
 * {@link AnnotatedNoAnnotationProcessor}
 * {@link java.lang.annotation.Annotation} to specify {@link ElementKind}
 * criteria.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE })
@AnnotatedTypeMustExtend(AnnotatedNoAnnotationProcessor.class)
public @interface ForElementKinds {
    ElementKind[] value() default { };

    /**
     * {@link Processor} implementation.
     */
    @ServiceProviderFor({ Processor.class })
    @For({ ForElementKinds.class })
    @NoArgsConstructor @ToString
    public static class ProcessorImpl extends AnnotatedProcessor {
        @Override
        public void process(RoundEnvironment roundEnv,
                            TypeElement annotation, Element element) {
            super.process(roundEnv, annotation, element);

            AnnotationMirror mirror = getAnnotationMirror(element, annotation);
            AnnotationValue value = getAnnotationValue(mirror, "value");

            if (isEmptyArray(value)) {
                print(ERROR, element,
                      "%s annotated with @%s whose value() is empty",
                      element.getKind(), annotation.getSimpleName());
            }
        }
    }
}
