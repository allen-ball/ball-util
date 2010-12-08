/*
 * $Id: Punctuation.java,v 1.1 2010-12-08 04:17:49 ball Exp $
 *
 * Copyright 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.lang.java;

/**
 * Provides Java punctuation as an {@link Enum} type.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
public enum Punctuation {
    EXCLAMATION("!"), DOUBLE_QUOTE("\""), POUND("#"), DOLLAR("$"),
        PERCENT("%"), AMPERSAND("&"), RIGHT_QUOTE("'"), LP("("), RP(")"),
        ASTERISK("*"), PLUS("+"), COMMA(","), HYPHEN("-"), PERIOD("."),
        SLASH("/"), COLON(":"), SEMICOLON(";"), LT("<"), EQUALS("="), GT(">"),
        QUESTION("?"), AT("@"), LBK("["), BACKSLASH("\\"), RBK("]"),
        CARET("^"), LEFT_QUOTE("`"), LBC("{"), PIPE("|"), RBC("}"), TILDE("~");

    private final String lexeme;

    private Punctuation(String lexeme) { this.lexeme = lexeme; }

    /**
     * Method to get the lexeme associated with this {@link Punctuation}.
     *
     * @return  The lexeme.
     */
    public String lexeme() { return lexeme; }
}
/*
 * $Log: not supported by cvs2svn $
 */
