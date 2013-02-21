/*
 * $Id$
 *
 * Copyright 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.annotation.processing;

import java.util.ArrayList;
import javax.annotation.processing.ProcessingEnvironment;
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
@ForSubclassesOf(Object.class)
public class ObjectCloneProcessor extends AbstractNoAnnotationProcessor {
    private ExecutableElement CLONE = null;
    private TypeElement OBJECT = null;
    private TypeElement CLONEABLE = null;

    /**
     * Sole constructor.
     */
    public ObjectCloneProcessor() { super(); }

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        try {
            CLONE = getExecutableElementFor(Object.class, "clone");
            OBJECT = (TypeElement) CLONE.getEnclosingElement();
            CLONEABLE = getTypeElementFor(Cloneable.class);
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }

    @Override
    protected void process(Element element) {
        for (ExecutableElement method :
                 methodsIn(element.getEnclosedElements())) {
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
                    + OBJECT.getSimpleName() + DOT + CLONE.toString()
                    + " but does not implement " + CLONEABLE.getSimpleName());
        }

        if (! types.isAssignable(clone.getReturnType(), type.asType())) {
            warning(clone,
                    clone.getKind() + " overrides "
                    + OBJECT.getSimpleName() + DOT + CLONE.toString()
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
                    + OBJECT.getSimpleName() + DOT + CLONE.toString()
                    + " but does not throw " + name);
        }
    }
}
