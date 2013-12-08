/*
 * $Id$
 *
 * Copyright 2010 - 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import java.lang.reflect.InvocationTargetException;

/**
 * {@link Long} {@link Factory} implementation.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class LongFactory extends Factory<Long> {
    private static final long serialVersionUID = 6355068951410168110L;

    private static final LongFactory DEFAULT = new LongFactory();

    /**
     * {@link LongFactory} factory method.
     */
    public static LongFactory getDefault() { return DEFAULT; }

    /**
     * Sole constructor.
     *
     * @see #getDefault()
     */
    protected LongFactory() { super(Long.class); }

    /**
     * {@inheritDoc}
     * @see Long#decode(String)
     */
    @Override
    public Long convert(Object in) throws IllegalAccessException,
                                          InstantiationException,
                                          InvocationTargetException,
                                          NoSuchMethodException {
        return ((in instanceof CharSequence)
                    ? Long.decode(in.toString())
                    : super.convert(in));
    }
}
