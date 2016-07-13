/*
 * $Id$
 *
 * Copyright 2010 - 2016 Allen D. Ball.  All rights reserved.
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
    private static final long serialVersionUID = 3224792916087784687L;

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

    /**
     * {@inheritDoc}
     * @see Short#decode(String)
     */
    @Override
    public Short convert(Object in) throws IllegalAccessException,
                                           InstantiationException,
                                           InvocationTargetException,
                                           NoSuchMethodException {
        return ((in instanceof CharSequence)
                    ? Short.decode(in.toString())
                    : super.convert(in));
    }
}
