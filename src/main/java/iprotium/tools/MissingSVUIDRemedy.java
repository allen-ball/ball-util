/*
 * $Id$
 *
 * Copyright 2011 - 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.tools;

import iprotium.annotation.ServiceProviderFor;
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

import static iprotium.lang.Punctuation.EQUALS;
import static iprotium.lang.Punctuation.SEMICOLON;
import static iprotium.lang.Punctuation.SPACE;
import static iprotium.util.ClassUtil.isAbstract;
import static iprotium.util.ClassUtil.isStatic;
import static java.lang.reflect.Modifier.FINAL;
import static java.lang.reflect.Modifier.PRIVATE;
import static java.lang.reflect.Modifier.STATIC;

/**
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
@ServiceProviderFor({ Remedy.class })
@Codes("compiler.warn.missing.SVUID")
public class MissingSVUIDRemedy extends Remedy implements Serializable {
    private static final long serialVersionUID = 2660379745550185787L;

    private static final int MODIFIERS = PRIVATE | STATIC | FINAL;
    private static final Class<?> TYPE = Long.TYPE;
    private static final String SVUID = "serialVersionUID";
    private static final String L = "L";

    /**
     * Sole constructor.
     */
    public MissingSVUIDRemedy() { super(); }

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
