/*
 * $Id$
 *
 * Copyright 2010 - 2012 Allen D. Ball.  All rights reserved.
 */
package iprotium.beans;

import iprotium.util.ClassOrder;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.TreeMap;

/**
 * {@link Converter} utility methods.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public abstract class ConverterUtil {
    private static final ConverterMap MAP = new ConverterMap();

    private ConverterUtil() { }

    /**
     * If the specified {@link Method} parameter is not assignable from the
     * argument {@link Object} and a {@link ConvertWith} annotation is
     * specified on the {@link Method} parameter, apply the specified
     * {@link Converter} to the {@link Object}.
     *
     * @param   method          The {@link Method}.
     * @param   parameter       The parameter index.
     * @param   object          The {@link Object}.
     *
     * @return  The converted {@link Object} if conversion is required and
     *          possible; the unconverted {@link Object} otherwise.
     */
    public static Object convert(Method method, int parameter, Object object) {
        Class<?> type = method.getParameterTypes()[parameter];

        if (object != null) {
            if (! type.isAssignableFrom(object.getClass())) {
                ConvertWith annotation = getConvertWith(method, parameter);

                if (annotation != null) {
                    Converter<?> converter = MAP.get(annotation.value());

                    if (converter != null) {
                        try {
                            object = converter.convert(object);
                        } catch (Exception exception) {
                        }
                    }
                }
            }
        }

        return object;
    }

    private static ConvertWith getConvertWith(Method method, int parameter) {
        ConvertWith with = null;

        if (with == null) {
            for(Annotation annotation :
                    method.getParameterAnnotations()[parameter]) {
                if (annotation instanceof ConvertWith) {
                    with = (ConvertWith) annotation;
                    break;
                }
            }
        }

        if (with == null) {
            with = method.getAnnotation(ConvertWith.class);
        }

        return with;
    }

    private static abstract class InstanceMap<T>
                                  extends TreeMap<Class<? extends T>,T> {
        private final Class<T> supertype;

        protected InstanceMap(Class<T> supertype) {
            super(ClassOrder.NAME);

            this.supertype = supertype;
        }

        @Override
        public T get(Object key) {
            T value = null;

            synchronized (this) {
                if (! containsKey(key)) {
                    if (key instanceof Class<?>) {
                        Class<? extends T> subtype = null;

                        try {
                            subtype = ((Class<?>) key).asSubclass(supertype);
                            value = new BeanFactory<T>(subtype).getInstance();
                        } catch (Exception exception) {
                        } finally {
                            if (subtype != null) {
                                put(subtype, value);
                            }
                        }
                    }
                } else {
                    value = super.get(key);
                }
            }

            return value;
        }
    }

    private static class ConverterMap extends InstanceMap<Converter> {
        private static final long serialVersionUID = -776744283893159398L;

        public ConverterMap() { super(Converter.class); }
    }
}
