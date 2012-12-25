/*
 * $Id$
 *
 * Copyright 2010 - 2012 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import java.math.BigDecimal;

/**
 * {@link BigDecimal} {@link Factory} implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
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
