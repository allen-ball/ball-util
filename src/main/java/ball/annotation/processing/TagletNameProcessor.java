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
import ball.tools.javadoc.TagletName;
import com.sun.tools.doclets.internal.toolkit.taglets.Taglet;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.tools.Diagnostic.Kind.ERROR;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * {@link Processor} implementation to check {@link Class}es annotated with
 * {@link TagletName}:
 * <ol>
 *   <li value="1">
 *     Are an instance of the legacy {@link com.sun.tools.doclets.Taglet} or
 *     the Sun internal {@link Taglet},
 *   </li>
 *   <li value="2">Concrete, and</li>
 *   <li value="3">Have a public no-argument constructor</li>
 * </ol>
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
@For({ TagletName.class })
@NoArgsConstructor @ToString
public class TagletNameProcessor extends AbstractAnnotationProcessor {
    @Override
    protected void process(RoundEnvironment env,
                           TypeElement annotation,
                           Element element) throws Exception {
        String name = element.getAnnotation(TagletName.class).value();

        if (! isEmpty(name)) {
            if (isAssignable(element.asType(), Taglet.class)) {
                if (! element.getModifiers().contains(ABSTRACT)) {
                    if (! hasPublicNoArgumentConstructor(element)) {
                        print(ERROR,
                              element,
                              element.getKind() + " annotated with "
                              + "@" + annotation.getSimpleName()
                              + " but does not have a " + PUBLIC
                              + " no-argument constructor");
                    }
                } else {
                    print(ERROR,
                          element,
                          element.getKind() + " annotated with "
                          + "@" + annotation.getSimpleName()
                          + " but is " + ABSTRACT);
                }
            } else {
                print(ERROR,
                      element,
                      element.getKind() + " annotated with "
                      + "@" + annotation.getSimpleName()
                      + " but does not implement " + Taglet.class.getName());
            }
        } else {
            print(ERROR,
                  element,
                  element.getKind() + " annotated with "
                  + "@" + annotation.getSimpleName()
                  + " but does not specify value()");
        }
    }
}
