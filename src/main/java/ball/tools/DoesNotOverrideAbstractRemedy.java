/*
 * $Id$
 *
 * Copyright 2013, 2014 Allen D. Ball.  All rights reserved.
 */
package ball.tools;

import ball.annotation.ServiceProviderFor;
import ball.util.Regex;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Locale;
import java.util.SortedSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;

import static ball.lang.Keyword.THROWS;
import static ball.lang.Punctuation.AT;
import static ball.lang.Punctuation.COMMA;
import static ball.lang.Punctuation.GT;
import static ball.lang.Punctuation.LBC;
import static ball.lang.Punctuation.LP;
import static ball.lang.Punctuation.LT;
import static ball.lang.Punctuation.RBC;
import static ball.lang.Punctuation.RP;
import static ball.lang.Punctuation.SEMICOLON;
import static ball.lang.Punctuation.SPACE;
import static java.lang.reflect.Modifier.ABSTRACT;

/**
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Remedy.class })
@Codes("compiler.err.does.not.override.abstract")
public class DoesNotOverrideAbstractRemedy extends Remedy {

    @Regex
    private static final String REGEX =
        "(?m)^.*does not override abstract method ([\\p{Graph}]+[(][\\p{Graph}]*[)])[\\p{Space}]+in[\\p{Space}]+([\\p{Graph}]+)$";
    private static final Pattern PATTERN = Pattern.compile(REGEX);
    private static final int METHOD = 1;
    private static final int CLASS = 2;

    private static final String NL = "\n";

    /**
     * Sole constructor.
     */
    public DoesNotOverrideAbstractRemedy() { super(); }

    @Override
    public String getRx(Diagnostic<? extends JavaFileObject> diagnostic,
                        StandardJavaFileManager fm,
                        SortedSet<Class<?>> classes) {
        JavaFileObject source = diagnostic.getSource();
        String message = diagnostic.getMessage(Locale.ROOT);
        Matcher matcher = PATTERN.matcher(message);
        Method method = null;

        if (matcher.find()) {
            Class<?> type = getClassForNameFrom(matcher.group(CLASS), classes);

            if (type != null) {
                for (Method declared : type.getDeclaredMethods()) {
                    String string = declared.toGenericString();

                    if (string.contains(matcher.group(METHOD))) {
                        method = declared;
                        break;
                    }
                }
            }
        }

        return getRX(method);
    }

    protected String getRX(Method method) {
        StringBuilder buffer = null;

        if (method != null) {
            buffer =
                new StringBuilder()
                .append(AT.lexeme()).append(toString(Override.class))
                .append(NL)
                .append(Modifier.toString(method.getModifiers() ^ ABSTRACT))
                .append(SPACE.lexeme())
                .append(toTypeList(LT.lexeme(), GT.lexeme() + SPACE.lexeme(),
                                   COMMA.lexeme(), method.getTypeParameters()))
                .append(toString(method.getGenericReturnType()))
                .append(SPACE.lexeme()).append(method.getName())
                .append(LP.lexeme())
                .append(toTypeList(null, null,
                                   COMMA.lexeme() + SPACE.lexeme(),
                                   method.getGenericParameterTypes()))
                .append(RP.lexeme())
                .append(SPACE.lexeme())
                .append(toTypeList(THROWS.lexeme() + SPACE.lexeme(),
                                   SPACE.lexeme(),
                                   COMMA.lexeme() + SPACE.lexeme(),
                                   method.getGenericExceptionTypes()))
                .append(LBC.lexeme()).append(NL).append(RBC.lexeme());
        }

        return (buffer != null) ? buffer.toString() : null;
    }

    private String toTypeList(String start, String end, String separator,
                              Type... types) {
        StringBuilder buffer = new StringBuilder();

        if (types != null && types.length > 0) {
            if (start != null) {
                buffer.append(start);
            }

            boolean first = true;

            for (Type type : types) {
                if (first) {
                    first = false;
                } else {
                    if (separator != null) {
                        buffer.append(separator);
                    }
                }

                buffer.append(toString(type));
            }

            if (end != null) {
                buffer.append(end);
            }
        }

        return buffer.toString();
    }

    private String toString(Type type) {
        String string = null;

        if (type != null) {
            if (type instanceof Class) {
                string = ((Class) type).getSimpleName();
            } else {
                string = type.toString();
            }
        }

        return string;
    }
}
