/*
 * $Id$
 *
 * Copyright 2011 Allen D. Ball.  All rights reserved.
 */
package iprotium.annotation.processing;

import iprotium.util.Regex;
import java.util.Collections;
import java.util.Set;
import java.util.regex.Pattern;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;

import static javax.tools.Diagnostic.Kind.ERROR;

/**
 * {@link javax.annotation.processing.Processor} implementation to verify
 * {@link String} initializers marked by the {@link Regex} annotation are
 * valid {@link Pattern}s.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class RegexProcessor extends AbstractProcessor {

    /**
     * Sole constructor.
     */
    public RegexProcessor() { super(); }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(Regex.class.getCanonicalName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        for (Element element :
                 roundEnv.getElementsAnnotatedWith(Regex.class)) {
            try {
                Object regex = ((VariableElement) element).getConstantValue();

                if (regex != null) {
                    Pattern.compile((String) regex);
                }
            } catch (Exception exception) {
                processingEnv.getMessager()
                    .printMessage(ERROR, exception.getMessage(), element);
            }
        }

        return true;
    }
}
