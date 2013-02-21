/*
 * $Id$
 *
 * Copyright 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.annotation.processing;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;

/**
 * {@link javax.annotation.processing.Processor} implementation to check
 * {@link ForElementKinds} annotations.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class ForElementKindsProcessor extends AbstractAnnotationProcessor {

    /**
     * Sole constructor.
     */
    public ForElementKindsProcessor() { super(ForElementKinds.class); }

    @Override
    public void process(RoundEnvironment roundEnv,
                        Element element) throws Exception {
    }
}
