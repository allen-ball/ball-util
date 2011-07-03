/*
 * $Id$
 *
 * Copyright 2010, 2011 Allen D. Ball.  All rights reserved.
 */
package iprotium.lang.java;

import iprotium.util.Parser;
import java.util.regex.Pattern;

/**
 * Provides Java punctuation as an {@link Enum} type.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public enum Punctuation implements Parser.Lexeme {
    EXCLAMATION("!"), DOUBLE_QUOTE("\""), POUND("#"), DOLLAR("$"),
        PERCENT("%"), AMPERSAND("&"), RIGHT_QUOTE("'"), LP("("), RP(")"),
        ASTERISK("*"), PLUS("+"), COMMA(","), HYPHEN("-"), PERIOD("."),
        SLASH("/"), COLON(":"), SEMICOLON(";"), LT("<"), EQUALS("="), GT(">"),
        QUESTION("?"), AT("@"), LBK("["), BACKSLASH("\\"), RBK("]"),
        CARET("^"), LEFT_QUOTE("`"), LBC("{"), PIPE("|"), RBC("}"), TILDE("~");

    private final String lexeme;
    private final Pattern pattern;

    private Punctuation(String lexeme) {
        this.lexeme = lexeme;
        this.pattern = Pattern.compile(Pattern.quote(lexeme));
    }

    /**
     * Method to get the lexeme associated with this {@link Punctuation}.
     *
     * @return  The lexeme.
     */
    public String lexeme() { return lexeme; }

    @Override
    public Pattern pattern() { return pattern; }
}
