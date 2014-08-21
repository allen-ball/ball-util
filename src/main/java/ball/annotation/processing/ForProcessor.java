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
 * {@link Processor} implementation to check {@link For} annotations.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
@For({ For.class })
public class ForProcessor extends AbstractAnnotationProcessor {
    private TypeElement supertype = null;

    /**
     * Sole constructor.
     */
    public ForProcessor() { super(); }

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        supertype = getTypeElementFor(AbstractAnnotationProcessor.class);
    }

    @Override
    public void process(RoundEnvironment roundEnv,
                        TypeElement annotation,
                        Element element) throws Exception {
        switch (element.getKind()) {
        case CLASS:
            if (! isAssignable(element.asType(), supertype.asType())) {
                print(ERROR,
                      element,
                      element.getKind() + " annotated with "
                      + AT + annotation.getSimpleName()
                      + " but is not a subclass of "
                      + supertype.getQualifiedName());
            }
            break;

        default:
            break;
        }
    }
}
