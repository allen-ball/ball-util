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
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static java.util.stream.Collectors.toSet;
import static javax.tools.Diagnostic.Kind.ERROR;
import static lombok.AccessLevel.PROTECTED;

/**
 * Abstract {@link javax.annotation.processing.Processor} base class for
 * processing an {@link Annotation}.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@NoArgsConstructor(access = PROTECTED) @ToString
public abstract class AbstractAnnotationProcessor extends AbstractProcessor {
    private final List<Class<? extends Annotation>> list;

    {
        try {
            list = Arrays.asList(getAnnotation(For.class).value());
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    /**
     * Method to get the {@link List} of supported {@link Annotation}
     * {@link Class}es.
     *
     * @return  The {@link List} of supported {@link Annotation}
     *          {@link Class}es.
     */
    protected List<Class<? extends Annotation>> getSupportedAnnotationTypeList() {
        return list;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set =
            getSupportedAnnotationTypeList()
            .stream()
            .map(Class::getCanonicalName)
            .collect(toSet());

        return set;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        annotations.stream().forEach(t -> process(roundEnv, t));

        return true;
    }

    private void process(RoundEnvironment roundEnv, TypeElement annotation) {
        for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
            try {
                process(roundEnv, annotation, element);
            } catch (Throwable throwable) {
                print(ERROR, element, throwable);
            }
        }
    }

    /**
     * Callback method to process an annotated {@link Element}.
     *
     * @param   roundEnv        The {@link RoundEnvironment}.
     * @param   annotation      The annotation {@link TypeElement}.
     * @param   element         The annotated {@link Element}.
     */
    protected abstract void process(RoundEnvironment roundEnv,
                                    TypeElement annotation,
                                    Element element);
}
