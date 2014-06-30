/*
 * $Id$
 *
 * Copyright 2011 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.annotation.processing;

import ball.annotation.ServiceProviderFor;
import ball.util.Regex;
import java.util.regex.Pattern;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import static javax.tools.Diagnostic.Kind.ERROR;

/**
 * {@link javax.annotation.processing.Processor} implementation to verify
 * {@link String} initializers marked by the {@link Regex} annotation are
 * valid {@link Pattern}s.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
@For({ Regex.class })
public class RegexProcessor extends AbstractAnnotationProcessor {

    /**
     * Sole constructor.
     */
    public RegexProcessor() { super(); }

    @Override
    public void process(RoundEnvironment roundEnv,
                        TypeElement annotation,
                        Element element) throws Exception {
        Object regex = ((VariableElement) element).getConstantValue();

        if (regex != null) {
            if (regex instanceof String) {
                Pattern.compile((String) regex);
            } else {
                print(ERROR,
                      element,
                      "Constant value is not " + String.class.getSimpleName());
            }
        }
    }
}
