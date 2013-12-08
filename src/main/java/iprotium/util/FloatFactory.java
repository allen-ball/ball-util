/*
 * $Id$
 *
 * Copyright 2010 - 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

/**
 * {@link Float} {@link Factory} implementation.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class FloatFactory extends Factory<Float> {
    private static final long serialVersionUID = 7642582830863432020L;

    private static final FloatFactory DEFAULT = new FloatFactory();

    /**
     * {@link FloatFactory} factory method.
     */
    public static FloatFactory getDefault() { return DEFAULT; }

    /**
     * Sole constructor.
     *
     * @see #getDefault()
     */
    protected FloatFactory() { super(Float.class); }
}
