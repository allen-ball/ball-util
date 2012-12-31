/*
 * $Id$
 *
 * Copyright 2012 Allen D. Ball.  All rights reserved.
 */
package iprotium.annotation.processing;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Set;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 * Abstract {@link javax.annotation.processing.Processor} base class for
 * processing an {@link Annotation}.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public abstract class AbstractAnnotationProcessor extends AbstractProcessor {
    protected final Class<? extends Annotation> type;

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
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(type.getCanonicalName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
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

    @Override
    protected void error(Element element, CharSequence message) {
        super.error(element, format(message));
    }

    @Override
    protected void warning(Element element, CharSequence message) {
        super.warning(element, format(message));
    }

    private CharSequence format(CharSequence message) {
        CharSequence sequence =
            new StringBuilder()
            .append("@").append(type.getCanonicalName())
            .append(": ").append(message);

        return sequence;
    }
}
