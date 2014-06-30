/*
 * $Id$
 *
 * Copyright 2013, 2014 Allen D. Ball.  All rights reserved.
 */
package ball.annotation.processing;

import ball.annotation.ServiceProviderFor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import static javax.tools.Diagnostic.Kind.ERROR;

/**
 * {@link Processor} implementation to check {@link ForElementKinds}
 * annotations.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
@For({ ForElementKinds.class })
public class ForElementKindsProcessor extends AbstractAnnotationProcessor {
    private TypeElement supertype = null;

    /**
     * Sole constructor.
     */
    public ForElementKindsProcessor() { super(); }

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        supertype = getTypeElementFor(AbstractNoAnnotationProcessor.class);
    }

    @Override
    public void process(RoundEnvironment roundEnv,
                        TypeElement annotation,
                        Element element) throws Exception {
        switch (element.getKind()) {
        case CLASS:
            if (! isAssignable(element, supertype)) {
                print(ERROR,
                      element,
                      element.getKind() + " annotated with "
                      + AT + annotation.getSimpleName()
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
