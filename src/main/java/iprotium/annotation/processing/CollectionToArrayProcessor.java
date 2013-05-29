/*
 * $Id$
 *
 * Copyright 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.annotation.processing;

import java.util.Collection;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.util.ElementFilter.methodsIn;

/**
 * {@link javax.annotation.processing.Processor} implementation to enforce
 * {@link Collection#toArray()} implementation methods return as narrow an
 * {@link Object} as possible.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
@ForElementKinds({ CLASS })
@ForSubclassesOf(Collection.class)
public class CollectionToArrayProcessor extends AbstractNoAnnotationProcessor {
    private ExecutableElement METHOD = null;

    /**
     * Sole constructor.
     */
    public CollectionToArrayProcessor() { super(); }

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        try {
            METHOD = getExecutableElementFor(Collection.class, "toArray");
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }

    @Override
    protected void process(Element element) {
        for (ExecutableElement method :
                 methodsIn(element.getEnclosedElements())) {
            if (overrides(method, METHOD)) {
                check(method);
            }
        }
    }

    private void check(ExecutableElement method) {
        TypeElement type = (TypeElement) method.getEnclosingElement();
    }
}
