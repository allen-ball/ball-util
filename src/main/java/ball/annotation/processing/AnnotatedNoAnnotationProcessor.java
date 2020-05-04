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
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.tools.Diagnostic.Kind.ERROR;
import static lombok.AccessLevel.PROTECTED;

/**
 * Abstract {@link javax.annotation.processing.Processor} base class for
 * processing "no" {@link java.lang.annotation.Annotation} ({@code "*"}).
 *
 * @see ForElementKinds
 * @see ForSubclassesOf
 * @see MustImplement
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@NoArgsConstructor(access = PROTECTED) @ToString
public abstract class AnnotatedNoAnnotationProcessor extends AbstractProcessor {
    private EnumSet<ElementKind> kinds = EnumSet.allOf(ElementKind.class);
    private Class<?> superclass = null;
    private Class<?>[] superclasses = null;

    {
        setElementKinds(getClass().getAnnotation(ForElementKinds.class));
        setSubclassesOf(getClass().getAnnotation(ForSubclassesOf.class));
        setMustImplement(getClass().getAnnotation(MustImplement.class));
    }

    private void setElementKinds(ForElementKinds annotation) {
        if (annotation != null) {
            kinds.retainAll(Arrays.asList(annotation.value()));
        }
    }

    private void setSubclassesOf(ForSubclassesOf annotation) {
        if (annotation != null) {
            superclass = annotation.value();
            kinds.retainAll(ForSubclassesOf.ELEMENT_KINDS);
        }
    }

    private void setMustImplement(MustImplement annotation) {
        if (annotation != null) {
            superclasses = annotation.value();
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton("*");
    }

    /**
     * @return  {@code false} always.
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        try {
            Predicate<Element> forElementKinds =
                t -> kinds.contains(t.getKind());
            Predicate<Element> forSubclasses =
                t -> (superclass == null
                      || isAssignable(t.asType(), superclass));

            roundEnv.getRootElements()
                .stream()
                .filter(forElementKinds)
                .filter(forSubclasses)
                .peek(new MustImplementConsumer())
                .forEach(t -> process(roundEnv, t));
        } catch (Throwable throwable) {
            print(ERROR, null, throwable);
        }

        return false;
    }

    /**
     * Method to process each {@link Element}.  Default implementation does
     * nothing.
     *
     * @param   roundEnv        The {@link RoundEnvironment}.
     * @param   element         The {@link Element}.
     */
    protected void process(RoundEnvironment roundEnv, Element element) {
    }

    @NoArgsConstructor @ToString
    private class MustImplementConsumer implements Consumer<Element> {
        @Override
        public void accept(Element element) {
            if (superclasses != null) {
                if (! element.getModifiers().contains(ABSTRACT)) {
                    for (Class<?> type : superclasses) {
                        if (! isAssignable(element.asType(), type)) {
                            print(ERROR, element,
                                  "%s annotated with @%s but does not implement %s",
                                  element,
                                  MustImplement.class.getSimpleName(),
                                  type.getName());
                        }
                    }
                }
            }
        }
    }
}
