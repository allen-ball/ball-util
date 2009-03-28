/*
 * $Id: FillEnum.java,v 1.1 2009-03-28 17:50:50 ball Exp $
 *
 * Copyright 2008, 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.text;

import static iprotium.text.FillStringFormat.SPACE;

/**
 * Text (column) fill Enum type.
 *
 * @see FillStringFormat#fill(String,int,char)
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
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
     * @param   string          The String to fill.
     * @param   width           The fill-to width.
     * @param   filler          The filler character.
     *
     * @return  The filled String.
     */
    public String fill(String string, int width, char filler) {
        return format.fill(string, width, filler);
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
