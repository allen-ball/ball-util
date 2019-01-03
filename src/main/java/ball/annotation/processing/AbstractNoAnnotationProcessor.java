/*
 * $Id$
 *
 * Copyright 2012 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.annotation.processing;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import static javax.tools.Diagnostic.Kind.ERROR;

/**
 * Abstract {@link javax.annotation.processing.Processor} base class for
 * processing "no" {@link java.lang.annotation.Annotation} ({@code "*"}).
 *
 * @see ForElementKinds
 * @see ForSubclassesOf
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class AbstractNoAnnotationProcessor extends AbstractProcessor {
    private List<ElementKind> kinds = null;
    private Class<?> superclass = null;

    /**
     * Sole constructor.
     */
    protected AbstractNoAnnotationProcessor() { super(); }

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
            kinds = Arrays.asList(annotation.value());
        } else {
            kinds = Collections.emptyList();
        }
    }

    private void setSubclassesOf(ForSubclassesOf annotation) {
        if (annotation != null) {
            superclass = annotation.value();
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
                .filter(t -> (kinds.isEmpty() || kinds.contains(t.getKind())))
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
}
