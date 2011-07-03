/*
 * $Id$
 *
 * Copyright 2010, 2011 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import java.math.BigInteger;

/**
 * {@link BigInteger} {@link Factory} implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class BigIntegerFactory extends Factory<BigInteger> {
    private static final BigIntegerFactory DEFAULT = new BigIntegerFactory();

    /**
     * {@link BigIntegerFactory} factory method.
     */
    public static BigIntegerFactory getDefault() { return DEFAULT; }

    /**
     * Sole constructor.
     *
     * @see #getDefault()
     */
    protected BigIntegerFactory() { super(BigInteger.class); }
}
