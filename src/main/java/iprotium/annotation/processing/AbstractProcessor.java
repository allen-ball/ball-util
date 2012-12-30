/*
 * $Id$
 *
 * Copyright 2012 Allen D. Ball.  All rights reserved.
 */
package iprotium.annotation.processing;

import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.WARNING;

/**
 * Extends {@link javax.annotation.processing.AbstractProcessor} by
 * providing a {@link #getSupportedSourceVersion()} implementation, methods
 * to report {@link javax.tools.Diagnostic.Kind#ERROR}s and
 * {@link javax.tools.Diagnostic.Kind#WARNING}s, and access to
 * {@link ProcessingEnvironment#getElementUtils()} and
 * {@link ProcessingEnvironment#getTypeUtils()}.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public abstract class AbstractProcessor
                      extends javax.annotation.processing.AbstractProcessor {
    /** See {@link ProcessingEnvironment#getElementUtils()}. */
    protected Elements elements = null;
    /** See {@link ProcessingEnvironment#getTypeUtils()}. */
    protected Types types = null;

    /**
     * Sole constructor.
     */
    protected AbstractProcessor() { super(); }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * See {@link #elements} and {@link #types}.
     */
    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        elements = processingEnv.getElementUtils();
        types = processingEnv.getTypeUtils();
    }

    @Override
    public abstract boolean process(Set<? extends TypeElement> annotations,
                                    RoundEnvironment roundEnv);

    /**
     * Method to print an error.
     *
     * @param   element         The offending {@link Element}.
     * @param   message         The message {@link String}.
     */
    protected void error(Element element, String message) {
        processingEnv.getMessager().printMessage(ERROR, message, element);
    }

    /**
     * Method to print a warning.
     *
     * @param   element         The offending {@link Element}.
     * @param   message         The message {@link String}.
     */
    protected void warning(Element element, String message) {
        processingEnv.getMessager().printMessage(WARNING, message, element);
    }
}
