/*
 * $Id$
 *
 * Copyright 2013, 2014 Allen D. Ball.  All rights reserved.
 */
package ball.annotation.processing;

import ball.annotation.ServiceProviderFor;
import java.util.ArrayList;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import static javax.lang.model.util.ElementFilter.methodsIn;
import static javax.tools.Diagnostic.Kind.WARNING;

/**
 * {@link Processor} implementation to check {@link Object#clone()}
 * implementations to verify:
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
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
@ForSubclassesOf(Object.class)
public class ObjectCloneProcessor extends AbstractNoAnnotationProcessor {
    private ExecutableElement METHOD = null;
    private TypeElement THROWABLE = null;

    /**
     * Sole constructor.
     */
    public ObjectCloneProcessor() { super(); }

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        try {
            METHOD = getExecutableElementFor(Object.class, "clone");
            THROWABLE = getTypeElementFor(Cloneable.class);
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }

    @Override
    protected void process(Element element) {
        TypeElement type = (TypeElement) element;

        for (ExecutableElement method :
                 methodsIn(type.getEnclosedElements())) {
            if (overrides(method, METHOD)) {
                check(method);
            }
        }
    }

    private void check(ExecutableElement method) {
        TypeElement type = (TypeElement) method.getEnclosingElement();

        if (! type.getInterfaces().contains(THROWABLE.asType())) {
            print(WARNING,
                  type,
                  type.getKind() + " overrides "
                  + METHOD.getEnclosingElement().getSimpleName()
                  + DOT + METHOD.toString() + " but does not implement "
                  + THROWABLE.getSimpleName());
        }

        if (! types.isAssignable(method.getReturnType(), type.asType())) {
            print(WARNING,
                  method,
                  method.getKind() + " overrides "
                  + METHOD.getEnclosingElement().getSimpleName()
                  + DOT + METHOD.toString()
                  + " but does not return a subclass of "
                  + type.getSimpleName());
        }

        ArrayList<TypeMirror> throwables = new ArrayList<TypeMirror>();

        throwables.addAll(METHOD.getThrownTypes());
        throwables.retainAll(overrides(method).getThrownTypes());
        throwables.removeAll(method.getThrownTypes());

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

            print(WARNING,
                  method,
                  method.getKind() + " overrides "
                  + METHOD.getEnclosingElement().getSimpleName()
                  + DOT + METHOD.toString() + " but does not throw " + name);
        }
    }
}
