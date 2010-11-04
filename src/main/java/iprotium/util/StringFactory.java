/*
 * $Id: StringFactory.java,v 1.1 2010-11-04 02:40:56 ball Exp $
 *
 * Copyright 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

/**
 * {@link String} {@link Factory} implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
public class StringFactory extends Factory<String> {
    private static final StringFactory DEFAULT = new StringFactory();

    /**
     * {@link StringFactory} factory method.
     */
    public static StringFactory getDefault() { return DEFAULT; }

    /**
     * Sole constructor.
     *
     * @see #getDefault()
     */
    protected StringFactory() { super(String.class); }
}
/*
 * $Log: not supported by cvs2svn $
 */
