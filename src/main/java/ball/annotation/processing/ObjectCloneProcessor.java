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
import java.lang.reflect.Method;
import java.util.List;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static java.util.stream.Collectors.toList;
import static javax.lang.model.element.ElementKind.METHOD;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.WARNING;

/**
 * {@link Processor} implementation to check {@link Object#clone()}
 * implementations to verify:
 * <ol>
 *   <li value="1">
 *     The implementing {@link Class} also implements {@link Cloneable}
 *   </li>
 *   <li value="2">
 *     The implementation throws {@link CloneNotSupportedException} (unless
 *     some "intravening" superclass' implementation does not)
 *   </li>
 *   <li value="3">
 *     The implementation returns a subtype of the implementation type
 *   </li>
 * </ol>
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
@ForElementKinds({ METHOD })
@NoArgsConstructor @ToString
public class ObjectCloneProcessor extends AnnotatedNoAnnotationProcessor {
    private static final Method PROTOTYPE;

    static {
        try {
            PROTOTYPE = Object.class.getDeclaredMethod("clone");
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private ExecutableElement METHOD = null;
    private TypeElement CLONEABLE = null;

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        try {
            METHOD = getMethod(PROTOTYPE);
            CLONEABLE = asTypeElement(Cloneable.class);

            criteria.add(t -> overrides((ExecutableElement) t, METHOD));
        } catch (Exception exception) {
            print(ERROR, exception);
        }
    }

    @Override
    protected void process(RoundEnvironment roundEnv, Element element) {
        ExecutableElement method = (ExecutableElement) element;
        TypeElement type = (TypeElement) method.getEnclosingElement();

        if (! type.getInterfaces().contains(CLONEABLE.asType())) {
            print(WARNING, type,
                  "%s overrides '%s' but does not implement %s",
                  type.getKind(),
                  declaration(PROTOTYPE), CLONEABLE.getSimpleName());
        }

        if (! types.isAssignable(method.getReturnType(), type.asType())) {
            print(WARNING, method,
                  "%s overrides '%s' but does not return a subclass of %s",
                  method.getKind(),
                  declaration(PROTOTYPE), type.getSimpleName());
        }

        List<TypeMirror> throwables =
            METHOD.getThrownTypes().stream().collect(toList());

        throwables.retainAll(overrides(method).getThrownTypes());
        throwables.removeAll(method.getThrownTypes());
        throwables.stream()
            .map(t -> types.asElement(t))
            .map(t -> t.getSimpleName())
            .forEach(t -> print(WARNING, method,
                                "%s overrides '%s' but does not throw %s",
                                method.getKind(), declaration(PROTOTYPE), t));
    }
}
