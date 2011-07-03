/*
 * $Id$
 *
 * Copyright 2010, 2011 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import java.lang.reflect.InvocationTargetException;

/**
 * {@link Short} {@link Factory} implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class ShortFactory extends Factory<Short> {
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
