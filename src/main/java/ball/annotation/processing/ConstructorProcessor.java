/*
 * $Id$
 *
 * Copyright 2013, 2014 Allen D. Ball.  All rights reserved.
 */
package ball.annotation.processing;

import ball.annotation.ServiceProviderFor;
import javax.annotation.processing.Processor;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.util.ElementFilter.constructorsIn;
import static javax.tools.Diagnostic.Kind.WARNING;

/**
 * {@link Processor} implementation to enforce that {@link Class}es have:
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
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
@ForElementKinds({ CLASS })
public class ConstructorProcessor extends AbstractNoAnnotationProcessor {

    /**
     * Sole constructor.
     */
    public ConstructorProcessor() { super(); }

    @Override
    protected void process(Element element) {
        for (ExecutableElement constructor :
                 constructorsIn(element.getEnclosedElements())) {
            if (element.getModifiers().contains(ABSTRACT)) {
                if (constructor.getModifiers().contains(PUBLIC)) {
                    print(WARNING,
                          constructor,
                          constructor.getKind() + " is declared "
                          + constructor.getModifiers()
                          + "; suggest non-" + PUBLIC);
                }
            }
        }
    }
}