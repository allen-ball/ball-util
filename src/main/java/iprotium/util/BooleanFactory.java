/*
 * $Id: BooleanFactory.java,v 1.1 2010-11-04 02:40:56 ball Exp $
 *
 * Copyright 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

/**
 * {@link Boolean} {@link Factory} implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
public class BooleanFactory extends Factory<Boolean> {
    private static final BooleanFactory DEFAULT = new BooleanFactory();

    /**
     * {@link BooleanFactory} factory method.
     */
    public static BooleanFactory getDefault() { return DEFAULT; }

    /**
     * Sole constructor.
     *
     * @see #getDefault()
     */
    protected BooleanFactory() { super(Boolean.class); }
}
/*
 * $Log: not supported by cvs2svn $
 */
