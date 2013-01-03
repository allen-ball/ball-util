/*
 * $Id$
 *
 * Copyright 2012, 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.predicate;

import iprotium.util.AbstractPredicate;

/**
 * {@link iprotium.util.Predicate} implementation to test for not
 * {@code null}.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
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
