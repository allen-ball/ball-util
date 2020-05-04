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
import ball.util.ant.taskdefs.AntTaskAttributeConstraint;
import ball.util.ant.taskdefs.NotEmpty;
import ball.util.ant.taskdefs.NotNull;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static javax.tools.Diagnostic.Kind.ERROR;

/**
 * {@link AntTaskAttributeConstraint} {@link Processor}
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
@For({ AntTaskAttributeConstraint.class, NotEmpty.class, NotNull.class })
@NoArgsConstructor @ToString
public class AntTaskAttributeConstraintProcessor extends AnnotatedProcessor {
    @Override
    public void process(RoundEnvironment roundEnv,
                        TypeElement annotation, Element element) {
        super.process(roundEnv, annotation, element);

        switch (element.getKind()) {
        case ANNOTATION_TYPE:
        case FIELD:
        default:
            break;

        case METHOD:
            if (! isGetterMethod((ExecutableElement) element)) {
                print(ERROR, element,
                      "%s annotated with @%s but is not a property getter method",
                      element.getKind(), annotation.getSimpleName());
            }
            break;
        }
    }
}
