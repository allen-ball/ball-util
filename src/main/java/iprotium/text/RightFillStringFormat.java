/*
 * $Id: RightFillStringFormat.java,v 1.3 2009-09-04 17:13:43 ball Exp $
 *
 * Copyright 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.text;

/**
 * "Right fill" {@link StringFormat} implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.3 $
 */
public class RightFillStringFormat extends FillStringFormat {
    private static final long serialVersionUID = 761597411095416242L;

    /**
     * @see FillStringFormat#FillStringFormat(int,char)
     */
    public RightFillStringFormat(int width, char filler) {
        super(width, filler);
    }

    /**
     * @see FillStringFormat#FillStringFormat(int)
     */
    public RightFillStringFormat(int width) { this(width, SPACE); }

    @Override
    protected String fill(int width, char filler, String string) {
        StringBuilder buffer = new StringBuilder(string);

        for (;;) {
            if (buffer.length() < width) {
                buffer.append(filler);
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
