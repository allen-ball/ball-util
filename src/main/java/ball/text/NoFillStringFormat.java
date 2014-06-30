/*
 * $Id$
 *
 * Copyright 2009 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.text;

import ball.util.StringUtil;

/**
 * "No fill" {@link StringFormat} implementation.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class NoFillStringFormat extends FillStringFormat {
    private static final long serialVersionUID = -154497174967716089L;

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
