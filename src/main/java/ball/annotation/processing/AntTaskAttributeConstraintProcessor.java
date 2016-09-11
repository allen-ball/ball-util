/*
 * $Id$
 *
 * Copyright 2014 Allen D. Ball.  All rights reserved.
 */
package ball.annotation.processing;

import ball.annotation.ServiceProviderFor;
import ball.util.ant.taskdefs.AntTaskAttributeConstraint;
import ball.util.ant.taskdefs.NotEmpty;
import ball.util.ant.taskdefs.NotNull;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.tools.Diagnostic.Kind.ERROR;

/**
 * {@link AntTaskAttributeConstraint} {@link Processor}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
@For({ AntTaskAttributeConstraint.class, NotEmpty.class, NotNull.class })
public class AntTaskAttributeConstraintProcessor
             extends AbstractAnnotationProcessor {

    /**
     * Sole constructor.
     */
    public AntTaskAttributeConstraintProcessor() { super(); }

    @Override
    public void process(RoundEnvironment roundEnv,
                        TypeElement annotation,
                        Element element) throws Exception {
        switch (element.getKind()) {
        case ANNOTATION_TYPE:
        case FIELD:
        default:
            break;

        case METHOD:
            if (! isGetterMethod((ExecutableElement) element)) {
                print(ERROR,
                      element,
                      element.getKind() + " annotated with "
                      + AT + annotation.getSimpleName()
                      + " but is not a property getter method");
            }
            break;
        }
    }
}
