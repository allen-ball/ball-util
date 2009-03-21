/*
 * $Id: StringFormat.java,v 1.1 2009-03-21 21:47:06 ball Exp $
 *
 * Copyright 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.text;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;

/**
 * StringFormat base class.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
public abstract class StringFormat extends Format {

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

    public String parse(String source) throws ParseException {
        ParsePosition pos = new ParsePosition(0);
        String result = parse(source, pos);

        if (source.length() > 0 && pos.getIndex() == 0) {
            throw new ParseException("Unparseable string: \"" + source + "\"" ,
                                     pos.getErrorIndex());
        }

        return result;
    }

    public String parse(String source, ParsePosition pos) {
        String string = source.substring(pos.getIndex());

        pos.setIndex(pos.getIndex() + string.length());

        return string;
    }

    @Override
    public Object parseObject(String source, ParsePosition pos) {
        return parse(source, pos);
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
