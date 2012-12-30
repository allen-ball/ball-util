/*
 * $Id$
 *
 * Copyright 2012 Allen D. Ball.  All rights reserved.
 */
package iprotium.annotation.processing;

import java.util.ArrayList;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.lang.model.util.ElementFilter.methodsIn;

/**
 * {@link javax.annotation.processing.Processor} implementation to identify
 * overriding {@link java.lang.reflect.Method}s that are not marked with the
 * {@link Override} {@link java.lang.annotation.Annotation}.
 *
 * @see javax.lang.model.util.Elements#overrides(ExecutableElement,ExecutableElement,TypeElement)
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
    protected boolean process(Iterable<? extends Element> iterable) {
        for (ExecutableElement element : methodsIn(iterable)) {
            if (element.getAnnotation(Override.class) == null
                && (! element.getModifiers().contains(PRIVATE))
                && (! element.getModifiers().contains(STATIC))) {
                TypeElement type = (TypeElement) element.getEnclosingElement();
                ExecutableElement overridden = null;
                ArrayList<TypeMirror> list = new ArrayList<TypeMirror>();

                list.add(type.getSuperclass());
                list.addAll(type.getInterfaces());

                for (TypeMirror superclass : list) {
                    overridden =
                        overrides(element, types.asElement(superclass));

                    if (overridden != null) {
                        break;
                    }
                }

                if (overridden != null) {
                    warning(element,
                            element.getKind() + " overrides " + overridden
                            + " in " + overridden.getEnclosingElement()
                            + " but does not have "
                            + "@" + Override.class.getSimpleName()
                            + " annotation");
                }
            }
        }

        return true;
    }

    private ExecutableElement overrides(ExecutableElement overrider,
                                        Element type) {
        ExecutableElement overridden = null;

        if (overridden == null) {
            if (type != null) {
                switch (type.getKind()) {
                case CLASS:
                case INTERFACE:
                    overridden = overridden(overrider, (TypeElement) type);
                    break;

                default:
                    break;
                }
            }
        }

        return overridden;
    }

    private ExecutableElement overridden(ExecutableElement overrider,
                                         TypeElement type) {
        ExecutableElement overridden = null;

        if (overridden == null) {
            for (ExecutableElement method :
                     methodsIn(type.getEnclosedElements())) {
                if ((! method.getModifiers().contains(PRIVATE))
                    && (! method.getModifiers().contains(STATIC))) {
                    if (elements.overrides(overrider, method, type)) {
                        overridden = method;
                        break;
                    }
                }
            }
        }

        if (overridden == null) {
            overridden =
                overrides(overrider, types.asElement(type.getSuperclass()));
        }

        return overridden;
    }
}
