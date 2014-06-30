/*
 * $Id$
 *
 * Copyright 2012 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.util.predicate;

/**
 * {@link Not} {@link IsNull} implementation.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
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
