/*
 * $Id: PropertyDescriptorMap.java,v 1.1 2010-10-23 21:09:21 ball Exp $
 *
 * Copyright 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.beans;

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
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
public class PropertyDescriptorMap
             extends LinkedHashMap<String,PropertyDescriptor> {
    private static final long serialVersionUID = -6625757279553634813L;

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

    @Override
    public PropertyDescriptorMap clone() {
        return (PropertyDescriptorMap) super.clone();
    }

    private static class Map extends HashMap<Class<?>,PropertyDescriptorMap> {
        private static final long serialVersionUID = -5876980931451176992L;

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
/*
 * $Log: not supported by cvs2svn $
 */