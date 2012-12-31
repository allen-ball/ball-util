/*
 * $Id$
 *
 * Copyright 2008 - 2012 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import iprotium.beans.PropertyDescriptorMap;
import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * {@link Map} implementation that wraps a Java {@code bean} and provides
 * entries for the {@code bean} properties.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class BeanMap extends AbstractMap<String,Object>
                             implements Serializable {
    private static final long serialVersionUID = -7861142510160605496L;

    private final Object bean;
    private final Set<Map.Entry<String,Object>> entrySet0;

    /**
     * Construct a {@link BeanMap} from a {@code bean}.
     *
     * @param   bean            The {@code bean} {@link Object} to wrap.
     *
     * @throws  NullPointerException
     *                          If the argument is null.
     */
    public BeanMap(Object bean) { this(bean, bean.getClass()); }

    /**
     * Construct a {@link BeanMap} from a {@code bean}.
     *
     * @param   bean            The Java {@code bean} to wrap (if {@code
     *                          null} then {@code this} will be used).
     * @param   type            The {@link Class} describing the
     *                          {@code bean}.
     *
     * @throws  NullPointerException
     *                          If the type argument is null.
     */
    public BeanMap(Object bean, Class<?> type) {
        super();

        this.bean = (bean != null) ? bean : this;

        LinkedHashSet<Map.Entry<String,Object>> entrySet =
            new LinkedHashSet<Map.Entry<String,Object>>();

        for (PropertyDescriptor descriptor :
                 PropertyDescriptorMap.forClass(type).values()) {
            if (descriptor.getReadMethod() != null) {
                entrySet.add(new EntryImpl(descriptor));
            }
        }

        this.entrySet0 = Collections.unmodifiableSet(entrySet);
    }

    /**
     * Method to get the wrapped {@code bean}.
     *
     * @return  The {@code bean} {@link Object}.
     */
    protected Object getBean() { return bean; }

    @Override
    public Object put(String key, Object value) {
        for (Map.Entry<String,Object> entry : entrySet0) {
            if (((EntryImpl) entry).isMatch(key)) {
                return entry.setValue(value);
            }
        }

        throw new IllegalArgumentException("key=" + String.valueOf(key));
    }

    @Override
    public Set<Map.Entry<String,Object>> entrySet() { return entrySet0; }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();

        for (Map.Entry<?,?> entry : entrySet()) {
            if (buffer.length() > 0) {
                buffer.append(",");
            }

            buffer.append(entry);
        }

        return buffer.insert(0, "{").append("}").toString();
    }

    /**
     * Static method to wrap a Java {@code bean} in a {@link BeanMap}.
     *
     * @param   bean            The Java {@code bean} to wrap.
     *
     * @return  The argument {@code bean} if it is an instance of
     *          {@link BeanMap}; a new {@link BeanMap} wrapping the argument
     *          {@code bean} otherwise.
     *
     * @throws  NullPointerException
     *                          If {@code bean} argument is null.
     */
    public static BeanMap asBeanMap(Object bean) {
        BeanMap map = null;

        if (bean instanceof BeanMap) {
            map = (BeanMap) bean;
        } else {
            map = new BeanMap(bean);
        }

        return map;
    }

    private class EntryImpl implements Entry<String,Object>, Serializable {
        private static final long serialVersionUID = -3429201342528841043L;

        private final String key;
        private final Method read;
        private final Method write;

        public EntryImpl(PropertyDescriptor descriptor) {
            key = descriptor.getName();
            read = descriptor.getReadMethod();
            write = descriptor.getWriteMethod();
        }

        public boolean isMatch(String key) {
            return (key != null) ? getKey().equals(key) : (getKey() == key);
        }

        @Override
        public String getKey() { return key; }

        @Override
        public Object getValue() {
            Object value = null;

            try {
                value = read.invoke(getBean());
            } catch (Exception exception) {
                throw new RuntimeException("key=" + String.valueOf(getKey()),
                                           exception);
            }

            return value;
        }

        @Override
        public Object setValue(Object value) {
            Object object = getValue();

            if (write != null) {
                try {
                    write.invoke(getBean(), value);
                } catch (Exception exception) {
                    throw new RuntimeException("key="
                                               + String.valueOf(getKey())
                                               + ",value="
                                               + String.valueOf(value),
                                               exception);
                }
            } else {
                throw new RuntimeException("key=" + String.valueOf(getKey()));
            }

            return object;
        }

        @Override
        public String toString() {
            String value = null;

            try {
                value = String.valueOf(getValue());
            } catch (Throwable throwable) {
                value = "?";
            }

            return getKey() + "=" + value;
        }
    }
}
