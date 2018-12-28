/*
 * $Id$
 *
 * Copyright 2010 - 2018 Allen D. Ball.  All rights reserved.
 */
package ball.util;

/**
 * {@link Double} {@link Factory} implementation.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class DoubleFactory extends Factory<Double> {
    private static final long serialVersionUID = -4268502113063243775L;

    private static final DoubleFactory DEFAULT = new DoubleFactory();

    /**
     * {@link DoubleFactory} factory method.
     *
     * @return  The default {@link DoubleFactory}.
     */
    public static DoubleFactory getDefault() { return DEFAULT; }

    /**
     * Sole constructor.
     *
     * @see #getDefault()
     */
    protected DoubleFactory() { super(Double.class); }
}
