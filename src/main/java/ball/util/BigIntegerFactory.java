/*
 * $Id$
 *
 * Copyright 2010 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import java.math.BigInteger;

/**
 * {@link BigInteger} {@link Factory} implementation.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class BigIntegerFactory extends Factory<BigInteger> {
    private static final long serialVersionUID = 5368486438498618349L;

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
