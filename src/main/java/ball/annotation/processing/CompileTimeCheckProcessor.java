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
import ball.annotation.CompileTimeCheck;
import ball.annotation.ServiceProviderFor;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.tools.Diagnostic.Kind.ERROR;

/**
 * {@link CompileTimeCheck} {@link Processor}.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
@For({ CompileTimeCheck.class })
@NoArgsConstructor @ToString
public class CompileTimeCheckProcessor extends AnnotatedProcessor {
    private static final EnumSet<Modifier> FIELD_MODIFIERS =
        EnumSet.of(STATIC, FINAL);

    private final Set<Element> set = new HashSet<>();

    @Override
    protected void process(RoundEnvironment roundEnv,
                           TypeElement annotation, Element element) {
        super.process(roundEnv, annotation, element);

        if (set.add(element)) {
            switch (element.getKind()) {
            case FIELD:
                if (element.getModifiers().containsAll(FIELD_MODIFIERS)) {
                    String name =
                        ((TypeElement) element.getEnclosingElement())
                        .getQualifiedName().toString();

                    try {
                        Class.forName(name);
                    } catch (Throwable throwable) {
                        if (throwable instanceof ExceptionInInitializerError) {
                            throwable = throwable.getCause();
                        }

                        while (throwable instanceof InvocationTargetException) {
                            throwable = throwable.getCause();
                        }

                        print(ERROR, element, "%s", throwable.getMessage());
                    }
                } else {
                    print(ERROR, element,
                          "%s must be %s", element.getKind(), FIELD_MODIFIERS);
                }
                break;

            default:
                throw new IllegalStateException(element.getKind().name());
                /* break; */
            }
        }
    }
}
