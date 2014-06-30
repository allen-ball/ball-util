/*
 * $Id$
 *
 * Copyright 2010 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.beans;

import java.lang.reflect.InvocationTargetException;

/**
 * Type {@link Converter} interface.
 *
 * @param       <T>             The type of {@link Object} this
 *                              {@link Converter} will target.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public interface Converter<T> {

    /**
     * Method to convert the argument {@link Object} to {@code this}
     * {@link Converter}'s type.
     *
     * @param   in              The {@link Object} to convert.
     *
     * @return  The converted {@link Object}.
     *
     * @throws  IllegalAccessException
     *                          If an underlying
     *                          {@link java.lang.reflect.Constructor} or
     *                          {@link java.lang.reflect.Method} enforces
     *                          Java language access control and the
     *                          underlying
     *                          {@link java.lang.reflect.Constructor} or
     *                          {@link java.lang.reflect.Method} is
     *                          inaccessible.
     * @throws  InstantiationException
     *                          If an underlying
     *                          {@link java.lang.reflect.Constructor} or
     *                          {@link java.lang.reflect.Method} represents
     *                          an abstract {@link Class}.
     * @throws  InvocationTargetException
     *                          If an underlying
     *                          {@link java.lang.reflect.Constructor} or
     *                          {@link java.lang.reflect.Method} fails for
     *                          some reason.
     * @throws  NoSuchMethodException
     *                          If there is no
     *                          {@link java.lang.reflect.Constructor} or
     *                          {@link java.lang.reflect.Method} to
     *                          facilitate the conversion.
     */
    public T convert(Object in) throws IllegalAccessException,
                                       InstantiationException,
                                       InvocationTargetException,
                                       NoSuchMethodException;
}
