/*
 * $Id: BigIntegerFactory.java,v 1.1 2010-11-04 02:40:56 ball Exp $
 *
 * Copyright 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import java.math.BigInteger;

/**
 * {@link BigInteger} {@link Factory} implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
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
/*
 * $Log: not supported by cvs2svn $
 */
