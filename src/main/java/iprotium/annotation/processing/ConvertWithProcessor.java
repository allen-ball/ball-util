/*
 * $Id$
 *
 * Copyright 2012, 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.annotation.processing;

import iprotium.annotation.ServiceProviderFor;
import iprotium.beans.ConvertWith;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;

/**
 * {@link Processor} implementation to check {@link ConvertWith}
 * annotations.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
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
