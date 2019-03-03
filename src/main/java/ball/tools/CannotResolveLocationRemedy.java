/*
 * $Id$
 *
 * Copyright 2011 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.tools;

import ball.annotation.Regex;
import ball.annotation.ServiceProviderFor;
import java.util.Locale;
import java.util.SortedSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static ball.lang.Keyword.IMPORT;
import static ball.lang.Punctuation.SEMICOLON;
import static ball.lang.Punctuation.SPACE;

/**
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Remedy.class })
@Codes({ "compiler.err.cant.resolve.location" })
@NoArgsConstructor @ToString
public class CannotResolveLocationRemedy extends Remedy {
    @Regex
    private static final String REGEX =
        "(?m)^symbol[\\p{Space}]*:[\\p{Space}]*class[\\p{Space}]+([\\p{Graph}]+)$";
    private static final Pattern PATTERN = Pattern.compile(REGEX);
    private static final int CLASS = 1;

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
