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
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.tools.Diagnostic.Kind.ERROR;

/**
 * {@link AbstractNoAnnotationProcessor} base class to specify superclasses
 * and interfaces an annotated {@link Class} {@link MustImplement}.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@NoArgsConstructor @ToString
public abstract class MustImplementProcessor extends AbstractNoAnnotationProcessor {
    private Class<?>[] superclasses = null;

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        try {
            setMustImplement(getAnnotation(MustImplement.class));
        } catch (Exception exception) {
            print(ERROR, null, exception);
        }
    }

    private void setMustImplement(MustImplement annotation) {
        if (annotation != null) {
            superclasses = annotation.value();
        }
    }

    @Override
    protected void process(RoundEnvironment roundEnv, Element element) {
        if (superclasses != null) {
            if (! element.getModifiers().contains(ABSTRACT)) {
                for (Class<?> type : superclasses) {
                    if (! isAssignable(element.asType(), type)) {
                        print(ERROR, element,
                              "%s implements %s but is not a subclass of %s",
                              element.getKind(),
                              superclass.getName(), type.getName());
                    }
                }
            }
        }
    }

    /**
     * {@link Processor} implementation.
     */
    @ServiceProviderFor({ Processor.class })
    @For({ MustImplement.class })
    @NoArgsConstructor @ToString
    public static class ProcessorImpl extends AbstractAnnotationProcessor {
        private static final Class<?> SUPERCLASS =
            MustImplementProcessor.class;

        @Override
        public void process(RoundEnvironment roundEnv,
                            TypeElement annotation, Element element) {
            switch (element.getKind()) {
            case CLASS:
                if (! isAssignable(element.asType(), SUPERCLASS)) {
                    print(ERROR, element,
                          "%s annotated with @%s but is not a subclass of %s",
                          element.getKind(),
                          annotation.getSimpleName(),
                          SUPERCLASS.getCanonicalName());
                }
                break;

            default:
                print(ERROR, element,
                      "%s annotated with @%s but is not a %s",
                      element.getKind(), annotation.getSimpleName(), CLASS);
                break;
            }
        }
    }
}
