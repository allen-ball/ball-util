/*
 * $Id: FloatFactory.java,v 1.1 2010-11-04 02:40:56 ball Exp $
 *
 * Copyright 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

/**
 * {@link Float} {@link Factory} implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
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
/*
 * $Log: not supported by cvs2svn $
 */
