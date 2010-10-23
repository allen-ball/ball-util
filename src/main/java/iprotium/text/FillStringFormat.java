/*
 * $Id: FillStringFormat.java,v 1.5 2010-10-23 22:10:52 ball Exp $
 *
 * Copyright 2009, 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.text;

import iprotium.util.StringUtil;
import java.text.FieldPosition;

/**
 * "Fill" {@link StringFormat} implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.5 $
 */
public class FillStringFormat extends StringFormat {
    private static final long serialVersionUID = -6988667371376403207L;

    private final int width;
    private final char filler;

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
/*
 * $Log: not supported by cvs2svn $
 */
