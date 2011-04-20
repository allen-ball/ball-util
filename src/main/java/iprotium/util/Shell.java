/*
 * $Id: Shell.java,v 1.1 2011-04-20 01:40:41 ball Exp $
 *
 * Copyright 2011 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import iprotium.util.jni.EditLine;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.TreeMap;
import java.util.regex.Pattern;

/**
 * Interactive command shell implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
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
                TreeMap<Integer,Parser.Token> map =
                    new TreeMap<Integer,Parser.Token>();

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
/*
 * $Log: not supported by cvs2svn $
 */
