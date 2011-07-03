/*
 * $Id$
 *
 * Copyright 2010, 2011 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import java.lang.reflect.InvocationTargetException;

/**
 * {@link Integer} {@link Factory} implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class IntegerFactory extends Factory<Integer> {
    private static final IntegerFactory DEFAULT = new IntegerFactory();

    /**
     * {@link IntegerFactory} factory method.
     */
    public static IntegerFactory getDefault() { return DEFAULT; }

    /**
     * Sole constructor.
     *
     * @see #getDefault()
     */
    protected IntegerFactory() { super(Integer.class); }

    /**
     * {@inheritDoc}
     * @see Integer#decode(String)
     */
    @Override
    public Integer convert(Object in) throws IllegalAccessException,
                                             InstantiationException,
                                             InvocationTargetException,
                                             NoSuchMethodException {
        return ((in instanceof CharSequence)
                    ? Integer.decode(in.toString())
                    : super.convert(in));
    }
}
