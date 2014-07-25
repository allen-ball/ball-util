/*
 * $Id$
 *
 * Copyright 2010 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import java.lang.reflect.InvocationTargetException;

/**
 * {@link Integer} {@link Factory} implementation.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class IntegerFactory extends Factory<Integer> {
    private static final long serialVersionUID = 7513510276209324479L;

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