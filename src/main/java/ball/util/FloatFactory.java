/*
 * $Id$
 *
 * Copyright 2010 - 2018 Allen D. Ball.  All rights reserved.
 */
package ball.util;

/**
 * {@link Float} {@link Factory} implementation.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class FloatFactory extends Factory<Float> {
    private static final long serialVersionUID = 5401678675256713200L;

    private static final FloatFactory DEFAULT = new FloatFactory();

    /**
     * {@link FloatFactory} factory method.
     *
     * @return  The default {@link FloatFactory}.
     */
    public static FloatFactory getDefault() { return DEFAULT; }

    /**
     * Sole constructor.
     *
     * @see #getDefault()
     */
    protected FloatFactory() { super(Float.class); }
}
