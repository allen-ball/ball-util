/*
 * $Id$
 *
 * Copyright 2008 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.text;

import ball.util.StringUtil;

/**
 * Text (column) fill {@link Enum} type.
 *
 * @see FillStringFormat#fill(CharSequence,int,char)
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public enum FillEnum {
    NONE(new NoFillStringFormat()),
        CENTER(new FillStringFormat(0, StringUtil.SPACE)),
        LEFT(new LeftFillStringFormat(0, StringUtil.SPACE)),
        RIGHT(new RightFillStringFormat(0, StringUtil.SPACE));

    private final FillStringFormat format;

    private FillEnum(FillStringFormat format) {
        if (format != null) {
            this.format = format;
        } else {
            throw new NullPointerException("format");
        }
    }

    /**
     * Convenience method to fill a {@link CharSequence}.
     *
     * @param   sequence        The {@link CharSequence} to fill.
     * @param   length          The minimum length to fill to.
     * @param   character       The fill {@code char}.
     *
     * @return  A {@link String} representing the filled
     *          {@link CharSequence}.
     */
    public String fill(CharSequence sequence, int length, char character) {
        return format.fill((sequence != null) ? sequence : StringUtil.NIL,
                           length, character);
    }

    /**
     * Convenience method to fill a {@link CharSequence} with spaces.
     *
     * @param   sequence        The {@link CharSequence} to fill.
     * @param   length          The minimum length to fill to.
     *
     * @return  A {@link String} representing the filled
     *          {@link CharSequence}.
     */
    public String fill(CharSequence sequence, int length) {
        return fill(sequence, length, StringUtil.SPACE);
    }
}