/*
 * $Id: IntegerFactory.java,v 1.1 2010-11-04 02:40:56 ball Exp $
 *
 * Copyright 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

/**
 * {@link Integer} {@link Factory} implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
public class IntegerFactory extends Factory<Integer> {
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
}
/*
 * $Log: not supported by cvs2svn $
 */
