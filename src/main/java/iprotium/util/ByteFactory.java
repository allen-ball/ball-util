/*
 * $Id$
 *
 * Copyright 2010, 2011 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import java.lang.reflect.InvocationTargetException;

/**
 * {@link Byte} {@link Factory} implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class ByteFactory extends Factory<Byte> {
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
