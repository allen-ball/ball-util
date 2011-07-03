/*
 * $Id$
 *
 * Copyright 2011 Allen D. Ball.  All rights reserved.
 */
package iprotium.tools;

import java.io.ObjectStreamClass;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Locale;
import java.util.SortedSet;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;

import static iprotium.lang.java.Punctuation.EQUALS;
import static iprotium.lang.java.Punctuation.SEMICOLON;
import static iprotium.util.ClassUtil.isAbstract;
import static iprotium.util.ClassUtil.isStatic;
import static java.lang.reflect.Modifier.FINAL;
import static java.lang.reflect.Modifier.PRIVATE;
import static java.lang.reflect.Modifier.STATIC;

/**
 * {@value CODE} {@link Remedy}.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class MissingSVUIDRemedy extends Remedy {
    protected static final String CODE = "compiler.warn.missing.SVUID";

    private static final String SERIALVERSIONUID = "serialVersionUID";

    private static final int MODIFIERS = PRIVATE | STATIC | FINAL;
    private static final Class<?> TYPE = Long.TYPE;
    private static final String L = "L";

    private static final String SPACE = " ";

    /**
     * Sole constructor.
     */
    public MissingSVUIDRemedy() { super(); }

    @Override
    public String getCode() { return CODE; }

    @Override
    public String getRx(Diagnostic<? extends JavaFileObject> diagnostic,
                        StandardJavaFileManager fm,
                        SortedSet<Class<?>> classes) {
        JavaFileObject source = diagnostic.getSource();
        String message = diagnostic.getMessage(Locale.ROOT);
        String name = fm.inferBinaryName(StandardLocation.SOURCE_PATH, source);
        int start = message.lastIndexOf(name);
        int end =
            (start >= 0) ? message.indexOf(SPACE, start) : message.length();

        if (start >= 0 && end > start) {
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

    protected String getDeclaration(long serialVersionUID) {
        return (Modifier.toString(MODIFIERS) + SPACE + TYPE.getName()
                + SPACE + SERIALVERSIONUID + SPACE + EQUALS.lexeme()
                + SPACE + String.valueOf(serialVersionUID) + L
                + SEMICOLON.lexeme());
    }
}
