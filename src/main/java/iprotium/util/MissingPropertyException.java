/*
 * $Id: MissingPropertyException.java,v 1.2 2009-01-27 22:00:19 ball Exp $
 *
 * Copyright 2008, 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import java.util.MissingResourceException;

/**
 * Exception thrown to indicate a required Property is not set.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.2 $
 */
public class MissingPropertyException extends MissingResourceException {
    private static final long serialVersionUID = 2070619364376943510L;

    /**
     * Sole constructor.
     *
     * @param   property        The Property whose value is not set.
     */
    public MissingPropertyException(Property property) {
        super("Undefined property `" + property.getName() + "'",
              property.getClass().getName(), property.getName());
    }
}
/*
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2008/11/04 04:07:09  ball
 * Initial writing.
 *
 */
