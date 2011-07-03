/*
 * $Id$
 *
 * Copyright 2008 - 2011 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import java.util.MissingResourceException;

/**
 * Exception thrown to indicate a required {@link Property} is not set.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class MissingPropertyException extends MissingResourceException {
    private static final long serialVersionUID = 2070619364376943510L;

    /**
     * Sole constructor.
     *
     * @param   property        The {@link Property} whose value is not set.
     */
    public MissingPropertyException(Property property) {
        super("Undefined property `" + property.getName() + "'",
              property.getClass().getName(), property.getName());
    }
}
