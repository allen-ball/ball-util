/*
 * $Id$
 *
 * Copyright 2010, 2011 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

/**
 * {@link Double} {@link Factory} implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class DoubleFactory extends Factory<Double> {
    private static final DoubleFactory DEFAULT = new DoubleFactory();

    /**
     * {@link DoubleFactory} factory method.
     */
    public static DoubleFactory getDefault() { return DEFAULT; }

    /**
     * Sole constructor.
     *
     * @see #getDefault()
     */
    protected DoubleFactory() { super(Double.class); }
}
