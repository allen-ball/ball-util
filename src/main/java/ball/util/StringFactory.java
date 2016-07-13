/*
 * $Id$
 *
 * Copyright 2010 - 2016 Allen D. Ball.  All rights reserved.
 */
package ball.util;

/**
 * {@link String} {@link Factory} implementation.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class StringFactory extends Factory<String> {
    private static final long serialVersionUID = -5476225150819606592L;

    private static final StringFactory DEFAULT = new StringFactory();

    /**
     * {@link StringFactory} factory method.
     *
     * @return  The default {@link StringFactory}.
     */
    public static StringFactory getDefault() { return DEFAULT; }

    /**
     * Sole constructor.
     *
     * @see #getDefault()
     */
    protected StringFactory() { super(String.class); }
}
