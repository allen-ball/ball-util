/*
 * $Id$
 *
 * Copyright 2010 - 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import java.lang.reflect.InvocationTargetException;

/**
 * {@link Byte} {@link Factory} implementation.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class ByteFactory extends Factory<Byte> {
    private static final long serialVersionUID = -3007088485403560348L;

    private static final ByteFactory DEFAULT = new ByteFactory();

    /**
     * {@link ByteFactory} factory method.
     */
    public static ByteFactory getDefault() { return DEFAULT; }

    /**
     * Sole constructor.
     *
     * @see #getDefault()
     */
    protected ByteFactory() { super(Byte.class); }

    /**
     * {@inheritDoc}
     * @see Byte#decode(String)
     */
    @Override
    public Byte convert(Object in) throws IllegalAccessException,
                                          InstantiationException,
                                          InvocationTargetException,
                                          NoSuchMethodException {
        return ((in instanceof CharSequence)
                    ? Byte.decode(in.toString())
                    : super.convert(in));
    }
}
