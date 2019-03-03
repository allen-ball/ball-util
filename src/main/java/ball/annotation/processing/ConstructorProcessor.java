/*
 * $Id$
 *
 * Copyright 2013 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.annotation.processing;

import ball.annotation.ServiceProviderFor;
import javax.annotation.processing.Processor;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

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
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
@ForElementKinds({ CLASS })
@NoArgsConstructor @ToString
public class ConstructorProcessor extends AbstractNoAnnotationProcessor {
    @Override
    protected void process(Element element) {
        if (element.getModifiers().contains(ABSTRACT)) {
            TypeElement type = (TypeElement) element;

            constructorsIn(element.getEnclosedElements())
                .stream()
                .filter(t -> t.getModifiers().contains(PUBLIC))
                .filter(t -> type.getAnnotation(AllArgsConstructor.class) == null)
                .filter(t -> type.getAnnotation(NoArgsConstructor.class) == null)
                .filter(t -> type.getAnnotation(RequiredArgsConstructor.class) == null)
                .forEach(t -> print(WARNING,
                                    t,
                                    t.getKind() + " is declared "
                                    + t.getModifiers()
                                    + "; suggest non-" + PUBLIC));
        }
    }
}
