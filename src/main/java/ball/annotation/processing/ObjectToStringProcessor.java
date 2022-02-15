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
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.type.TypeKind.NONE;
import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.WARNING;

/**
 * {@link Processor} implementation to check {@link Class}es to verify:
 * <ol>
 *   <li value="1">
 *     The implementing {@link Class} also overrides {@link Object#toString()}
 *   </li>
 * </ol>
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 */
@ServiceProviderFor({ Processor.class })
@ForElementKinds({ CLASS })
@ForSubclassesOf(Object.class)
@WithoutModifiers({ ABSTRACT })
@NoArgsConstructor @ToString
public class ObjectToStringProcessor extends AnnotatedNoAnnotationProcessor {
    private static final Method PROTOTYPE;

    static {
        try {
            PROTOTYPE = Object.class.getDeclaredMethod("toString");
            PROTOTYPE.setAccessible(true);
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static final Set<String> ANNOTATIONS =
        ResourceBundle.getBundle(ObjectToStringProcessor.class.getName())
        .keySet();

    private ExecutableElement METHOD = null;

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        try {
            METHOD = asExecutableElement(PROTOTYPE);
        } catch (Exception exception) {
            print(ERROR, exception);
        }
    }

    @Override
    protected void process(RoundEnvironment roundEnv, Element element) {
        if (! isGenerated(element)) {
            TypeElement type = (TypeElement) element;

            if (! isAnnotated(type)) {
                ExecutableElement implementation = implementationOf(METHOD, type);

                if (implementation == null || METHOD.equals(implementation)) {
                    print(WARNING, type, "%s does not override '%s'", type.getKind(), declaration(PROTOTYPE));
                }
            }
        }
    }

    private boolean isAnnotated(TypeElement element) {
        boolean found =
            element.getAnnotationMirrors().stream()
            .map(AnnotationMirror::getAnnotationType)
            .map(Objects::toString)
            .anyMatch(ANNOTATIONS::contains);

        if (! found) {
            TypeMirror mirror = element.getSuperclass();

            if (mirror != null && (! mirror.getKind().equals(NONE))) {
                found |= isAnnotated((TypeElement) types.asElement(mirror));
            }
        }

        return found;
    }
}
