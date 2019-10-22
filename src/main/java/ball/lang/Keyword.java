/*
 * $Id$
 *
 * Copyright 2010 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.lang;

import ball.util.Parser;
import java.util.regex.Pattern;

/**
 * Provides Java keywords as an {@link Enum} type.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public enum Keyword implements Parser.Lexeme {
    ABSTRACT, ASSERT, BOOLEAN, BREAK, BYTE, CASE, CATCH, CHAR, CLASS, CONST,
        CONTINUE, DEFAULT, DO, DOUBLE, ELSE, ENUM, EXTENDS, FINAL, FINALLY,
        FLOAT, FOR, GOTO, IF, IMPLEMENTS, IMPORT, INSTANCEOF, INT, INTERFACE,
        LONG, NATIVE, NEW, PACKAGE, PRIVATE, PROTECTED, PUBLIC, RETURN, SHORT,
        STATIC, STRICTFP, SUPER, SWITCH, SYNCHRONIZED, THIS, THROW, THROWS,
        TRANSIENT, TRY, VOID, VOLATILE, WHILE, YIELD;

    private final Pattern pattern;

    private Keyword() {
        pattern = Pattern.compile("\\b" + Pattern.quote(lexeme()) + "\\B");
    }

    /**
     * Method to get the lexeme associated with this {@link.this}
     * {@link Keyword}.
     *
     * @return  The lexeme (literal keyword).
     */
    public String lexeme() { return name().toLowerCase(); }

    @Override
    public Pattern pattern() { return pattern; }
}
