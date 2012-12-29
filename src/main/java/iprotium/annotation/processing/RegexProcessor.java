/*
 * $Id$
 *
 * Copyright 2011, 2012 Allen D. Ball.  All rights reserved.
 */
package iprotium.annotation.processing;

import iprotium.util.Regex;
import java.util.regex.Pattern;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;

/**
 * {@link javax.annotation.processing.Processor} implementation to verify
 * {@link String} initializers marked by the {@link Regex} annotation are
 * valid {@link Pattern}s.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class RegexProcessor extends AbstractAnnotationProcessor {

    /**
     * Sole constructor.
     */
    public RegexProcessor() { super(Regex.class); }

    @Override
    public void process(RoundEnvironment roundEnv,
                        Element element) throws Exception {
        Object regex = ((VariableElement) element).getConstantValue();

        if (regex != null) {
            if (regex instanceof String) {
                Pattern.compile((String) regex);
            } else {
                error(element,
                      "Constant value is not " + String.class.getSimpleName());
            }
        }
    }
}
