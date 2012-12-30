/*
 * $Id$
 *
 * Copyright 2012 Allen D. Ball.  All rights reserved.
 */
package iprotium.annotation.processing;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 * Abstract {@link javax.annotation.processing.Processor} base class for
 * processing "no" {@link java.lang.annotation.Annotation} ("*").
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public abstract class AbstractNoAnnotationProcessor extends AbstractProcessor {

    /**
     * Sole constructor.
     */
    protected AbstractNoAnnotationProcessor() { super(); }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton("*");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        LinkedList<Element> list = new LinkedList<Element>();

        recurse(list, roundEnv.getRootElements());

        return process(list);
    }

    private void recurse(List<Element> list,
                         Iterable<? extends Element> iterable) {
        for (Element element : iterable) {
            list.add(element);

            recurse(list, element.getEnclosedElements());
        }
    }

    /**
     * Method to process the root {@link Element}s and all enclosed
     * {@link Element}s.
     *
     * @param   iterable        The {@link Iterable} of {@link Element}s to
     *                          process.
     *
     * @return  {@code true} if the annotations are "consumed";
     *          {@code false} otherwise.
     *
     * @see RoundEnvironment#getRootElements()
     */
    protected abstract boolean process(Iterable<? extends Element> iterable);
}
