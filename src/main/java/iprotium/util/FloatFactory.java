/*
 * $Id$
 *
 * Copyright 2010, 2011 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

/**
 * {@link Float} {@link Factory} implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class FloatFactory extends Factory<Float> {
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
