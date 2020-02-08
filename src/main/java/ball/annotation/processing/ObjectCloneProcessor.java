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
import java.util.ArrayList;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.util.ElementFilter.methodsIn;
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
@ForElementKinds({ CLASS })
@ForSubclassesOf(Object.class)
@NoArgsConstructor @ToString
public class ObjectCloneProcessor extends AbstractNoAnnotationProcessor {
    private ExecutableElement METHOD = null;
    private TypeElement THROWABLE = null;

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        try {
            METHOD = getExecutableElementFor(Object.class, "clone");
            THROWABLE = getTypeElementFor(Cloneable.class);
        } catch (Exception exception) {
            print(ERROR, null, exception);
        }
    }

    @Override
    protected void process(Element element) {
        methodsIn(((TypeElement) element).getEnclosedElements())
            .stream()
            .filter(t -> overrides(t, METHOD))
            .forEach(t -> check(t));
    }

    private void check(ExecutableElement method) {
        TypeElement type = (TypeElement) method.getEnclosingElement();

        if (! type.getInterfaces().contains(THROWABLE.asType())) {
            print(WARNING,
                  type,
                  type.getKind() + " overrides "
                  + METHOD.getEnclosingElement().getSimpleName()
                  + DOT + METHOD.toString() + " but does not implement "
                  + THROWABLE.getSimpleName());
        }

        if (! types.isAssignable(method.getReturnType(), type.asType())) {
            print(WARNING,
                  method,
                  method.getKind() + " overrides "
                  + METHOD.getEnclosingElement().getSimpleName()
                  + DOT + METHOD.toString()
                  + " but does not return a subclass of "
                  + type.getSimpleName());
        }

        ArrayList<TypeMirror> throwables = new ArrayList<>();

        throwables.addAll(METHOD.getThrownTypes());
        throwables.retainAll(overrides(method).getThrownTypes());
        throwables.removeAll(method.getThrownTypes());

        for (TypeMirror mirror : throwables) {
            Element element = types.asElement(mirror);
            CharSequence name = null;

            switch (element.getKind()) {
            case CLASS:
                name = ((TypeElement) element).getQualifiedName();
                break;

            default:
                name = element.getSimpleName();
                break;
            }

            print(WARNING,
                  method,
                  method.getKind() + " overrides "
                  + METHOD.getEnclosingElement().getSimpleName()
                  + DOT + METHOD.toString() + " but does not throw " + name);
        }
    }
}
