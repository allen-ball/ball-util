/*
 * $Id$
 *
 * Copyright 2018 Allen D. Ball.  All rights reserved.
 */
package ball.annotation.processing;

import ball.annotation.ServiceProviderFor;
import ball.util.ant.taskdefs.AntTaskMixIn;
import javax.annotation.processing.Processor;
import javax.lang.model.element.Element;
import org.apache.tools.ant.Task;

import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.tools.Diagnostic.Kind.ERROR;

/**
 * {@link Processor} implementation to verify concrete implementations of
 * {@link AntTaskMixIn} are also subclasses of {@link Task}.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
@ForSubclassesOf(AntTaskMixIn.class)
public class AntTaskMixInProcessor extends AbstractNoAnnotationProcessor {

    /**
     * Sole constructor.
     */
    public AntTaskMixInProcessor() { super(); }

    @Override
    protected void process(Element element) {
        switch (element.getKind()) {
        case CLASS:
            if (! element.getModifiers().contains(ABSTRACT)) {
                if (! isAssignable(element.asType(), Task.class)) {
                    print(ERROR,
                          element,
                          element.getKind() + " implements "
                          + AntTaskMixIn.class.getName()
                          + " but is not a subclass of "
                          + Task.class.getName());
                }
            }
            break;

        default:
            break;
        }
    }
}
