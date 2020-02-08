package ball.util;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * Parser implementation.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public class Parser extends ArrayList<Parser.Lexeme> {
    private static final long serialVersionUID = -4408046262231182942L;

    private static final Pattern FACTORY = Pattern.compile(EMPTY);

    /**
     * @param   collection      The {@link Collection} of parser
     *                          {@link Lexeme}s.
     */
    public Parser(Collection<? extends Lexeme> collection) {
        super(collection);
    }

    /**
     * @param   lexemes         The parser {@link Lexeme}s.
     */
    public Parser(Lexeme... lexemes) { this(Arrays.asList(lexemes)); }

    /**
     * Method to parse a {@link CharSequence}.
     *
     * @param   input           The input {@link CharSequence}.
     *
     * @return  An {@link Iterator} of {@link Token}s.
     */
    public Iterable<Token> parse(CharSequence input) {
        return new IterableImpl(input);
    }

    @Override
    public Lexeme[] toArray() { return toArray(new Lexeme[] { }); }

    @Override
    public Parser clone() { return (Parser) super.clone(); }

    /**
     * {@link Lexeme} interface definition.
     */
    public interface Lexeme {

        /**
         * Method to get the name of this {@link Lexeme}.
         *
         * @return      The name {@link String}.
         *
         * @see Enum#name()
         */
        public String name();

        /**
         * Method to get the {@link Pattern} for this {@link Lexeme}.
         *
         * @return      The {@link Pattern}.
         */
        public Pattern pattern();
    }

    /**
     * Parsed {@link Token}s.
     *
     * @see Parser#parse(CharSequence)
     */
    public class Token {
        private final Lexeme lexeme;
        private final CharSequence input;
        private final int start;
        private final int end;
        private final String string;

        private Token(Lexeme lexeme, CharSequence input, Matcher matcher) {
            this(lexeme, input, matcher.start(), matcher.end());
        }

        private Token(CharSequence input, Matcher matcher) {
            this(null, input, matcher.regionStart(), matcher.regionEnd());
        }

        private Token(Lexeme lexeme, CharSequence input, int start, int end) {
            this.lexeme = lexeme;
            this.input = input;
            this.start = start;
            this.end = end;
            this.string = input.subSequence(start(), end()).toString();
        }

        /**
         * Method to get the {@link Token}'s {@link Lexeme}.
         *
         * @return      The matching {@link Lexeme}.
         */
        public Lexeme lexeme() { return lexeme; }

        /**
         * Method to get the {@link Token}'s {@link String} value.
         *
         * @return      The matching {@link String}.
         */
        public String string() { return string; }

        public CharSequence subSequence() {
            return input.subSequence(start(), end());
        }

        public int start() { return start; }

        public int end() { return end; }

        @Override
        public String toString() {
            return String.valueOf(lexeme()) + ":" + string;
        }
    }

    private class IterableImpl implements Iterable<Token> {
        private final CharSequence input;

        public IterableImpl(CharSequence input) { this.input = input; }

        @Override
        public Iterator<Token> iterator() { return new IteratorImpl(); }

        private class IteratorImpl implements Iterator<Token> {
            private final Matcher matcher;

            public IteratorImpl() { this.matcher = FACTORY.matcher(input); }

            @Override
            public boolean hasNext() {
                return matcher.regionStart() < matcher.regionEnd();
            }

            @Override
            public Token next() {
                Token token = null;

                if (hasNext()) {
                    for (Lexeme lexeme : Parser.this) {
                        matcher.usePattern(lexeme.pattern());

                        if (matcher.lookingAt()) {
                            token = new Token(lexeme, input, matcher);
                            break;
                        }
                    }

                    if (token == null) {
                        token = new Token(input, matcher);
                    }

                    matcher.region(token.end(), matcher.regionEnd());
                } else {
                    throw new NoSuchElementException();
                }

                return token;
            }
        }
    }
}
