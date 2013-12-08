/*
 * $Id$
 *
 * Copyright 2010 - 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import java.math.BigDecimal;

/**
 * {@link BigDecimal} {@link Factory} implementation.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class BigDecimalFactory extends Factory<BigDecimal> {
    private static final long serialVersionUID = 3471903407759681575L;

    private static final BigDecimalFactory DEFAULT = new BigDecimalFactory();

    /**
     * {@link BigDecimalFactory} factory method.
     */
    public static BigDecimalFactory getDefault() { return DEFAULT; }

    /**
     * Sole constructor.
     *
     * @see #getDefault()
     */
    protected BigDecimalFactory() { super(BigDecimal.class); }
}
