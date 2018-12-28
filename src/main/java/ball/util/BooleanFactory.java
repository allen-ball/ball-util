/*
 * $Id$
 *
 * Copyright 2010 - 2018 Allen D. Ball.  All rights reserved.
 */
package ball.util;

/**
 * {@link Boolean} {@link Factory} implementation.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class BooleanFactory extends Factory<Boolean> {
    private static final long serialVersionUID = 2996965046198529405L;

    private static final BooleanFactory DEFAULT = new BooleanFactory();

    /**
     * {@link BooleanFactory} factory method.
     *
     * @return  The default {@link BooleanFactory}.
     */
    public static BooleanFactory getDefault() { return DEFAULT; }

    /**
     * Sole constructor.
     *
     * @see #getDefault()
     */
    protected BooleanFactory() { super(Boolean.class); }
}
