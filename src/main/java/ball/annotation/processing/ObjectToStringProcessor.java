/*
 * $Id$
 *
 * Copyright 2013 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.annotation.processing;

import ball.annotation.ServiceProviderFor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.WARNING;

/**
 * {@link javax.annotation.processing.Processor} implementation to check
 * {@link Class}es to verify:
 * <ol>
 *   <li value="1">
 *     The implementing {@link Class} also overrides {@link Object#toString()}
 *   </li>
 * </ol>
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
@ForElementKinds({ CLASS })
@ForSubclassesOf(Object.class)
@NoArgsConstructor @ToString
public class ObjectToStringProcessor extends AbstractNoAnnotationProcessor {
    private ExecutableElement METHOD = null;

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        try {
            METHOD = getExecutableElementFor(Object.class, "toString");
        } catch (Exception exception) {
            print(ERROR, null, exception);
        }
    }

    @Override
    protected void process(Element element) {
        if (! element.getModifiers().contains(ABSTRACT)) {
            TypeElement type = (TypeElement) element;

            if (type.getAnnotation(ToString.class) == null) {
                ExecutableElement method = implementationOf(METHOD, type);

                if (method == null || METHOD.equals(method)) {
                    print(WARNING,
                          type,
                          type.getKind() + " does not override "
                          + METHOD.getEnclosingElement().getSimpleName()
                          + DOT + METHOD.toString());
                }
            }
        }
    }
}
