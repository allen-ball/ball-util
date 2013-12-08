/*
 * $Id$
 *
 * Copyright 2010 - 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

/**
 * {@link String} {@link Factory} implementation.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class StringFactory extends Factory<String> {
    private static final long serialVersionUID = -9042453166428623758L;

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
