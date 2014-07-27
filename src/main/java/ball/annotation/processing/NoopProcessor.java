/*
 * $Id$
 *
 * Copyright 2014 Allen D. Ball.  All rights reserved.
 */
package ball.annotation.processing;

import ball.annotation.ServiceProviderFor;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.Set;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;

/**
 * No-op {@link Processor} to silence compilation warnings for 3rd-party
 * {@link java.lang.annotation.Annotation}s that do not have a processor.
 *
 * {@include NoopProcessor.properties}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
public class NoopProcessor extends AbstractProcessor {
    private static final ResourceBundle BUNDLE =
        ResourceBundle.getBundle(NoopProcessor.class.getName());

    /**
     * Sole costructor.
     */
    public NoopProcessor() { super(); }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.unmodifiableSet(BUNDLE.keySet());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        return true;
    }
}
