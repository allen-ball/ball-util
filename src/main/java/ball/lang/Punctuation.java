/*
 * $Id$
 *
 * Copyright 2010 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.lang;

import ball.util.Parser;
import java.util.regex.Pattern;

/**
 * Provides Java punctuation as an {@link Enum} type.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public enum Punctuation implements Parser.Lexeme {
    EXCLAMATION('!'), DOUBLE_QUOTE('"'), POUND('#'), DOLLAR('$'),
        PERCENT('%'), AMPERSAND('&'), RIGHT_QUOTE('\''), LP('('), RP(')'),
        ASTERISK('*'), PLUS('+'), COMMA(','), HYPHEN('-'), PERIOD('.'),
        SLASH('/'), COLON(':'), SEMICOLON(';'), LT('<'), EQUALS('='), GT('>'),
        QUESTION('?'), AT('@'), LBK('['), BACKSLASH('\\'), RBK(']'),
        CARET('^'), LEFT_QUOTE('`'), LBC('{'), PIPE('|'), RBC('}'), TILDE('~'),
        SPACE(' ', "[\\p{Space}]+");

    private final String lexeme;
    private final Pattern pattern;

    private Punctuation(char character) {
        this(character, Pattern.quote(String.valueOf(character)));
    }

    private Punctuation(char character, String regex) {
        this(String.valueOf(character), regex);
    }

    private Punctuation(String lexeme, String regex) {
        this.lexeme = lexeme;
        this.pattern = Pattern.compile(regex);
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
