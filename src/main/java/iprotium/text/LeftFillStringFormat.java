/*
 * $Id$
 *
 * Copyright 2009 - 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.text;

import iprotium.util.StringUtil;

/**
 * "Left fill" {@link StringFormat} implementation.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class LeftFillStringFormat extends FillStringFormat {
    private static final long serialVersionUID = -2375631972336108130L;

    /**
     * @see FillStringFormat#FillStringFormat(int,char)
     */
    public LeftFillStringFormat(int width, char filler) {
        super(width, filler);
    }

    /**
     * @see FillStringFormat#FillStringFormat(int)
     */
    public LeftFillStringFormat(int width) { this(width, StringUtil.SPACE); }

    @Override
    protected String fill(CharSequence sequence, int length, char character) {
        return StringUtil.lfill(sequence, length, character);
    }
}
