/*
 * $Id: FillEnum.java,v 1.3 2009-03-30 06:25:12 ball Exp $
 *
 * Copyright 2008, 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.text;

import static iprotium.text.FillStringFormat.SPACE;

/**
 * Text (column) fill Enum type.
 *
 * @see FillStringFormat#fill(int,char,String)
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.3 $
 */
public enum FillEnum {
    NONE(new NoFillStringFormat()),
        CENTER(new FillStringFormat(0, SPACE)),
        LEFT(new LeftFillStringFormat(0, SPACE)),
        RIGHT(new RightFillStringFormat(0, SPACE));

    private final FillStringFormat format;

    private FillEnum(FillStringFormat format) {
        if (format != null) {
            this.format = format;
        } else {
            throw new NullPointerException("format");
        }
    }

    /**
     * Convenience method to fill a String.
     *
     * @param   width           The fill-to width.
     * @param   filler          The filler character.
     * @param   string          The String to fill.
     *
     * @return  The filled String.
     */
    public String fill(int width, char filler, String string) {
        return format.fill(width, filler, (string != null) ? string : "");
    }

    /**
     * Convenience method to fill a String with spaces.
     *
     * @param   width           The fill-to width.
     * @param   string          The String to fill.
     *
     * @return  The filled String.
     */
    public String fill(int width, String string) {
        return fill(width, SPACE, string);
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
