/*
 * $Id$
 *
 * Copyright 2016 Allen D. Ball.  All rights reserved.
 */
package ball.annotation;

import ball.util.AbstractPredicate;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

import static ball.util.IterableUtil.filter;

/**
 * {@link Annotataion} utility methods.
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
     * @see Class#getClassses()
     */
    public static Iterable<Class<?>> getClassesAnnotatedWith(Class<?> type,
                                                             Class<? extends Annotation> annotation) {
        return filter(new IsAnnotatedWith<Class<?>>(annotation),
                      Arrays.asList(type.getClasses()));
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
        return filter(new IsAnnotatedWith<Constructor<?>>(annotation),
                      Arrays.asList(type.getConstructors()));
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
        return filter(new IsAnnotatedWith<Field>(annotation),
                      Arrays.asList(type.getFields()));
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
        return filter(new IsAnnotatedWith<Method>(annotation),
                      Arrays.asList(type.getMethods()));
    }

    private static class IsAnnotatedWith<T extends AnnotatedElement>
                         extends AbstractPredicate<T> {
        private final Class<? extends Annotation> annotation;

        public IsAnnotatedWith(Class<? extends Annotation> annotation) {
            super();

            if (annotation != null) {
                this.annotation = annotation;
            } else {
                throw new NullPointerException("annotation");
            }
        }

        @Override
        public boolean apply(T object) {
            return (object.getAnnotation(annotation) != null);
        }
    }
}
