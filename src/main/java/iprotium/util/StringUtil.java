/*
 * $Id: StringUtil.java,v 1.1 2010-09-24 05:28:02 ball Exp $
 *
 * Copyright 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import java.util.regex.Pattern;

/**
 * {@link String} utility methods.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
public abstract class StringUtil {
    private StringUtil() { }

    private static final Pattern SPACE = Pattern.compile("[\\p{Space}]*");

    /**
     * {@value #NIL}
     */
    public static final String NIL = "";

    /**
     * Method to determine if the argument {@link CharSequence}
     * {@link #isNil(CharSequence)} or blank.
     *
     * @param   sequence        The  {@link CharSequence} to test.
     *
     * @return  {@code true} if the {@link CharSequence}
     *           {@link #isNil(CharSequence)} or blank; {@code false}
     *           otherwise.
     */
    public static boolean isBlank(CharSequence sequence) {
        return (isNil(sequence) || SPACE.matcher(sequence).matches());
    }

    /**
     * Method to determine if the argument {@link CharSequence} is
     * {@code null} or empty.
     *
     * @param   sequence        The  {@link CharSequence} to test.
     *
     * @return  {@code true} if the {@link CharSequence} is {@code null} or
     *          equal to {@value NIL}; {@code false} otherwise.
     */
    public static boolean isNil(CharSequence sequence) {
        return (sequence == null || sequence.length() == 0);
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
