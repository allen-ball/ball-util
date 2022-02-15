package ball.annotation.processing;
/*-
 * ##########################################################################
 * Utilities
 * %%
 * Copyright (C) 2008 - 2022 Allen D. Ball
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
import ball.util.ant.taskdefs.AntTask;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static javax.tools.Diagnostic.Kind.ERROR;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * {@link Processor} implementation to check {@link Class}es annotated with
 * {@link AntTask}:
 * <ol>
 *   <li value="1">Are an instance of {@link org.apache.tools.ant.Task},</li>
 *   <li value="2">Concrete, and</li>
 *   <li value="3">Have a public no-argument constructor</li>
 * </ol>
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 */
@ServiceProviderFor({ Processor.class })
@For({ AntTask.class })
@NoArgsConstructor @ToString
public class AntTaskProcessor extends AnnotatedProcessor {
    @Override
    protected void process(RoundEnvironment roundEnv, TypeElement annotation, Element element) {
        super.process(roundEnv, annotation, element);

        AnnotationMirror mirror = getAnnotationMirror(element, annotation);
        AnnotationValue value = getAnnotationValue(mirror, "value");

        if (isEmpty((String) value.getValue())) {
            print(ERROR, element, mirror, value, "value() must be non-empty");
        }
    }
}
