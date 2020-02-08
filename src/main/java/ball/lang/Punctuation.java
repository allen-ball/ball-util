package ball.lang;
/*-
 * ##########################################################################
 * Utilities
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2008 - 2020 Allen D. Ball
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ##########################################################################
 */
import ball.util.Parser;
import java.util.regex.Pattern;

/**
 * Provides Java punctuation as an {@link Enum} type.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
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
