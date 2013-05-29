/*
 * $Id$
 *
 * Copyright 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.annotation.processing;

import iprotium.annotation.ConstantInitializer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;

/**
 * {@link javax.annotation.processing.Processor} implementation to compile
 * initializer expresssions marked by the {@link ConstantInitializer}
 * annotation into compile-time constants.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class ConstantInitializerProcessor extends AbstractAnnotationProcessor {

    /**
     * Sole constructor.
     */
    public ConstantInitializerProcessor() { super(ConstantInitializer.class); }

    @Override
    public void process(RoundEnvironment roundEnv,
                        Element element) throws Exception {
        VariableElement variable = (VariableElement) element;

        if (variable.getConstantValue() == null) {
            warning(variable, "initializer is not compile-time constant");
        }
    }
}
