/*
 * $Id$
 *
 * Copyright 2012 - 2018 Allen D. Ball.  All rights reserved.
 */
package ball.annotation.processing;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import static javax.tools.Diagnostic.Kind.ERROR;

/**
 * Abstract {@link javax.annotation.processing.Processor} base class for
 * processing an {@link Annotation}.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class AbstractAnnotationProcessor extends AbstractProcessor {
    private final List<Class<? extends Annotation>> list;
    private transient TypeElement annotation = null;

    /**
     * Sole non-deprecated costructor.  Implementing class must be annotated
     * with {@link For}.
     */
    protected AbstractAnnotationProcessor() {
        super();

        try {
            list = Arrays.asList(getClass().getAnnotation(For.class).value());
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    /**
     * Use {@link #AbstractAnnotationProcessor()} with {@link For}
     * {@link Annotation} instead.
     *
     * @param   type            The {@link Annotation} {@link Class} to
     *                          process.
     */
    @Deprecated
    protected AbstractAnnotationProcessor(Class<? extends Annotation> type) {
        super();

        if (type != null) {
            list =
                Collections.<Class<? extends Annotation>>singletonList(type);
        } else {
            throw new NullPointerException("type");
        }
    }

    /**
     * Method to get the {@link List} of supported {@link Annotation}
     * {@link Class}es.
     *
     * @return  The {@link List} of supported {@link Annotation}
     *          {@link Class}es.
     */
    protected List<Class<? extends Annotation>> getSupportedAnnotationTypeList() {
        return list;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set =
            getSupportedAnnotationTypeList()
            .stream()
            .map(t -> t.getCanonicalName())
            .collect(Collectors.toSet());

        return Collections.unmodifiableSet(set);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            try {
                this.annotation = annotation;

                for (Element element :
                         roundEnv.getElementsAnnotatedWith(annotation)) {
                    try {
                        process(roundEnv, annotation, element);
                    } catch (Exception exception) {
                        print(ERROR, element, exception);
                    }
                }
            } finally {
                this.annotation = null;
            }
        }

        return true;
    }

    /**
     * Callback method to process an annotated {@link Element}.
     *
     * @param   roundEnv        The {@link RoundEnvironment}.
     * @param   annotation      The annotation {@link TypeElement}.
     * @param   element         The annotated {@link Element}.
     *
     * @throws  Exception       If an exception occurs.
     */
    protected abstract void process(RoundEnvironment roundEnv,
                                    TypeElement annotation,
                                    Element element) throws Exception;

    @Override
    protected void print(Diagnostic.Kind kind,
                         Element element, CharSequence message) {
        super.print(kind, element, format(message));
    }

    private CharSequence format(CharSequence message) {
        CharSequence sequence = message;

        if (annotation != null) {
            sequence =
                new StringBuilder()
                .append(AT).append(annotation.getQualifiedName())
                .append(COLON).append(SPACE).append(message);
        }

        return sequence;
    }
}
