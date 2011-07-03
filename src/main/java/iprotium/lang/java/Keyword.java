/*
 * $Id$
 *
 * Copyright 2010, 2011 Allen D. Ball.  All rights reserved.
 */
package iprotium.lang.java;

import iprotium.util.Parser;
import java.util.regex.Pattern;

/**
 * Provides Java keywords as an {@link Enum} type.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public enum Keyword implements Parser.Lexeme {
    ABSTRACT, ASSERT, BOOLEAN, BREAK, BYTE, CASE, CATCH, CHAR, CLASS, CONST,
        CONTINUE, DEFAULT, DO, DOUBLE, ELSE, ENUM, EXTENDS, FALSE, FINAL,
        FINALLY, FLOAT, FOR, GOTO, IF, IMPLEMENTS, IMPORT, INSTANCEOF, INT,
        INTERFACE, LONG, NATIVE, NEW, NULL, PACKAGE, PRIVATE, PROTECTED,
        PUBLIC, RETURN, SHORT, STATIC, STRICTFP, SUPER, SWITCH, SYNCHRONIZED,
        THIS, THROW, THROWS, TRANSIENT, TRUE, TRY, VOID, VOLATILE, WHILE;

    private final Pattern pattern;

    private Keyword() {
        this.pattern =
            Pattern.compile("\\b" + Pattern.quote(lexeme()) + "\\B");
    }

    /**
     * Method to get the lexeme associated with this {@link Keyword}.
     *
     * @return  The lexeme (literal keyword).
     */
    public String lexeme() { return name().toLowerCase(); }

    @Override
    public Pattern pattern() { return pattern; }
}
