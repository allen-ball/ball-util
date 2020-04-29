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
import ball.annotation.MatcherGroup;
import ball.annotation.ServiceProviderFor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.tools.Diagnostic.Kind.ERROR;

/**
 * {@link Processor} implementation to check {@link MatcherGroup}
 * annotations.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
@For({ MatcherGroup.class })
@NoArgsConstructor @ToString
public class MatcherGroupProcessor extends AbstractAnnotationProcessor {
    @Override
    protected void process(RoundEnvironment env,
                           TypeElement annotation,
                           Element element) throws Exception {
        int group = element.getAnnotation(MatcherGroup.class).value();

        if (group < 0) {
            print(ERROR, element,
                  "%s annotated with @%s has invalid value()",
                  element.getKind(), annotation.getSimpleName());
        }

        switch (element.getKind()) {
        case ANNOTATION_TYPE:
        case FIELD:
        default:
            break;

        case METHOD:
            ExecutableElement executable = (ExecutableElement) element;

            if (! element.getModifiers().contains(PRIVATE)) {
                if (executable.isVarArgs() || executable.getParameters().size() != 1) {
                    print(ERROR, element,
                          "%s annotated with @%s but does not take exactly one argument",
                          element.getKind(), annotation.getSimpleName());
                }
            } else {
                print(ERROR, element,
                      "%s annotated with @%s but is %s",
                      element.getKind(), annotation.getSimpleName(), PRIVATE);
            }
            break;
        }
    }
}
