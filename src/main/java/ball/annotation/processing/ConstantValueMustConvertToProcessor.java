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
import ball.annotation.ConstantValueMustConvertTo;
import ball.annotation.ServiceProviderFor;
import java.lang.reflect.InvocationTargetException;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static javax.tools.Diagnostic.Kind.ERROR;

/**
 * {@link Processor} implementation to verify constant initializers
 * marked by the {@link ConstantValueMustConvertTo} can be converted to the
 * specified type.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
@For({ ConstantValueMustConvertTo.class })
@NoArgsConstructor @ToString
public class ConstantValueMustConvertToProcessor extends AnnotatedProcessor {
    @Override
    public void process(RoundEnvironment roundEnv,
                        TypeElement annotation, Element element) {
        super.process(roundEnv, annotation, element);

        AnnotationMirror mirror = getAnnotationMirror(element, annotation);
        AnnotationValue value = getAnnotationValue(mirror, "value");
        TypeElement to =
            (TypeElement) types.asElement((TypeMirror) value.getValue());
        String method =
            (String) getAnnotationValue(mirror, "method").getValue();
        Object from = null;

        try {
            from = ((VariableElement) element).getConstantValue();

            Class<?> type = Class.forName(to.getQualifiedName().toString());

            if (! method.isEmpty()) {
                type.getMethod(method, from.getClass())
                    .invoke(null, from);
            } else {
                type.getConstructor(from.getClass())
                    .newInstance(from);
            }
        } catch (Exception exception) {
            Throwable throwable = exception;

            while (throwable instanceof InvocationTargetException) {
                throwable = throwable.getCause();
            }

            print(ERROR, element,
                  "Cannot convert %s to %s\n%s: %s",
                  elements.getConstantExpression(from),
                  to.getQualifiedName(),
                  throwable.getClass().getName(), throwable.getMessage());
        }
    }
}
