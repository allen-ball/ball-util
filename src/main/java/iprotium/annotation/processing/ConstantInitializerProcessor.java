/*
 * $Id$
 *
 * Copyright 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.annotation.processing;

import iprotium.annotation.ConstantInitializer;
import iprotium.annotation.ServiceProviderFor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;

import static javax.tools.Diagnostic.Kind.WARNING;

/**
 * {@link Processor} implementation to compile initializer expresssions
 * marked by the {@link ConstantInitializer} annotation into compile-time
 * constants.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
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
            print(WARNING,
                  variable, "initializer is not compile-time constant");
        }
    }
}
