/*
 * $Id$
 *
 * Copyright 2012 Allen D. Ball.  All rights reserved.
 */
package iprotium.annotation.processing;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import static java.util.Arrays.asList;
import static java.util.Collections.disjoint;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.lang.model.util.ElementFilter.methodsIn;

/**
 * {@link javax.annotation.processing.Processor} implementation to identify
 * overriding {@link java.lang.reflect.Method}s that are not marked with the
 * {@link Override} {@link java.lang.annotation.Annotation}.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class NoOverrideProcessor extends AbstractNoAnnotationProcessor {

    /**
     * Sole constructor.
     */
    public NoOverrideProcessor() { super(); }

    @Override
    protected void process(Iterable<? extends Element> iterable) {
        for (ExecutableElement element : methodsIn(iterable)) {
            if (element.getAnnotation(Override.class) == null
                && disjoint(element.getModifiers(), asList(PRIVATE, STATIC))) {
                ExecutableElement specification = specifiedBy(element);

                if (specification != null) {
                    warning(element,
                            element.getKind() + " specified by "
                            + specification.getEnclosingElement() + "."
                            + specification + " but does not have "
                            + "@" + Override.class.getSimpleName()
                            + " annotation");
                }
            }
        }
    }
}
