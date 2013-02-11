/*
 * $Id$
 *
 * Copyright 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.annotation.processing;

import java.util.Set;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import static javax.lang.model.util.ElementFilter.methodsIn;

/**
 * {@link javax.annotation.processing.Processor} implementation to check
 * {@link Object#clone()} implementations to verify:
 * <ol>
 *   <li value="1">
 *     The implementing {@link Class} also implements {@link Cloneable}
 *   </li>
 *   <li value="2">
 *     The implementation throws {@link CloneNotSupportedException}
 *   </li>
 * </ol>
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class ObjectCloneProcessor extends AbstractNoAnnotationProcessor {
    private TypeElement TYPE = null;
    private ExecutableElement METHOD = null;
    private TypeElement INTERFACE = null;
    private TypeElement THROWABLE = null;

    /**
     * Sole constructor.
     */
    public ObjectCloneProcessor() { super(); }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        if (TYPE == null) {
            TYPE = getTypeElement(Object.class);

            for (ExecutableElement method :
                     methodsIn(TYPE.getEnclosedElements())) {
                if (method.getSimpleName().contentEquals("clone")
                    && method.getParameters().isEmpty()) {
                    METHOD = method;
                    break;
                }
            }

            INTERFACE = getTypeElement(Cloneable.class);
            THROWABLE = getTypeElement(CloneNotSupportedException.class);
        }

        return super.process(annotations, roundEnv);
    }

    @Override
    protected void process(Iterable<? extends Element> iterable) {
        for (ExecutableElement method : methodsIn(iterable)) {
            if (overrides(method, METHOD)) {
                TypeElement type = (TypeElement) method.getEnclosingElement();

                if (! type.getInterfaces().contains(INTERFACE.asType())) {
                    warning(type,
                            type.getKind() + " overrides "
                            + TYPE.getSimpleName() + "." + METHOD
                            + " but does not implement "
                            + INTERFACE.getQualifiedName());
                }

                if (! method.getThrownTypes().contains(THROWABLE.asType())) {
                    warning(method,
                            method.getKind() + " overrides "
                            + TYPE.getSimpleName() + "." + METHOD
                            + " but does not throw "
                            + THROWABLE.getQualifiedName());
                }
            }
        }
    }
}
