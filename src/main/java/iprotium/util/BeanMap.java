/*
 * $Id: BeanMap.java,v 1.3 2008-12-01 01:43:55 ball Exp $
 *
 * Copyright 2008 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Map implementation that wraps a Java bean and provides entries for the
 * bean properties.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.3 $
 */
public class BeanMap extends AbstractMap<String,Object>
                             implements Serializable {
    private static final long serialVersionUID = 7892236967861768264L;

    private final Object bean;
    private final Set<EntryImpl> entrySet0;

    /**
     * Construct a BeanMap from a bean.
     *
     * @param   bean            The Java bean to wrap.
     *
     * @throw   NullPointerException
     *                          If bean argument is null.
     * @throw   IntrospectionException
     *                          If bean introspection fails.
     */
    public BeanMap(Object bean) throws IntrospectionException {
        this(bean, Introspector.getBeanInfo(bean.getClass()));
    }

    /**
     * Construct a BeanMap from a bean.
     *
     * @param   bean            The Java bean to wrap.
     * @param   info            The BeanInfo that describes the bean.
     *
     * @throw   NullPointerException
     *                          If bean argument is null.
     */
    protected BeanMap(Object bean, BeanInfo info) {
        if (bean != null) {
            this.bean = bean;
        } else {
            throw new NullPointerException("bean");
        }

        Set<EntryImpl> entrySet = new LinkedHashSet<EntryImpl>();

        for (PropertyDescriptor property : info.getPropertyDescriptors()) {
            entrySet.add(new EntryImpl(property));
        }

        this.entrySet0 = Collections.unmodifiableSet(entrySet);
    }

    /**
     * Method to get the wrapped bean.
     *
     * @return  The bean Object.
     */
    protected Object getBean() { return bean; }

    @Override
    public Object put(String key, Object value) {
        for (EntryImpl entry : entrySet0) {
            if (entry.isMatch(key)) {
                Object object = entry.getValue();

                entry.setValue(value);

                return object;
            }
        }

        throw new IllegalArgumentException("key=" + String.valueOf(key));
    }

    @Override
    public Object remove(Object key) {
        throw new UnsupportedOperationException("remove");
    }

    @Override
    public void clear() { throw new UnsupportedOperationException("clear"); }

    @Override
    public Set<Map.Entry<String,Object>> entrySet() {
        Set<Map.Entry<String,Object>> entrySet =
            new LinkedHashSet<Map.Entry<String,Object>>(entrySet0);

        return Collections.unmodifiableSet(entrySet);
    }

    /**
     * Static method to wrap a Java bean in a BeanMap.
     *
     * @param   bean            The Java bean to wrap.
     *
     * @return  The argument bean if it is an instance of BeanMap; an new
     *          BeanMap wrapping the argument bean otherwise.
     *
     * @throw   NullPointerException
     *                          If bean argument is null.
     * @throw   IntrospectionException
     *                          If bean introspection fails.
     */
    public static BeanMap asBeanMap(Object bean) throws IntrospectionException {
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

        public String getKey() { return key; }

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

        public Object setValue(Object value) {
            Object object = getValue();

            if (write != null) {
                try {
                    write.invoke(getBean(), value);
                } catch (Exception exception) {
                    throw new RuntimeException("key=" + String.valueOf(getKey())
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
        public String toString() { return getKey() + "=" + getValue(); }
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
