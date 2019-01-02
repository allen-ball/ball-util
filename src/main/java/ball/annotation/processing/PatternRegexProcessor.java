/*
 * $Id$
 *
 * Copyright 2019 Allen D. Ball.  All rights reserved.
 */
package ball.annotation.processing;

import ball.annotation.PatternRegex;
import ball.annotation.ServiceProviderFor;
import java.util.regex.Pattern;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 * {@link Processor} implementation to check {@link PatternRegex}
 * annotations.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
@For({ PatternRegex.class })
public class PatternRegexProcessor extends AbstractAnnotationProcessor {

    /**
     * Sole constructor.
     */
    public PatternRegexProcessor() { super(); }

    @Override
    protected void process(RoundEnvironment env,
                           TypeElement annotation,
                           Element element) throws Exception {
        Pattern.compile(element.getAnnotation(PatternRegex.class).value());
    }
}
