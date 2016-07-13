/*
 * $Id$
 *
 * Copyright 2009 - 2016 Allen D. Ball.  All rights reserved.
 */
package ball.text;

import ball.util.StringUtil;

/**
 * "Left fill" {@link StringFormat} implementation.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class LeftFillStringFormat extends FillStringFormat {
    private static final long serialVersionUID = 5459922417228548968L;

    /**
     * See {@link FillStringFormat#FillStringFormat(int,char)}.
     *
     * @param   width           The fill-to width.
     * @param   filler          The filler {@code char}.
     */
    public LeftFillStringFormat(int width, char filler) {
        super(width, filler);
    }

    /**
     * See {@link FillStringFormat#FillStringFormat(int)}.
     *
     * @param   width           The fill-to width.
     */
    public LeftFillStringFormat(int width) { this(width, StringUtil.SPACE); }

    @Override
    protected String fill(CharSequence sequence, int length, char character) {
        return StringUtil.lfill(sequence, length, character);
    }
}
