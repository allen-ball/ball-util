/*
 * $Id$
 *
 * Copyright 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.annotation.processing;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 * {@link javax.annotation.processing.Processor} implementation to check
 * {@link ForSubclassesOf} annotations.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class ForSubclassesOfProcessor extends AbstractAnnotationProcessor {
    private TypeElement supertype = null;

    /**
     * Sole constructor.
     */
    public ForSubclassesOfProcessor() { super(ForSubclassesOf.class); }

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        supertype = getTypeElementFor(AbstractNoAnnotationProcessor.class);
    }

    @Override
    public void process(RoundEnvironment roundEnv,
                        Element element) throws Exception {
        switch (element.getKind()) {
        case CLASS:
            if (! isAssignable(element, supertype)) {
                error(element,
                      element.getKind() + " annotated with "
                      + AT + type.getSimpleName()
                      + " but is not a subclass of "
                      + supertype.getQualifiedName());
            }
            break;

        default:
            throw new IllegalStateException();
            /* break; */
        }
    }
}