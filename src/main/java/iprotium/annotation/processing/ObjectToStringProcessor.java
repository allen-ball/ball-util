/*
 * $Id$
 *
 * Copyright 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.annotation.processing;

import iprotium.annotation.ServiceProviderFor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.Modifier.ABSTRACT;

/**
 * {@link javax.annotation.processing.Processor} implementation to check
 * {@link Class}es to verify:
 * <ol>
 *   <li value="1">
 *     The implementing {@link Class} also overrides {@link Object#toString()}
 *   </li>
 * </ol>
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
@ForElementKinds({ CLASS })
@ForModifiers(exclude = true, value = { ABSTRACT })
@ForSubclassesOf(Object.class)
public class ObjectToStringProcessor extends AbstractNoAnnotationProcessor {
    private ExecutableElement METHOD = null;

    /**
     * Sole constructor.
     */
    public ObjectToStringProcessor() { super(); }

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        try {
            METHOD = getExecutableElementFor(Object.class, "toString");
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }

    @Override
    protected void process(Element element) {
        TypeElement type = (TypeElement) element;
        ExecutableElement method = implementationOf(METHOD, type);

        if (method == null || METHOD.equals(method)) {
            warning(type,
                    type.getKind() + " does not override "
                    + METHOD.getEnclosingElement().getSimpleName()
                    + DOT + METHOD.toString());
        }
    }
}
