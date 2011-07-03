/*
 * $Id$
 *
 * Copyright 2010, 2011 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

/**
 * {@link String} {@link Factory} implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class StringFactory extends Factory<String> {
    private static final StringFactory DEFAULT = new StringFactory();

    /**
     * {@link StringFactory} factory method.
     */
    public static StringFactory getDefault() { return DEFAULT; }

    /**
     * Sole constructor.
     *
     * @see #getDefault()
     */
    protected StringFactory() { super(String.class); }
}
