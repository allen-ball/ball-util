/*
 * $Id$
 *
 * Copyright 2011 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import ball.util.jni.EditLine;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.TreeMap;
import java.util.regex.Pattern;

/**
 * Interactive command shell implementation.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class Shell implements Runnable {
    private static final Parser PARSER =
        new Parser(EnumSet.allOf(Lexeme.class));

    private final EditLine el;
    private final String prompt =
        getClass().getSimpleName().toLowerCase() + "> ";

    /**
     * Sole constructor.
     */
    public Shell() { el = new EditLine(getClass().getName()); }

    /**
     * XXX
     */
    protected void execute(String[] argv) {
        System.out.println(Arrays.toString(argv));
    }

    @Override
    public void run() {
        for (;;) {
            String line = el.readline(prompt);

            if (line != null) {
                TreeMap<Integer,Parser.Token> map = new TreeMap<>();

                for (Parser.Token token : PARSER.parse(line)) {
                    map.put(token.end(), token);
                }

                System.out.println(map);

                el.add_history(line);
            } else {
                break;
            }
        }
    }

    public enum Lexeme implements Parser.Lexeme {
        SPACE("[\\p{Space}]+"),
            QUOTED("\"[^\"]*\""),
            WORD("[\\p{Graph}&&[^\"]]+");

        private final Pattern pattern;

        private Lexeme(String regex) { this(Pattern.compile(regex)); }
        private Lexeme(Pattern pattern) { this.pattern = pattern; }

        @Override
        public Pattern pattern() { return pattern; }
    }
}
