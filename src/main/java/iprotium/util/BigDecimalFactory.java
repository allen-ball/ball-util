/*
 * $Id: BigDecimalFactory.java,v 1.1 2010-11-04 02:40:56 ball Exp $
 *
 * Copyright 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import java.math.BigDecimal;

/**
 * {@link BigDecimal} {@link Factory} implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
public class BigDecimalFactory extends Factory<BigDecimal> {
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
/*
 * $Log: not supported by cvs2svn $
 */
