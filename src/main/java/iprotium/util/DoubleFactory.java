/*
 * $Id$
 *
 * Copyright 2010 - 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

/**
 * {@link Double} {@link Factory} implementation.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class DoubleFactory extends Factory<Double> {
    private static final long serialVersionUID = 8756382581077256526L;

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
