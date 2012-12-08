/*
 * $Id$
 *
 * Copyright 2011, 2012 Allen D. Ball.  All rights reserved.
 */
package iprotium.tools;

import iprotium.util.Regex;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;

import static iprotium.lang.java.Keyword.IMPORT;
import static iprotium.lang.java.Punctuation.SEMICOLON;
import static iprotium.lang.java.Punctuation.SPACE;

/**
 * {@value CODE} {@link Remedy}.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class CannotResolveLocationRemedy extends Remedy {
    protected static final String CODE = "compiler.err.cant.resolve.location";

    @Regex
    private static final String REGEX =
        "(?m)^symbol[\\p{Space}]*:[\\p{Space}]*class[\\p{Space}]+([\\p{Graph}]+)$";
    private final Pattern PATTERN = Pattern.compile(REGEX);

    /**
     * Sole constructor.
     */
    public CannotResolveLocationRemedy() { super(); }

    @Override
    public String getCode() { return CODE; }

    @Override
    public String getRx(Diagnostic<? extends JavaFileObject> diagnostic,
                        StandardJavaFileManager fm,
                        SortedSet<Class<?>> classes) {
        JavaFileObject source = diagnostic.getSource();
        String message = diagnostic.getMessage(Locale.ROOT);
        Matcher matcher = PATTERN.matcher(message);
        Class<?> type = null;

        if (matcher.find()) {
            String symbol = matcher.group(1);

            if (type == null) {
                try {
                    type = Class.forName(symbol);
                } catch (ClassNotFoundException exception) {
                }
            }

            if (type == null) {
                type = new ClassMap(classes).get(symbol);
            }

            if (type == null) {
                for (Package pkg : Package.getPackages()) {
                    try {
                        type = Class.forName(pkg.getName() + "." + symbol);
                    } catch (ClassNotFoundException exception) {
                    }

                    if (type != null) {
                        break;
                    }
                }
            }
        }

        return getRX(type);
    }

    protected String getRX(Class<?> type) {
        String remedy = null;

        if (type != null) {
            remedy =
                IMPORT.lexeme() + SPACE.lexeme()
                + type.getName() + SEMICOLON.lexeme();
        }

        return remedy;
    }

    private class ClassMap extends TreeMap<String,Class<?>> {
        private static final long serialVersionUID = 9110132596690976649L;

        public ClassMap(Iterable<Class<?>> values) {
            super();

            for (Class<?> value : values) {
                String key = value.getSimpleName();

                if (key != null && (! containsKey(key))) {
                    put(key, value);
                }
            }
        }
    }
}
