/*
 * $Id$
 *
 * Copyright 2012 Allen D. Ball.  All rights reserved.
 */
package iprotium.annotation.processing;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.WARNING;

/**
 * Abstract {@link javax.annotation.processing.Processor} base class for
 * processing an {@link Annotation}.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public abstract class AbstractAnnotationProcessor extends AbstractProcessor {
    protected final Class<? extends Annotation> type;
    protected Elements elements = null;
    protected Types types = null;

    /**
     * Sole constructor.
     *
     * @param   type            The {@link Annotation} {@link Class} to
     *                          process.
     */
    protected AbstractAnnotationProcessor(Class<? extends Annotation> type) {
        super();

        if (type != null) {
            this.type = type;
        } else {
            throw new NullPointerException("type");
        }
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(type.getCanonicalName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        elements = processingEnv.getElementUtils();
        types = processingEnv.getTypeUtils();

        for (TypeElement type : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(type)) {
                try {
                    process(roundEnv, element);
                } catch (Exception exception) {
                    error(element, exception.getMessage());
                }
            }
        }

        return true;
    }

    /**
     * Callback method to process an annotated {@link Element}.
     *
     * @param   roundEnv        The {@link RoundEnvironment}.
     * @param   element         The annotated {@link Element}.
     */
    protected abstract void process(RoundEnvironment roundEnv,
                                    Element element) throws Exception;

    /**
     * Method to print an error.
     *
     * @param   element         The offending {@link Element}.
     * @param   message         The message {@link String}.
     */
    protected void error(Element element, String message) {
        processingEnv.getMessager()
            .printMessage(ERROR, format(message), element);
    }

    /**
     * Method to print a warning.
     *
     * @param   element         The offending {@link Element}.
     * @param   message         The message {@link String}.
     */
    protected void warning(Element element, String message) {
        processingEnv.getMessager()
            .printMessage(WARNING, format(message), element);
    }

    private String format(String message) {
        return "@" + type.getCanonicalName() + ": " + message;
    }
}
