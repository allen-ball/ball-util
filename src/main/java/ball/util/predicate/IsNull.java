/*
 * $Id$
 *
 * Copyright 2012 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.util.predicate;

import ball.util.AbstractPredicate;

/**
 * {@link ball.util.Predicate} implementation to test for not {@code null}.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class IsNull extends AbstractPredicate {
    private static final IsNull DEFAULT = new IsNull();

    /**
     * {@link IsNull} factory method.
     */
    public static IsNull getDefault() { return DEFAULT; }

    /**
     * Sole constructor.
     */
    public IsNull() { super(); }

    @Override
    public boolean apply(Object object) { return (object == null); }
}
