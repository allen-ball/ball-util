/*
 * $Id$
 *
 * Copyright 2011 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.tools;

import ball.annotation.ServiceProviderFor;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Locale;
import java.util.SortedSet;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static ball.lang.Punctuation.EQUALS;
import static ball.lang.Punctuation.SEMICOLON;
import static ball.lang.Punctuation.SPACE;
import static java.lang.reflect.Modifier.FINAL;
import static java.lang.reflect.Modifier.PRIVATE;
import static java.lang.reflect.Modifier.STATIC;

/**
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Remedy.class })
@Codes({ "compiler.warn.missing.SVUID" })
@NoArgsConstructor @ToString
public class MissingSVUIDRemedy extends Remedy implements Serializable {
    private static final long serialVersionUID = -6900234880629237267L;

    private static final int MODIFIERS = PRIVATE | STATIC | FINAL;
    private static final Class<?> TYPE = Long.TYPE;
    private static final String SVUID = "serialVersionUID";
    private static final String L = "L";

    @Override
    public String getRx(Diagnostic<? extends JavaFileObject> diagnostic,
                        StandardJavaFileManager fm,
                        SortedSet<Class<?>> classes) {
        JavaFileObject source = diagnostic.getSource();
        String message = diagnostic.getMessage(Locale.ROOT);
        String name = fm.inferBinaryName(StandardLocation.SOURCE_PATH, source);
        int start = message.lastIndexOf(name);
        int end =
            (start >= 0)
                ? message.indexOf(SPACE.lexeme(), start)
                : message.length();

        if (0 <= start && start < end) {
            name = message.substring(start, end);
        }

        Class<?> type = null;

        for (Class<?> member : classes) {
            if (name.equals(member.getCanonicalName())) {
                type = member;
                break;
            }
        }

        return getRX(type);
    }

    protected String getRX(Class<?> type) {
        String remedy = null;

        if (type != null) {
            long serialVersionUID =
                ObjectStreamClass.lookup(type).getSerialVersionUID();

            remedy = getDeclaration(serialVersionUID);
        }

        return remedy;
    }

    private String getDeclaration(long serialVersionUID) {
        return (Modifier.toString(MODIFIERS) + SPACE.lexeme() + TYPE.getName()
                + SPACE.lexeme() + SVUID
                + SPACE.lexeme() + EQUALS.lexeme()
                + SPACE.lexeme() + String.valueOf(serialVersionUID) + L
                + SEMICOLON.lexeme());
    }
}
