/*
 * $Id$
 *
 * Copyright 2010 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.beans;

import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * {@link PropertyDescriptor} {@link java.util.Map} implementation.
 *
 * See {@link Introspector#getBeanInfo(Class)} and
 * {@link java.beans.BeanInfo#getPropertyDescriptors()}.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class PropertyDescriptorMap
             extends LinkedHashMap<String,PropertyDescriptor> {
    private static final long serialVersionUID = -6738630562558450010L;

    private static final Map MAP = new Map();

    /**
     * Static factory method to get the {@link PropertyDescriptorMap} for a
     * specific {@link Class}.
     *
     * @param   type            The {@link Class}.
     *
     * @return  The {@link PropertyDescriptorMap}.
     */
    public static PropertyDescriptorMap forClass(Class<?> type) {
        return MAP.get(type).clone();
    }

    private final Class<?> type;

    private PropertyDescriptorMap(Class<?> type) {
        super();

        this.type = type;

        try {
            PropertyDescriptor[] descriptors =
                Introspector.getBeanInfo(type).getPropertyDescriptors();

            if (descriptors != null) {
                for (PropertyDescriptor descriptor : descriptors) {
                    super.put(descriptor.getName(), descriptor);
                }
            }
        } catch (IntrospectionException exception) {
        }
    }

    /**
     * See {@link PropertyDescriptor#getReadMethod()}.
     *
     * @param   key             The property name.
     *
     * @return  The read {@link Method} or {@code null} if there is none.
     */
    public Method getReadMethod(String key) {
        PropertyDescriptor value = get(key);

        return (value != null) ? value.getReadMethod() : null;
    }

    /**
     * See {@link PropertyDescriptor#getWriteMethod()}.
     *
     * @param   key             The property name.
     *
     * @return  The write {@link Method} or {@code null} if there is none.
     */
    public Method getWriteMethod(String key) {
        PropertyDescriptor value = get(key);

        return (value != null) ? value.getWriteMethod() : null;
    }

    /**
     * See {@link IndexedPropertyDescriptor#getIndexedReadMethod()}.
     *
     * @param   key             The property name.
     *
     * @return  The indexed read {@link Method} or {@code null} if there is
     *          none.
     */
    public Method getIndexedReadMethod(String key) {
        Method method = null;
        PropertyDescriptor value = get(key);

        if (value instanceof IndexedPropertyDescriptor) {
            method =
                ((IndexedPropertyDescriptor) value).getIndexedReadMethod();
        }

        return method;
    }

    /**
     * See {@link IndexedPropertyDescriptor#getIndexedWriteMethod()}.
     *
     * @param   key             The property name.
     *
     * @return  The indexed write {@link Method} or {@code null} if there is
     *          none.
     */
    public Method getIndexedWriteMethod(String key) {
        Method method = null;
        PropertyDescriptor value = get(key);

        if (value instanceof IndexedPropertyDescriptor) {
            method =
                ((IndexedPropertyDescriptor) value).getIndexedWriteMethod();
        }

        return method;
    }

    @Override
    public PropertyDescriptorMap clone() {
        return (PropertyDescriptorMap) super.clone();
    }

    private static class Map extends HashMap<Class<?>,PropertyDescriptorMap> {
        private static final long serialVersionUID = 5504623084348854045L;

        public Map() { super(); }

        @Override
        public PropertyDescriptorMap get(Object key) {
            PropertyDescriptorMap value = null;

            if (key instanceof Class<?>) {
                Class<?> type = (Class<?>) key;

                synchronized (this) {
                    if (! this.containsKey(type)) {
                        this.put(type, new PropertyDescriptorMap(type));
                    }

                    value = super.get(type);
                }
            }

            return value;
        }
    }
}
