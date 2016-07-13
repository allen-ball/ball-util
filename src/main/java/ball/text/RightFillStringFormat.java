/*
 * $Id$
 *
 * Copyright 2009 - 2016 Allen D. Ball.  All rights reserved.
 */
package ball.text;

import ball.util.StringUtil;

/**
 * "Right fill" {@link StringFormat} implementation.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class RightFillStringFormat extends FillStringFormat {
    private static final long serialVersionUID = -1301548447466665639L;

    /**
     * See {@link FillStringFormat#FillStringFormat(int,char)}.
     *
     * @param   width           The fill-to width.
     * @param   filler          The filler {@code char}.
     */
    public RightFillStringFormat(int width, char filler) {
        super(width, filler);
    }

    /**
     * See {@link FillStringFormat#FillStringFormat(int)}.
     *
     * @param   width           The fill-to width.
     */
    public RightFillStringFormat(int width) { this(width, StringUtil.SPACE); }

    @Override
    protected String fill(CharSequence sequence, int length, char character) {
        return StringUtil.rfill(sequence, length, character);
    }
}
