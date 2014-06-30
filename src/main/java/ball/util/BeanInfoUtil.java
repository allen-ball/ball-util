/*
 * $Id$
 *
 * Copyright 2013, 2014 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import java.beans.BeanInfo;
import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import static ball.util.StringUtil.NIL;

/**
 * {@code BeanInfo} utility methods.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class BeanInfoUtil {
    private BeanInfoUtil() { }

    private static final String R = "R";
    private static final String W = "W";

    /**
     * Method to return a {@link String} indicating read and write access
     * for the property described by the argument
     * {@link PropertyDescriptor}.
     *
     * @param   descriptor              The {@link PropertyDescriptor}.
     *
     * @return  The {@link String} representing the access mode.
     */
    public static String getMode(PropertyDescriptor descriptor) {
        String mode =
            getMode(descriptor.getReadMethod(), descriptor.getWriteMethod());

        if (descriptor instanceof IndexedPropertyDescriptor) {
            mode += "[";
            mode += getMode((IndexedPropertyDescriptor) descriptor);
            mode += "]";
        }

        return mode;
    }

    private static String getMode(IndexedPropertyDescriptor descriptor) {
        return getMode(descriptor.getIndexedReadMethod(),
                       descriptor.getIndexedWriteMethod());
    }

    private static String getMode(Method read, Method write) {
        return ((read != null) ? R : NIL) + ((write != null) ? W : NIL);
    }
}
