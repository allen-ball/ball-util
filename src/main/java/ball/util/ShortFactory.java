/*
 * $Id$
 *
 * Copyright 2010 - 2018 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import java.lang.reflect.InvocationTargetException;

/**
 * {@link Short} {@link Factory} implementation.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class ShortFactory extends Factory<Short> {
    private static final long serialVersionUID = -6846419157647109745L;

    private static final ShortFactory DEFAULT = new ShortFactory();

    /**
     * {@link ShortFactory} factory method.
     *
     * @return  The default {@link ShortFactory}.
     */
    public static ShortFactory getDefault() { return DEFAULT; }

    /**
     * Sole constructor.
     *
     * @see #getDefault()
     */
    protected ShortFactory() { super(Short.class); }
}
