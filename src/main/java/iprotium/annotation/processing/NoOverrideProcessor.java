/*
 * $Id$
 *
 * Copyright 2012, 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.annotation.processing;

import iprotium.annotation.ServiceProviderFor;
import javax.annotation.processing.Processor;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import static java.util.Arrays.asList;
import static java.util.Collections.disjoint;
import static javax.lang.model.element.ElementKind.METHOD;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.STATIC;

/**
 * {@link Processor} implementation to identify overriding
 * {@link java.lang.reflect.Method}s that are not marked with the
 * {@link Override} {@link java.lang.annotation.Annotation}.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
@ForElementKinds({ METHOD })
public class NoOverrideProcessor extends AbstractNoAnnotationProcessor {

    /**
     * Sole constructor.
     */
    public NoOverrideProcessor() { super(); }

    @Override
    protected void process(Element element) {
        ExecutableElement method = (ExecutableElement) element;

        if (method.getAnnotation(Override.class) == null
            && disjoint(method.getModifiers(), asList(PRIVATE, STATIC))) {
            ExecutableElement specification = specifiedBy(method);

            if (specification != null) {
                warning(method,
                        method.getKind() + " specified by "
                        + specification.getEnclosingElement() + DOT
                        + specification.toString() + " but does not have "
                        + AT + Override.class.getSimpleName()
                        + " annotation");
            }
        }
    }
}
