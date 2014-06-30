/*
 * $Id$
 *
 * Copyright 2008 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import java.util.MissingResourceException;

/**
 * Exception thrown to indicate a required {@link Property} is not set.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class MissingPropertyException extends MissingResourceException {
    private static final long serialVersionUID = -4489375095788364471L;

    /**
     * Sole constructor.
     *
     * @param   property        The {@link Property} whose value is not set.
     */
    public MissingPropertyException(Property<?> property) {
        super("Undefined property `" + property.getName() + "'",
              property.getClass().getName(), property.getName());
    }
}
