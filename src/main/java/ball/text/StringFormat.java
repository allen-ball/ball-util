/*
 * $Id$
 *
 * Copyright 2009 - 2016 Allen D. Ball.  All rights reserved.
 */
package ball.text;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;

/**
 * {@link String} {@link Format} base class.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class StringFormat extends Format {
    private static final long serialVersionUID = 488288557671578461L;

    /**
     * Sole constructor.
     */
    protected StringFormat() { super(); }

    public abstract StringBuffer format(String string,
                                        StringBuffer out, FieldPosition pos);

    @Override
    public StringBuffer format(Object object,
                               StringBuffer out, FieldPosition pos) {
        return format(object.toString(), out, pos);
    }

    /**
     * Parses text from the beginning of the given {@link String} to produce
     * a {@link String}.  The method may not use the entire text of the
     * given string.
     *
     * @see #parse(String,ParsePosition)
     *
     * @param   source          A {@link String} whose beginning should be
     *                          parsed.
     *
     * @return  A {@link String} parsed from the input {@link String}.
     *
     * @throws  ParseException  If the beginning of the {@link String}
     *                          cannot be parsed.
     */
    public String parse(String source) throws ParseException {
        ParsePosition pos = new ParsePosition(0);
        String result = parse(source, pos);

        if (source.length() > 0 && pos.getIndex() == 0) {
            throw new ParseException("Unparseable string: \"" + source + "\"" ,
                                     pos.getErrorIndex());
        }

        return result;
    }

    /**
     * Parses a {@link String} starting at the argument {@link ParsePosition}
     * to produce a {@link String}.
     *
     * @param   source          A {@link String} to be parsed.
     * @param   pos             The starting {@link ParsePosition}.
     *
     * @return  A {@link String} parsed from the input {@link String} or
     *          {@code null} if input could not be parsed.
     */
    public String parse(String source, ParsePosition pos) {
        String string = source.substring(pos.getIndex());

        pos.setIndex(pos.getIndex() + string.length());

        return string;
    }

    @Override
    public Object parseObject(String source, ParsePosition pos) {
        return parse(source, pos);
    }

    @Override
    public String toString() { return super.toString(); }
}
