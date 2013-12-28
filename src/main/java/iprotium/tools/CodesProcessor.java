/*
 * $Id$
 *
 * Copyright 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.tools;

import iprotium.annotation.ServiceProviderFor;
import iprotium.annotation.processing.AbstractAnnotationProcessor;
import iprotium.annotation.processing.For;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 * {@link javax.annotation.processing.Processor} implementation to check
 * {@link Codes} annotations.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
@For({ Codes.class })
public class CodesProcessor extends AbstractAnnotationProcessor {

    /**
     * Sole constructor.
     */
    public CodesProcessor() { super(); }

    @Override
    public void process(RoundEnvironment roundEnv,
                        TypeElement annotation,
                        Element element) throws Exception {
    }
}
