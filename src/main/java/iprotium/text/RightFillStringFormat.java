/*
 * $Id$
 *
 * Copyright 2009 - 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.text;

import iprotium.util.StringUtil;

/**
 * "Right fill" {@link StringFormat} implementation.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class RightFillStringFormat extends FillStringFormat {
    private static final long serialVersionUID = -6899289721657071954L;

    /**
     * @see FillStringFormat#FillStringFormat(int,char)
     */
    public RightFillStringFormat(int width, char filler) {
        super(width, filler);
    }

    /**
     * @see FillStringFormat#FillStringFormat(int)
     */
    public RightFillStringFormat(int width) { this(width, StringUtil.SPACE); }

    @Override
    protected String fill(CharSequence sequence, int length, char character) {
        return StringUtil.rfill(sequence, length, character);
    }
}
