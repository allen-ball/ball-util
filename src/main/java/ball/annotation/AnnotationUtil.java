/*
 * $Id$
 *
 * Copyright 2016 - 2018 Allen D. Ball.  All rights reserved.
 */
package ball.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * {@link Annotation} utility methods.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class AnnotationUtil {
    private AnnotationUtil() { }

    /**
     * Method to get a {@link Class}'s inner {@link Class}es annotated with
     * an {@link Annotation}.
     *
     * @param   type            The {@link Class} to inspect.
     * @param   annotation      The required {@link Annotation}.
     *
     * @return  An {@link Iterable} of annotated {@link Class}es.
     *
     * @see Class#getClasses()
     */
    public static Iterable<Class<?>> getClassesAnnotatedWith(Class<?> type,
                                                             Class<? extends Annotation> annotation) {
        return annotatedWith(Arrays.asList(type.getClasses()), annotation);
    }

    /**
     * Method to get a {@link Class}'s {@link Constructor}s annotated with
     * an {@link Annotation}.
     *
     * @param   type            The {@link Class} to inspect.
     * @param   annotation      The required {@link Annotation}.
     *
     * @return  An {@link Iterable} of annotated {@link Constructor}s.
     *
     * @see Class#getConstructors()
     */
    public static Iterable<Constructor<?>> getConstructorsAnnotatedWith(Class<?> type,
                                                                        Class<? extends Annotation> annotation) {
        return annotatedWith(Arrays.asList(type.getConstructors()),
                             annotation);
    }

    /**
     * Method to get a {@link Class}'s {@link Field}s annotated with an
     * {@link Annotation}.
     *
     * @param   type            The {@link Class} to inspect.
     * @param   annotation      The required {@link Annotation}.
     *
     * @return  An {@link Iterable} of annotated {@link Field}s.
     *
     * @see Class#getFields()
     */
    public static Iterable<Field> getFieldsAnnotatedWith(Class<?> type,
                                                         Class<? extends Annotation> annotation) {
        return annotatedWith(Arrays.asList(type.getFields()), annotation);
    }

    /**
     * Method to get a {@link Class}'s {@link Method}s annotated with an
     * {@link Annotation}.
     *
     * @param   type            The {@link Class} to inspect.
     * @param   annotation      The required {@link Annotation}.
     *
     * @return  An {@link Iterable} of annotated {@link Method}s.
     *
     * @see Class#getMethods()
     */
    public static Iterable<Method> getMethodsAnnotatedWith(Class<?> type,
                                                           Class<? extends Annotation> annotation) {
        return annotatedWith(Arrays.asList(type.getMethods()), annotation);
    }

    private static <T extends AnnotatedElement> List<T> annotatedWith(Iterable<T> elements,
                                                                      Class<? extends Annotation> annotation) {
        ArrayList<T> list = new ArrayList<>();

        for (T element : elements) {
            if (element.getAnnotation(annotation) != null) {
                list.add(element);
            }
        }

        return list;
    }
}
