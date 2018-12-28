/*
 * $Id$
 *
 * Copyright 2010 - 2018 Allen D. Ball.  All rights reserved.
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
    private static final long serialVersionUID = -6875650947612227173L;

    private static final IntegerFactory DEFAULT = new IntegerFactory();

    /**
     * {@link IntegerFactory} factory method.
     *
     * @return  The default {@link IntegerFactory}.
     */
    public static IntegerFactory getDefault() { return DEFAULT; }

    /**
     * Sole constructor.
     *
     * @see #getDefault()
     */
    protected IntegerFactory() { super(Integer.class); }
}
