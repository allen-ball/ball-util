/*
 * $Id: DoubleFactory.java,v 1.1 2010-11-04 02:40:56 ball Exp $
 *
 * Copyright 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

/**
 * {@link Double} {@link Factory} implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
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
/*
 * $Log: not supported by cvs2svn $
 */
