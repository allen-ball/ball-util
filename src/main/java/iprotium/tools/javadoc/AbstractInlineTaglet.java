/*
 * $Id$
 *
 * Copyright 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.tools.javadoc;

/**
 * Abstract base class for inline
 * {@link com.sun.tools.doclets.internal.toolkit.taglets.Taglet}
 * implementations.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class AbstractInlineTaglet extends AbstractTaglet {

    /**
     * Sole constructor.
     */
    protected AbstractInlineTaglet() {
        super(true, true, true, true, true, true, true);
    }
}
