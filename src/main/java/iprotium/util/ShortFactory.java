/*
 * $Id$
 *
 * Copyright 2010 - 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import java.lang.reflect.InvocationTargetException;

/**
 * {@link Short} {@link Factory} implementation.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class ShortFactory extends Factory<Short> {
    private static final long serialVersionUID = 1108606764641970678L;

    private static final ShortFactory DEFAULT = new ShortFactory();

    /**
     * {@link ShortFactory} factory method.
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
