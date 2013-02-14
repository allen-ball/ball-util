/*
 * $Id$
 *
 * Copyright 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.annotation.processing;

import java.util.ArrayList;
import java.util.Set;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import static javax.lang.model.util.ElementFilter.methodsIn;

/**
 * {@link javax.annotation.processing.Processor} implementation to check
 * {@link Object#clone()} implementations to verify:
 * <ol>
 *   <li value="1">
 *     The implementing {@link Class} also implements {@link Cloneable}
 *   </li>
 *   <li value="2">
 *     The implementation throws {@link CloneNotSupportedException} (unless
 *     some "intravening" superclass' implementation does not)
 *   </li>
 *   <li value="3">
 *     The implementation returns a subtype of the implementation type
 *   </li>
 * </ol>
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class ObjectCloneProcessor extends AbstractNoAnnotationProcessor {
    private TypeElement OBJECT = null;
    private TypeElement CLONEABLE = null;
    private ExecutableElement CLONE = null;

    /**
     * Sole constructor.
     */
    public ObjectCloneProcessor() { super(); }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        synchronized (this) {
            if (OBJECT == null) {
                OBJECT = getTypeElement(Object.class);
                CLONEABLE = getTypeElement(Cloneable.class);

                for (ExecutableElement method :
                         methodsIn(OBJECT.getEnclosedElements())) {
                    if (method.getSimpleName().contentEquals("clone")
                        && method.getParameters().isEmpty()) {
                        CLONE = method;
                        break;
                    }
                }
            }
        }

        return super.process(annotations, roundEnv);
    }

    @Override
    protected void process(Iterable<? extends Element> iterable) {
        for (ExecutableElement method : methodsIn(iterable)) {
            if (overrides(method, CLONE)) {
                check(method);
            }
        }
    }

    private void check(ExecutableElement clone) {
        TypeElement type = (TypeElement) clone.getEnclosingElement();

        if (! type.getInterfaces().contains(CLONEABLE.asType())) {
            warning(type,
                    type.getKind() + " overrides "
                    + OBJECT.getSimpleName() + "." + CLONE
                    + " but does not implement "
                    + CLONEABLE.getQualifiedName());
        }

        if (! types.isAssignable(clone.getReturnType(), type.asType())) {
            warning(clone,
                    clone.getKind() + " overrides "
                    + OBJECT.getSimpleName() + "." + CLONE
                    + " but does not return a subclass of "
                    + type.getSimpleName());
        }

        ArrayList<TypeMirror> throwables = new ArrayList<TypeMirror>();

        throwables.addAll(CLONE.getThrownTypes());
        throwables.retainAll(overrides(clone).getThrownTypes());
        throwables.removeAll(clone.getThrownTypes());

        for (TypeMirror mirror : throwables) {
            Element element = types.asElement(mirror);
            CharSequence name = null;

            switch (element.getKind()) {
            case CLASS:
                name = ((TypeElement) element).getQualifiedName();
                break;

            default:
                name = element.getSimpleName();
                break;
            }

            warning(clone,
                    clone.getKind() + " overrides "
                    + OBJECT.getSimpleName() + "." + CLONE
                    + " but does not throw " + name);
        }
    }
}
