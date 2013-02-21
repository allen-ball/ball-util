/*
 * $Id$
 *
 * Copyright 2012, 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.annotation.processing;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;

/**
 * Abstract {@link javax.annotation.processing.Processor} base class for
 * processing "no" {@link java.lang.annotation.Annotation} ("*").
 *
 * @see ForElementKinds
 * @see ForSubclassesOf
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public abstract class AbstractNoAnnotationProcessor extends AbstractProcessor {
    private List<ElementKind> kinds = null;
    private TypeElement supertype = null;

    /**
     * Sole constructor.
     */
    protected AbstractNoAnnotationProcessor() { super(); }

    @Override
    public Set<String> getSupportedAnnotationTypes() { return singleton("*"); }

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        setElementKinds(getAnnotation(ForElementKinds.class));
        setSubclassesOf(getAnnotation(ForSubclassesOf.class));
    }

    private void setElementKinds(ForElementKinds annotation) {
        if (annotation != null) {
            kinds = asList(annotation.value());
        }
    }

    private void setSubclassesOf(ForSubclassesOf annotation) {
        if (annotation != null) {
            supertype = getTypeElementFor(annotation.value());
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
        for (Element element : new IterableImpl(roundEnv.getRootElements())) {

            if (kinds != null) {
                if (! kinds.contains(element.getKind())) {
                    continue;
                }
            }

            if (supertype != null) {
                switch (element.getKind()) {
                case CLASS:
                case INTERFACE:
                    if (! isAssignable(element, supertype)) {
                        continue;
                    }
                    break;

                default:
                    continue;
                    /* break; */
                }
            }

            process(element);
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
        private static final long serialVersionUID = -877676924037023355L;

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
