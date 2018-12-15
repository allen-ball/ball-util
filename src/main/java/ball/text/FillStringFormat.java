/*
 * $Id$
 *
 * Copyright 2009 - 2018 Allen D. Ball.  All rights reserved.
 */
package ball.text;

import ball.util.StringUtil;
import java.text.FieldPosition;

/**
 * "Fill" {@link StringFormat} implementation.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class FillStringFormat extends StringFormat {
    private static final long serialVersionUID = 5799447729229814790L;

    /** @serial */ private final int width;
    /** @serial */ private final char filler;

    /**
     * @param   width           The fill-to width.
     * @param   filler          The filler {@code char}.
     */
    public FillStringFormat(int width, char filler) {
        super();

        this.width = width;
        this.filler = filler;
    }

    /**
     * @param   width           The fill-to width.
     */
    public FillStringFormat(int width) { this(width, StringUtil.SPACE); }

    @Override
    public StringBuffer format(String string,
                               StringBuffer out, FieldPosition pos) {
        int begin = out.length();

        out.append(fill(string, width, filler));

        pos.setBeginIndex(begin);
        pos.setEndIndex(out.length());

        return out;
    }

    protected String fill(CharSequence sequence, int length, char character) {
        return StringUtil.fill(sequence, length, character);
    }
}
