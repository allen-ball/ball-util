/*
 * $Id$
 *
 * Copyright 2009 - 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.text;

import iprotium.util.StringUtil;

/**
 * "No fill" {@link StringFormat} implementation.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class NoFillStringFormat extends FillStringFormat {
    private static final long serialVersionUID = -7219855768510535981L;

    /**
     * No-argument constructor.
     */
    public NoFillStringFormat() { super(0, StringUtil.SPACE); }

    @Override
    protected String fill(CharSequence sequence, int length, char character) {
        return (new StringBuilder((sequence != null)
                                      ? sequence
                                      : StringUtil.NIL)
                .toString());
    }
}
