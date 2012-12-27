/*
 * $Id$
 *
 * Copyright 2012 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.criteria;

/**
 * {@link Not} {@link IsNull} implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class IsNotNull extends Not {
    private static final IsNull DEFAULT = new IsNull();

    /**
     * {@link IsNull} factory method.
     */
    public static IsNull getDefault() { return DEFAULT; }

    /**
     * Sole constructor.
     */
    public IsNotNull() { super(new IsNull()); }
}
