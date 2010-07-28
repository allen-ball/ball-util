/*
 * $Id: BeanMap.java,v 1.9 2010-07-28 04:52:03 ball Exp $
 *
 * Copyright 2008 - 2010 Allen D. Ball.  All rights reserved.
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
 * {@link Map} implementation that wraps a Java bean and provides entries
 * for the bean properties.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.9 $
 */
public class BeanMap extends AbstractMap<String,Object>
                             implements Serializable {
    private static final long serialVersionUID = -7056120117701081116L;

    private final Object bean;
    private final Set<EntryImpl> entrySet0;

    /**
     * Construct a BeanMap from a bean.
     *
     * @param   bean            The Java bean to wrap.
     *
     * @throws  NullPointerException
     *                          If bean argument is null.
     * @throws  IntrospectionException
     *                          If bean introspection fails.
     * @throws  NullPointerException
     *                          If bean argument is null.
     */
    public BeanMap(Object bean) throws IntrospectionException {
        this(bean, Introspector.getBeanInfo(bean.getClass()));
    }

    /**
     * Construct a BeanMap from a bean.
     *
     * @param   bean            The Java bean to wrap (if <code>null</code>
     *                          then <code>this</code> will be used).
     * @param   info            The {@link BeanInfo} that describes the bean.
     *
     * @throws  NullPointerException
     *                          If info argument is null.
     */
    public BeanMap(Object bean, BeanInfo info) {
        this.bean = (bean != null) ? bean : this;

        if (info != null) {
            Set<EntryImpl> entrySet = new LinkedHashSet<EntryImpl>();

            for (PropertyDescriptor property : info.getPropertyDescriptors()) {
                if (property.getReadMethod() != null) {
                    entrySet.add(new EntryImpl(property));
                }
            }

            this.entrySet0 = Collections.unmodifiableSet(entrySet);
        } else {
            throw new NullPointerException("info");
        }
    }

    /**
     * Method to get the wrapped bean.
     *
     * @return  The bean {@link Object}.
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

    /**
     * @throws  UnsupportedOperationException
     *                          Always.
     */
    @Override
    public Object remove(Object key) {
        throw new UnsupportedOperationException("remove");
    }

    /**
     * @throws  UnsupportedOperationException
     *                          Always.
     */
    @Override
    public void clear() { throw new UnsupportedOperationException("clear"); }

    @Override
    public Set<Map.Entry<String,Object>> entrySet() {
        Set<Map.Entry<String,Object>> entrySet =
            new LinkedHashSet<Map.Entry<String,Object>>(entrySet0);

        return Collections.unmodifiableSet(entrySet);
    }

    /**
     * Static method to wrap a Java bean in a {@link BeanMap}.
     *
     * @param   bean            The Java bean to wrap.
     *
     * @return  The argument bean if it is an instance of {@link BeanMap}; a
     *          new {@link BeanMap} wrapping the argument bean otherwise.
     *
     * @throws  NullPointerException
     *                          If bean argument is null.
     * @throws  IntrospectionException
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
        public String toString() { return getKey() + "=" + getValue(); }
    }
}
/*
 * $Log: not supported by cvs2svn $
 * Revision 1.8  2009/11/26 00:09:06  ball
 * Only include bean properties with read methods in the keySet().
 *
 */
