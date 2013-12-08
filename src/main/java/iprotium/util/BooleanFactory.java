/*
 * $Id$
 *
 * Copyright 2010 - 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

/**
 * {@link Boolean} {@link Factory} implementation.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class BooleanFactory extends Factory<Boolean> {
    private static final long serialVersionUID = 4480305068240979716L;

    private static final BooleanFactory DEFAULT = new BooleanFactory();

    /**
     * {@link BooleanFactory} factory method.
     */
    public static BooleanFactory getDefault() { return DEFAULT; }

    /**
     * Sole constructor.
     *
     * @see #getDefault()
     */
    protected BooleanFactory() { super(Boolean.class); }
}
