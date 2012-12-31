/*
 * $Id$
 *
 * Copyright 2012 Allen D. Ball.  All rights reserved.
 */
package iprotium.annotation.processing;

import java.util.ArrayList;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static java.util.Arrays.asList;
import static java.util.Collections.disjoint;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.lang.model.util.ElementFilter.methodsIn;
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
     * @param   message         The message {@link CharSequence}.
     */
    protected void error(Element element, CharSequence message) {
        processingEnv.getMessager().printMessage(ERROR, message, element);
    }

    /**
     * Method to print a warning.
     *
     * @param   element         The offending {@link Element}.
     * @param   message         The message {@link CharSequence}.
     */
    protected void warning(Element element, CharSequence message) {
        processingEnv.getMessager().printMessage(WARNING, message, element);
    }

    /**
     * Method to return the {@link ExecutableElement}
     * ({@link java.lang.reflect.Method}) the argument
     * {@link ExecutableElement} is specified by (if any).
     *
     * @param   method          The {@link ExecutableElement}.
     *
     * @return  The specification {@link ExecutableElement} if any;
     *          {@code null} otherwise.
     *
     * @see #overrides(ExecutableElement)
     */
    protected ExecutableElement specifiedBy(ExecutableElement method) {
        ExecutableElement specification = overrides(method);

        if (specification != null) {
            for (;;) {
                ExecutableElement overridden = overrides(specification);

                if (overridden != null) {
                    specification = overridden;
                } else {
                    break;
                }
            }
        }

        return specification;
    }

    /**
     * Method to return the {@link ExecutableElement}
     * ({@link java.lang.reflect.Method}) the argument
     * {@link ExecutableElement} overrides (if any).
     *
     * @param   overrider       The {@link ExecutableElement}.
     *
     * @return  The overridden {@link ExecutableElement} if any;
     *          {@code null} otherwise.
     *
     * @see Elements#overrides(ExecutableElement,ExecutableElement,TypeElement)
     */
    protected ExecutableElement overrides(ExecutableElement overrider) {
        ExecutableElement overridden = null;
        TypeElement type = (TypeElement) overrider.getEnclosingElement();
        ArrayList<TypeMirror> superclasses = new ArrayList<TypeMirror>();

        superclasses.add(type.getSuperclass());
        superclasses.addAll(type.getInterfaces());

        for (TypeMirror superclass : superclasses) {
            overridden = overrides(overrider, types.asElement(superclass));

            if (overridden != null) {
                break;
            }
        }

        return overridden;
    }

    private ExecutableElement overrides(ExecutableElement overrider,
                                        Element type) {
        ExecutableElement overridden = null;

        if (overridden == null) {
            if (type != null) {
                switch (type.getKind()) {
                case CLASS:
                case INTERFACE:
                    overridden = overridden(overrider, (TypeElement) type);
                    break;

                default:
                    break;
                }
            }
        }

        return overridden;
    }

    private ExecutableElement overridden(ExecutableElement overrider,
                                         TypeElement type) {
        ExecutableElement overridden = null;

        if (overridden == null) {
            for (ExecutableElement method :
                     methodsIn(type.getEnclosedElements())) {
                if (disjoint(method.getModifiers(), asList(PRIVATE, STATIC))) {
                    if (elements.overrides(overrider, method, type)) {
                        overridden = method;
                        break;
                    }
                }
            }
        }

        if (overridden == null) {
            overridden =
                overrides(overrider, types.asElement(type.getSuperclass()));
        }

        return overridden;
    }
}
