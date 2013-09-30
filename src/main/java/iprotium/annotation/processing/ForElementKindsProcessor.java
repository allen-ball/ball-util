/*
 * $Id$
 *
 * Copyright 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.annotation.processing;

import iprotium.annotation.ServiceProviderFor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 * {@link Processor} implementation to check {@link ForElementKinds}
 * annotations.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
public class ForElementKindsProcessor extends AbstractAnnotationProcessor {
    private TypeElement supertype = null;

    /**
     * Sole constructor.
     */
    public ForElementKindsProcessor() { super(ForElementKinds.class); }

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
