/*
 * $Id: FillStringFormat.java,v 1.4 2010-08-23 03:43:54 ball Exp $
 *
 * Copyright 2009, 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.text;

import java.text.FieldPosition;

/**
 * "Fill" {@link StringFormat} implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.4 $
 */
public class FillStringFormat extends StringFormat {
    private static final long serialVersionUID = 2590787583727880296L;

    /**
     * {@link #SPACE} = {@value #SPACE}
     */
    protected static final char SPACE = ' ';

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
    public FillStringFormat(int width) { this(width, SPACE); }

    @Override
    public StringBuffer format(String string,
                               StringBuffer out, FieldPosition pos) {
        int begin = out.length();

        out.append(fill(width, filler, string));

        pos.setBeginIndex(begin);
        pos.setEndIndex(out.length());

        return out;
    }

    protected String fill(int width, char filler, String string) {
        StringBuilder buffer = new StringBuilder(string);

        for (;;) {
            if (buffer.length() < width) {
                buffer.append(filler);
            } else {
                break;
            }

            if (buffer.length() < width) {
                buffer.insert(0, filler);
            } else {
                break;
            }
        }

        return buffer.toString();
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
