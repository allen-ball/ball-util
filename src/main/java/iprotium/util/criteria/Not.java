/*
 * $Id$
 *
 * Copyright 2012 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.criteria;

import iprotium.util.AbstractCriteria;
import iprotium.util.Criteria;

/**
 * Not {@link Criteria} implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class Not extends AbstractCriteria {
    private final Criteria criteria;

    /**
     * Sole constructor.
     *
     * @param   criteria        The {@link Criteria} to {@code not}.
     */
    public Not(Criteria criteria) {
        super();

        if (criteria != null) {
            this.criteria = criteria;
        } else {
            throw new NullPointerException("criteria");
        }
    }

    @Override
    public boolean match(Object object) { return (! criteria.match(object)); }
}
