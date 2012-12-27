/*
 * $Id$
 *
 * Copyright 2012 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.criteria;

import iprotium.util.AbstractCriteria;
import iprotium.util.Criteria;

/**
 * Or {@link Criteria} implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class Or extends AbstractCriteria {
    private final Criteria[] criteria;

    /**
     * Sole constructor.
     *
     * @param   criteria        The {@link Criteria} to {@code or}.
     */
    public Or(Criteria... criteria) {
        super();

        if (criteria != null) {
            this.criteria = criteria;
        } else {
            throw new NullPointerException("criteria");
        }
    }

    @Override
    public boolean match(Object object) {
        boolean match = (criteria.length == 0);

        for (Criteria criteron : criteria) {
            match |= criteron.match(object);

            if (match) {
                break;
            }
        }

        return match;
    }
}
