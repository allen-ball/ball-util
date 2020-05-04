package ball.tools.javadoc;
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
import ball.annotation.processing.AnnotatedProcessor;
import ball.annotation.processing.For;
import ball.annotation.processing.AnnotatedTypeMustExtend;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.tools.Diagnostic.Kind.ERROR;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * {@link java.lang.annotation.Annotation} to mark
 * {@link com.sun.tools.doclets.internal.toolkit.taglets.Taglet}s
 * with their name.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE })
@AnnotatedTypeMustExtend(AnnotatedTaglet.class)
public @interface TagletName {
    String value();

    /**
     * {@link Processor} implementation.
     */
    @ServiceProviderFor({ Processor.class })
    @For({ TagletName.class })
    @NoArgsConstructor @ToString
    public class ProcessorImpl extends AnnotatedProcessor {
        @Override
        protected void process(RoundEnvironment roundEnv,
                               TypeElement annotation, Element element) {
            super.process(roundEnv, annotation, element);

            String name = element.getAnnotation(TagletName.class).value();

            if (! isEmpty(name)) {
                if (element.getModifiers().contains(ABSTRACT)) {
                    print(ERROR, element,
                          "%s annotated with @%s but is %s",
                          element.getKind(),
                          annotation.getSimpleName(), ABSTRACT);
                }
            } else {
                print(ERROR, element,
                      "%s annotated with @%s but does not specify value()",
                      element.getKind(), annotation.getSimpleName());
            }
        }
    }
}
