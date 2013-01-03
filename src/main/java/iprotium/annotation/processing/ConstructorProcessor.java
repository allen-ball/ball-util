/*
 * $Id$
 *
 * Copyright 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.annotation.processing;

import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.util.ElementFilter.constructorsIn;
import static javax.lang.model.util.ElementFilter.typesIn;

/**
 * {@link javax.annotation.processing.Processor} implementation to enforce
 * that {@link Class}es have:
 * <ol>
 *   <li value="1">
 *     An explicitly declared {@link java.lang.reflect.Constructor}
 *     (NOT YET IMPLEMENTED)
 *   </li>
 *   <li value="2">
 *     When abstract, no {@code public}
 *     {@link java.lang.reflect.Constructor}s
 *   </li>
 * </ol>
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class ConstructorProcessor extends AbstractNoAnnotationProcessor {

    /**
     * Sole constructor.
     */
    public ConstructorProcessor() { super(); }

    @Override
    protected void process(Iterable<? extends Element> iterable) {
        for (TypeElement type : typesIn(iterable)) {
            List<? extends ExecutableElement> list =
                constructorsIn(type.getEnclosedElements());

            for (ExecutableElement constructor : list) {
                if (type.getModifiers().contains(ABSTRACT)) {
                    if (constructor.getModifiers().contains(PUBLIC)) {
                        warning(constructor,
                                constructor.getKind() + " is declared "
                                + constructor.getModifiers()
                                + "; suggest non-" + PUBLIC);
                    }
                }
            }
        }
    }
}
