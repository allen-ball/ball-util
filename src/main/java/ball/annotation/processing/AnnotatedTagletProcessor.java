/*
 * $Id$
 *
 * Copyright 2019 Allen D. Ball.  All rights reserved.
 */
package ball.annotation.processing;

import ball.annotation.ServiceProviderFor;
import ball.tools.javadoc.AnnotatedTaglet;
import com.sun.tools.doclets.Taglet;
import javax.annotation.processing.Processor;
import javax.lang.model.element.Element;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.tools.Diagnostic.Kind.ERROR;

/**
 * {@link Processor} implementation to verify concrete implementations of
 * {@link AnnotatedTaglet} are also subclasses of {@link Taglet}.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
@ForElementKinds({ CLASS })
@ForSubclassesOf(AnnotatedTaglet.class)
public class AnnotatedTagletProcessor extends AbstractNoAnnotationProcessor {

    /**
     * Sole constructor.
     */
    public AnnotatedTagletProcessor() { super(); }

    @Override
    protected void process(Element element) {
        if (! element.getModifiers().contains(ABSTRACT)) {
            if (! isAssignable(element.asType(), Taglet.class)) {
                print(ERROR,
                      element,
                      element.getKind()
                      + " implements " + AnnotatedTaglet.class.getName()
                      + " but is not a subclass of " + Taglet.class.getName());
            }
        }
    }
}
