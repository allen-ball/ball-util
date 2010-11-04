/*
 * $Id: ByteFactory.java,v 1.1 2010-11-04 02:40:56 ball Exp $
 *
 * Copyright 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

/**
 * {@link Byte} {@link Factory} implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
public class ByteFactory extends Factory<Byte> {
    private static final ByteFactory DEFAULT = new ByteFactory();

    /**
     * {@link ByteFactory} factory method.
     */
    public static ByteFactory getDefault() { return DEFAULT; }

    /**
     * Sole constructor.
     *
     * @see #getDefault()
     */
    protected ByteFactory() { super(Byte.class); }
}
/*
 * $Log: not supported by cvs2svn $
 */
