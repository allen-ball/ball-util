/*
 * $Id$
 *
 * Copyright 2012 Allen D. Ball.  All rights reserved.
 */
package iprotium.annotation.processing;

import iprotium.beans.ConvertWith;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;

/**
 * {@link javax.annotation.processing.Processor} implementation to check
 * {@link ConvertWith} annotations.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class ConvertWithProcessor extends AbstractAnnotationProcessor {

    /**
     * Sole constructor.
     */
    public ConvertWithProcessor() { super(ConvertWith.class); }

    @Override
    public void process(RoundEnvironment roundEnv,
                        Element element) throws Exception {
    }
}
