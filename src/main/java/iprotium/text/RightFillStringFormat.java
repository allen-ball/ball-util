/*
 * $Id: RightFillStringFormat.java,v 1.1 2009-03-28 17:17:36 ball Exp $
 *
 * Copyright 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.text;

/**
 * "Right fill" StringFormat implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
public class RightFillStringFormat extends FillStringFormat {
    private static final long serialVersionUID = 6275974425054422945L;

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
    protected String fill(String string, int width, char filler) {
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
