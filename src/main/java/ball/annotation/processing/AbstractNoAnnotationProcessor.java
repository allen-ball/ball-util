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
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.INTERFACE;
import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.WARNING;
import static lombok.AccessLevel.PROTECTED;

/**
 * Abstract {@link javax.annotation.processing.Processor} base class for
 * processing "no" {@link java.lang.annotation.Annotation} ({@code "*"}).
 *
 * @see ForElementKinds
 * @see ForSubclassesOf
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@NoArgsConstructor(access = PROTECTED) @ToString
public abstract class AbstractNoAnnotationProcessor extends AbstractProcessor {
    private static final List<ElementKind> REQUIRED_FOR_SUBCLASSES_OF =
        Arrays.asList(CLASS, INTERFACE);

    private TreeSet<ElementKind> kinds =
        new TreeSet<>(EnumSet.allOf(ElementKind.class));
    private Class<?> superclass = null;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton("*");
    }

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        try {
            setElementKinds(getAnnotation(ForElementKinds.class));
            setSubclassesOf(getAnnotation(ForSubclassesOf.class));
        } catch (Exception exception) {
            print(ERROR, null, exception);
        }
    }

    private void setElementKinds(ForElementKinds annotation) {
        if (annotation != null) {
            kinds.retainAll(Arrays.asList(annotation.value()));
        }
    }

    private void setSubclassesOf(ForSubclassesOf annotation) {
        if (annotation != null) {
            superclass = annotation.value();
            kinds.retainAll(REQUIRED_FOR_SUBCLASSES_OF);
        }
    }

    private <A extends Annotation> A getAnnotation(Class<A> annotation) {
        return getClass().getAnnotation(annotation);
    }

    /**
     * @return  {@code false} always.
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        try {
            new IterableImpl(roundEnv.getRootElements())
                .stream()
                .filter(t -> kinds.contains(t.getKind()))
                .filter(t -> (superclass == null
                              || isAssignable(t.asType(), superclass)))
                .forEach(t -> process(t));
        } catch (Exception exception) {
            print(ERROR, null, exception);
        }

        return false;
    }

    /**
     * Method to process each {@link Element}.
     *
     * @param   element         The {@link Element}.
     */
    protected abstract void process(Element element);

    private class IterableImpl extends LinkedHashSet<Element> {
        private static final long serialVersionUID = -6350696749245608053L;

        public IterableImpl(Collection<? extends Element> collection) {
            addAll(collection);
        }

        @Override
        public boolean add(Element element) {
            boolean modified = super.add(element);

            modified |= addAll(element.getEnclosedElements());

            return modified;
        }

        @Override
        public boolean addAll(Collection<? extends Element> collection) {
            boolean modified = false;

            for (Element element : collection) {
                modified |= add(element);
            }

            return modified;
        }
    }

    /**
     * {@link Processor} implementation.
     */
    @ServiceProviderFor({ Processor.class })
    @For({ ForElementKinds.class, ForSubclassesOf.class })
    @NoArgsConstructor @ToString
    public static class AnnotationProcessor
                        extends AbstractAnnotationProcessor {
        private static final Class<?> SUPERCLASS =
            AbstractNoAnnotationProcessor.class;

        @Override
        public void process(RoundEnvironment roundEnv,
                            TypeElement annotation,
                            Element element) throws Exception {
            switch (element.getKind()) {
            case CLASS:
                if (! isAssignable(element.asType(), SUPERCLASS)) {
                    print(ERROR,
                          element,
                          element.getKind() + " annotated with "
                          + "@" + annotation.getSimpleName()
                          + " but is not a subclass of "
                          + SUPERCLASS.getCanonicalName());
                }

                if (isSameType(annotation.asType(), ForSubclassesOf.class)) {
                    ForElementKinds kinds =
                        element.getAnnotation(ForElementKinds.class);

                    if (kinds != null) {
                        ElementKind[] array = kinds.value();
                        LinkedHashSet<ElementKind> set = new LinkedHashSet<>();

                        if (array != null) {
                            Collections.addAll(set, array);
                        }

                        if (! set.removeAll(REQUIRED_FOR_SUBCLASSES_OF)) {
                            print(ERROR,
                                  element,
                                  element.getKind() + " annotated with "
                                  + "@" + annotation.getSimpleName() + " and "
                                  + "@" + ForElementKinds.class.getSimpleName()
                                  + " but does not specify one of "
                                  + REQUIRED_FOR_SUBCLASSES_OF);
                        }

                        if (! set.isEmpty()) {
                            print(WARNING,
                                  element,
                                  element.getKind() + " annotated with "
                                  + "@" + annotation.getSimpleName() + " and "
                                  + "@" + ForElementKinds.class.getSimpleName()
                                  + "; " + set + " will be ignored");
                        }
                    }
                }
                break;

            default:
                print(ERROR,
                      element,
                      element.getKind() + " annotated with "
                      + "@" + annotation.getSimpleName()
                      + " but is not a " + CLASS);
                break;
            }
        }
    }
}
