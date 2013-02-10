/*
 * $Id$
 *
 * Copyright 2011 - 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.tools;

import iprotium.util.Regex;
import java.util.Locale;
import java.util.SortedSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;

import static iprotium.lang.Keyword.IMPORT;
import static iprotium.lang.Punctuation.SEMICOLON;
import static iprotium.lang.Punctuation.SPACE;

/**
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
@Codes("compiler.err.cant.resolve.location")
public class CannotResolveLocationRemedy extends Remedy {

    @Regex
    private static final String REGEX =
        "(?m)^symbol[\\p{Space}]*:[\\p{Space}]*class[\\p{Space}]+([\\p{Graph}]+)$";
    private static final Pattern PATTERN = Pattern.compile(REGEX);
    private static final int CLASS = 1;

    /**
     * Sole constructor.
     */
    public CannotResolveLocationRemedy() { super(); }

    @Override
    public String getRx(Diagnostic<? extends JavaFileObject> diagnostic,
                        StandardJavaFileManager fm,
                        SortedSet<Class<?>> classes) {
        JavaFileObject source = diagnostic.getSource();
        String message = diagnostic.getMessage(Locale.ROOT);
        Matcher matcher = PATTERN.matcher(message);
        Class<?> type = null;

        if (matcher.find()) {
            type = getClassForNameFrom(matcher.group(CLASS), classes);
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
}
