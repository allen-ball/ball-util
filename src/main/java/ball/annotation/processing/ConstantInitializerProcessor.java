/*
 * $Id$
 *
 * Copyright 2013 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.annotation.processing;

import ball.annotation.ConstantInitializer;
import ball.annotation.ServiceProviderFor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
@For({ ConstantInitializer.class })
@NoArgsConstructor @ToString
public class ConstantInitializerProcessor extends AbstractAnnotationProcessor {
    @Override
    public void process(RoundEnvironment roundEnv,
                        TypeElement annotation,
                        Element element) throws Exception {
        VariableElement variable = (VariableElement) element;

        if (variable.getConstantValue() == null) {
            print(WARNING,
                  variable, "initializer is not compile-time constant");
        }
    }
}
