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
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static java.util.Arrays.asList;
import static java.util.Collections.disjoint;
import static javax.lang.model.element.ElementKind.METHOD;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.tools.Diagnostic.Kind.WARNING;

/**
 * {@link Processor} implementation to identify overriding
 * {@link java.lang.reflect.Method}s that are not marked with the
 * {@link Override} {@link java.lang.annotation.Annotation}.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
@ForElementKinds({ METHOD })
@NoArgsConstructor @ToString
public class NoOverrideProcessor extends AbstractNoAnnotationProcessor {
    @Override
    protected void process(RoundEnvironment roundEnv, Element element) {
        ExecutableElement method = (ExecutableElement) element;

        if (method.getAnnotation(Override.class) == null
            && disjoint(method.getModifiers(), asList(PRIVATE, STATIC))) {
            ExecutableElement specification = specifiedBy(method);

            if (specification != null) {
                print(WARNING, method,
                      "%s specified by %s.%s but does not have a @%s annotation",
                      method.getKind(),
                      specification.getEnclosingElement(), specification,
                      Override.class.getSimpleName());
            }
        }
    }
}
