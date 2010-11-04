/*
 * $Id: LongFactory.java,v 1.1 2010-11-04 02:40:56 ball Exp $
 *
 * Copyright 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

/**
 * {@link Long} {@link Factory} implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
public class LongFactory extends Factory<Long> {
    private static final LongFactory DEFAULT = new LongFactory();

    /**
     * {@link LongFactory} factory method.
     */
    public static LongFactory getDefault() { return DEFAULT; }

    /**
     * Sole constructor.
     *
     * @see #getDefault()
     */
    protected LongFactory() { super(Long.class); }
}
/*
 * $Log: not supported by cvs2svn $
 */
