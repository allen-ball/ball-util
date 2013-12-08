/*
 * $Id$
 *
 * Copyright 2010 - 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import iprotium.beans.ConverterUtil;
import iprotium.beans.PropertyDescriptorMap;
import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.beans.PropertyVetoException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * {@code Bean} utility methods.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class BeanUtil {
    private BeanUtil() { }

    /**
     * Method to get a {@code bean} property value.
     *
     * @param   type            The {@link Class} of the bean.
     * @param   bean            The {@code bean} to interrogate (may be
     *                          {@code null}).
     * @param   name            The name of the property to get.
     *
     * @throws  IllegalAccessException
     *                          If this method does not have access to the
     *                          property getter method.
     * @throws  InvocationTargetException
     *                          If there is a problem invoking the getter
     *                          method for the specified property.
     * @throws  NoSuchMethodException
     *                          If there is no getter method for the
     *                          specified property.
     */
    public static Object get(Class<?> type,
                             Object bean,
                             String name) throws IllegalAccessException,
                                                 InvocationTargetException,
                                                 NoSuchMethodException {
        Method get =
            PropertyDescriptorMap.forClass(type).getReadMethod(name);

        if (get == null) {
            throw new NoSuchMethodException();
        }

        return get.invoke(bean);
    }

    /**
     * Method to get a {@code bean} property value.
     *
     * @param   bean            The {@code bean} to interrogate.
     * @param   name            The name of the property to get.
     *
     * @throws  NullPointerException
     *                          If {@code bean} is {@code null}.
     * @throws  IllegalAccessException
     *                          If this method does not have access to the
     *                          property getter method.
     * @throws  InvocationTargetException
     *                          If there is a problem invoking the getter
     *                          method for the specified property.
     * @throws  NoSuchMethodException
     *                          If there is no getter method for the
     *                          specified property.
     * @throws  NullPointerException
     *                          If {@code bean} is {@code null}.
     */
    public static Object get(Object bean,
                             String name) throws IllegalAccessException,
                                                 InvocationTargetException,
                                                 NoSuchMethodException {
        return get(bean.getClass(), bean, name);
    }

    /**
     * Method to set a {@code bean} property value.
     *
     * @param   type            The {@link Class} of the bean.
     * @param   bean            The {@code bean} to modify (may be
     *                          {@code null}).
     * @param   name            The name of the property to set.
     * @param   value           The value to assign to the property.
     *
     * @throws  IllegalAccessException
     *                          If this method does not have access to the
     *                          property setter method.
     * @throws  InvocationTargetException
     *                          If there is a problem invoking the setter
     *                          method for the specified property.
     * @throws  NoSuchMethodException
     *                          If there is no setter method for the
     *                          specified property.
     * @throws  PropertyVetoException
     *                          If the target setter method throws
     *                          {@link PropertyVetoException}.
     */
    public static void set(Class<?> type,
                           Object bean,
                           String name,
                           Object value) throws IllegalAccessException,
                                                InvocationTargetException,
                                                NoSuchMethodException,
                                                PropertyVetoException {
        Method set =
            PropertyDescriptorMap.forClass(type).getWriteMethod(name);

        if (set == null) {
            throw new NoSuchMethodException();
        }

        try {
            set.invoke(bean, ConverterUtil.convert(set, 0, value));
        } catch (InvocationTargetException exception) {
            Throwable cause = exception.getCause();

            if (cause instanceof PropertyVetoException) {
                throw (PropertyVetoException) cause;
            } else {
                throw exception;
            }
        }
    }

    /**
     * Method to set a {@code bean} property value.
     *
     * @param   bean            The {@code bean} to interrogate.
     * @param   name            The name of the property to set.
     * @param   value           The value to assign to the property.
     *
     * @throws  IllegalAccessException
     *                          If this method does not have access to the
     *                          property setter method.
     * @throws  InvocationTargetException
     *                          If there is a problem invoking the setter
     *                          method for the specified property.
     * @throws  NoSuchMethodException
     *                          If there is no setter method for the
     *                          specified property.
     * @throws  NullPointerException
     *                          If {@code bean} is {@code null}.
     * @throws  PropertyVetoException
     *                          If the target setter method throws
     *                          {@link PropertyVetoException}.
     */
    public static void set(Object bean,
                           String name,
                           Object value) throws IllegalAccessException,
                                                InvocationTargetException,
                                                NoSuchMethodException,
                                                PropertyVetoException {
        set(bean.getClass(), bean, name, value);
    }

    /**
     * Method to get a {@code bean} indexed property value.
     *
     * @param   type            The {@link Class} of the bean.
     * @param   bean            The {@code bean} to interrogate (may be
     *                          {@code null}).
     * @param   name            The name of the property to get.
     * @param   index           The property index.
     *
     * @throws  IllegalAccessException
     *                          If this method does not have access to the
     *                          property getter method.
     * @throws  InvocationTargetException
     *                          If there is a problem invoking the getter
     *                          method for the specified property.
     * @throws  NoSuchMethodException
     *                          If there is no getter method for the
     *                          specified property.
     */
    public static Object get(Class<?> type,
                             Object bean,
                             String name,
                             int index) throws IllegalAccessException,
                                               InvocationTargetException,
                                               NoSuchMethodException {
        Method get =
            PropertyDescriptorMap.forClass(type).getIndexedReadMethod(name);

        if (get == null) {
            throw new NoSuchMethodException();
        }

        return get.invoke(bean, index);
    }

    /**
     * Method to get a {@code bean} indexed property value.
     *
     * @param   bean            The {@code bean} to interrogate.
     * @param   name            The name of the property to get.
     * @param   index           The property index.
     *
     * @throws  NullPointerException
     *                          If {@code bean} is {@code null}.
     * @throws  IllegalAccessException
     *                          If this method does not have access to the
     *                          property getter method.
     * @throws  InvocationTargetException
     *                          If there is a problem invoking the getter
     *                          method for the specified property.
     * @throws  NoSuchMethodException
     *                          If there is no getter method for the
     *                          specified property.
     * @throws  NullPointerException
     *                          If {@code bean} is {@code null}.
     */
    public static Object get(Object bean,
                             String name,
                             int index) throws IllegalAccessException,
                                               InvocationTargetException,
                                               NoSuchMethodException {
        return get(bean.getClass(), bean, name, index);
    }

    /**
     * Method to set a {@code bean} indexed property value.
     *
     * @param   type            The {@link Class} of the bean.
     * @param   bean            The {@code bean} to modify (may be
     *                          {@code null}).
     * @param   name            The name of the property to set.
     * @param   index           The property index.
     * @param   value           The value to assign to the property.
     *
     * @throws  IllegalAccessException
     *                          If this method does not have access to the
     *                          property setter method.
     * @throws  InvocationTargetException
     *                          If there is a problem invoking the setter
     *                          method for the specified property.
     * @throws  NoSuchMethodException
     *                          If there is no setter method for the
     *                          specified property.
     * @throws  PropertyVetoException
     *                          If the target setter method throws
     *                          {@link PropertyVetoException}.
     */
    public static void set(Class<?> type,
                           Object bean,
                           String name,
                           int index,
                           Object value) throws IllegalAccessException,
                                                InvocationTargetException,
                                                NoSuchMethodException,
                                                PropertyVetoException {
        Method set =
            PropertyDescriptorMap.forClass(type).getIndexedWriteMethod(name);

        if (set == null) {
            throw new NoSuchMethodException();
        }

        try {
            set.invoke(bean, index, ConverterUtil.convert(set, 1, value));
        } catch (InvocationTargetException exception) {
            Throwable cause = exception.getCause();

            if (cause instanceof PropertyVetoException) {
                throw (PropertyVetoException) cause;
            } else {
                throw exception;
            }
        }
    }

    /**
     * Method to set a {@code bean} indexed property value.
     *
     * @param   bean            The {@code bean} to interrogate.
     * @param   name            The name of the property to set.
     * @param   index           The property index.
     * @param   value           The value to assign to the property.
     *
     * @throws  IllegalAccessException
     *                          If this method does not have access to the
     *                          property setter method.
     * @throws  InvocationTargetException
     *                          If there is a problem invoking the setter
     *                          method for the specified property.
     * @throws  NoSuchMethodException
     *                          If there is no setter method for the
     *                          specified property.
     * @throws  NullPointerException
     *                          If {@code bean} is {@code null}.
     * @throws  PropertyVetoException
     *                          If the target setter method throws
     *                          {@link PropertyVetoException}.
     */
    public static void set(Object bean,
                           String name,
                           int index,
                           Object value) throws IllegalAccessException,
                                                InvocationTargetException,
                                                NoSuchMethodException,
                                                PropertyVetoException {
        set(bean.getClass(), bean, name, index, value);
    }

    /**
     * Method to copy one {@code bean}'s property values to another.
     *
     * @param   in              The source {@code bean} {@link Object}.
     * @param   out             The target {@code bean} {@link Object}.
     *
     * @throws  IllegalAccessException
     *                          If this method does not have access to a
     *                          property getter or setter method.
     * @throws  InvocationTargetException
     *                          If there is a problem invoking a getter or
     *                          setter method for any property.
     * @throws  PropertyVetoException
     *                          If the target setter method throws
     *                          {@link PropertyVetoException}.
     */
    public static void copy(Object in, Object out)
                                        throws IllegalAccessException,
                                               InvocationTargetException,
                                               PropertyVetoException {
        for (PropertyDescriptor descriptor :
                 PropertyDescriptorMap.forClass(out.getClass()).values()) {
            if (descriptor.getWriteMethod() != null) {
                Method get =
                    PropertyDescriptorMap.forClass(in.getClass())
                    .getReadMethod(descriptor.getName());

                if (get != null) {
                    try {
                        set(out, descriptor.getName(), get.invoke(in));
                    } catch (NoSuchMethodException exception) {
                        throw new IllegalStateException(exception);
                    }
                }
            }
        }
    }

    /**
     * Method to populate a {@code bean}'s property values from a
     * {@link Map}.
     *
     * @param   in              The source {@link Map}.
     * @param   out             The target {@code bean} {@link Object}.
     *
     * @throws  IllegalAccessException
     *                          If this method does not have access to a
     *                          property setter method.
     * @throws  InvocationTargetException
     *                          If there is a problem invoking a setter
     *                          method for any property.
     * @throws  PropertyVetoException
     *                          If the target setter method throws
     *                          {@link PropertyVetoException}.
     */
    public static void copy(Map<?,?> in, Object out)
                                        throws IllegalAccessException,
                                               InvocationTargetException,
                                               PropertyVetoException {
        for (PropertyDescriptor descriptor :
                 PropertyDescriptorMap.forClass(out.getClass()).values()) {
            String name = descriptor.getName();
            Method set = descriptor.getWriteMethod();

            if (set != null) {
                if (in.containsKey(name)) {
                    try {
                        set(out, name, in.get(name));
                    } catch (NoSuchMethodException exception) {
                        throw new IllegalStateException(exception);
                    }
                }
            }
        }
    }

    /**
     * Method to copy {@code bean}'s property values to a {@link Map}.
     *
     * @param   in              The source {@code bean} {@link Object}.
     * @param   out             The target {@link Map}.
     *
     * @throws  IllegalAccessException
     *                          If this method does not have access to a
     *                          property getter method.
     * @throws  InvocationTargetException
     *                          If there is a problem invoking a getter
     *                          method for any property.
     */
    public static void copy(Object in, Map<? super Object,? super Object> out)
                                        throws IllegalAccessException,
                                               InvocationTargetException {
        for (PropertyDescriptor descriptor :
                 PropertyDescriptorMap.forClass(in.getClass()).values()) {
            Method get = descriptor.getReadMethod();

            if (get != null) {
                out.put(descriptor.getName(), get.invoke(in));
            }
        }
    }

    /**
     * Method to clone a {@code bean} by invoking its no-argument
     * constructor and copying its attributes to the newly cloned
     * {@code bean}.
     *
     * @param   bean            The {@link Object} to clone.
     *
     * @return  The cloned {@code bean}.
     *
     * @throws  IllegalAccessException
     *                          If this method does not have access to an
     *                          source {@link Object} property getter method
     *                          or a target {@link Object} property setter
     *                          method.
     * @throws  InstantiationException
     *                          If the clone {@link Object} cannot be
     *                          instantiated for any reason.
     * @throws  InvocationTargetException
     *                          If there is a problem invoking a getter or
     *                          setter method for any property.
     * @throws  PropertyVetoException
     *                          If the target setter method throws
     *                          {@link PropertyVetoException}.
     *
     * @see #copy(Object,Object)
     */
    public static Object clone(Object bean) throws IllegalAccessException,
                                                   InstantiationException,
                                                   InvocationTargetException,
                                                   PropertyVetoException {
        Object clone = bean.getClass().newInstance();

        copy(bean, clone);

        return clone;
    }

    /**
     * Method to convert an arbitrary {@link String} to a bean property
     * name.
     *
     * @param   string          The input {@link String}.
     *
     * @return  The property name (or {@code null} if the argument is
     *          {@code null}).
     */
    public static String asPropertyName(String string) {
        String name = null;

        if (string != null) {
            String[] tokens = string.split("[\\p{Punct}\\p{Space}]+");

            name = tokens[0];

            if (! name.toUpperCase().equals(name)) {
                name = name.toLowerCase();
            }

            for (int i = 1; i < tokens.length; i += 1) {
                name += capitalize(tokens[i]);
            }
        }

        return name;
    }

    private static String capitalize(CharSequence sequence) {
        StringBuilder buffer =
            new StringBuilder()
            .append(Character.toUpperCase(sequence.charAt(0)))
            .append(sequence.subSequence(1, sequence.length()));

        return buffer.toString();
    }
}
