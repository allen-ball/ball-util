/*
 * $Id$
 *
 * Copyright 2012, 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.predicate;

/**
 * {@link Not} {@link IsNull} implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class IsNotNull extends Not {
    private static final IsNotNull DEFAULT = new IsNotNull();

    /**
     * {@link IsNotNull} factory method.
     */
    public static IsNotNull getDefault() { return DEFAULT; }

    /**
     * Sole constructor.
     */
    public IsNotNull() { super(IsNull.getDefault()); }
}
