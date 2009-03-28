/*
 * $Id: LeftFillStringFormat.java,v 1.1 2009-03-28 17:17:36 ball Exp $
 *
 * Copyright 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.text;

/**
 * "Left fill" StringFormat implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
public class LeftFillStringFormat extends FillStringFormat {
    private static final long serialVersionUID = -4099739026210843792L;

    /**
     * @see FillStringFormat#FillStringFormat(int,char)
     */
    public LeftFillStringFormat(int width, char filler) {
        super(width, filler);
    }

    /**
     * @see FillStringFormat#FillStringFormat(int)
     */
    public LeftFillStringFormat(int width) { this(width, SPACE); }

    @Override
    protected String fill(String string, int width, char filler) {
        StringBuilder buffer = new StringBuilder(string);

        for (;;) {
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
