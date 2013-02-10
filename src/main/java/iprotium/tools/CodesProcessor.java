/*
 * $Id$
 *
 * Copyright 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.tools;

import iprotium.annotation.processing.AbstractAnnotationProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;

/**
 * {@link javax.annotation.processing.Processor} implementation to check
 * {@link Codes} annotations.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class CodesProcessor extends AbstractAnnotationProcessor {

    /**
     * Sole constructor.
     */
    public CodesProcessor() { super(Codes.class); }

    @Override
    public void process(RoundEnvironment roundEnv,
                        Element element) throws Exception {
    }
}