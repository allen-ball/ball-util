/*
 * $Id$
 *
 * Copyright 2012 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.criteria;

import iprotium.util.AbstractCriteria;

/**
 * {@link iprotium.util.Criteria} implementation to test for not {@code null}..
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class IsNull extends AbstractCriteria {
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
    public boolean match(Object object) { return (object == null); }
}
