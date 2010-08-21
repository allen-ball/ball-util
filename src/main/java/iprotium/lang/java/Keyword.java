/*
 * $Id: Keyword.java,v 1.2 2010-08-21 07:36:39 ball Exp $
 *
 * Copyright 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.lang.java;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import static iprotium.util.ClassUtil.isFinal;
import static iprotium.util.ClassUtil.isPublic;
import static iprotium.util.ClassUtil.isStatic;

/**
 * Provides Java keywords as named constants.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.2 $
 */
public abstract class Keyword {
    private Keyword() { }

    /**
     * The {@link Set} of Java keywords.
     */
    public static final Set<String> SET;

    static {
        try {
            TreeSet<String> set = new TreeSet<String>();

            for (Field field : Keyword.class.getDeclaredFields()) {
                if (isPublic(field) && isStatic(field) && isFinal(field)
                    && String.class.isAssignableFrom(field.getType())) {
                    set.add((String) field.get(null));
                }
            }

            SET = Collections.unmodifiableSet(set);
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    /**
     * {@value #ABSTRACT}
     */
    public static final String ABSTRACT = "abstract";

    /**
     * {@value #ASSERT}
     */
    public static final String ASSERT = "assert";

    /**
     * {@value #BOOLEAN}
     */
    public static final String BOOLEAN = "boolean";

    /**
     * {@value #BREAK}
     */
    public static final String BREAK = "break";

    /**
     * {@value #BYTE}
     */
    public static final String BYTE = "byte";

    /**
     * {@value #CASE}
     */
    public static final String CASE = "case";

    /**
     * {@value #CATCH}
     */
    public static final String CATCH = "catch";

    /**
     * {@value #CHAR}
     */
    public static final String CHAR = "char";

    /**
     * {@value #CLASS}
     */
    public static final String CLASS = "class";

    /**
     * {@value #CONST}
     */
    public static final String CONST = "const";

    /**
     * {@value #CONTINUE}
     */
    public static final String CONTINUE = "continue";

    /**
     * {@value #DEFAULT}
     */
    public static final String DEFAULT = "default";

    /**
     * {@value #DO}
     */
    public static final String DO = "do";

    /**
     * {@value #DOUBLE}
     */
    public static final String DOUBLE = "double";

    /**
     * {@value #ELSE}
     */
    public static final String ELSE = "else";

    /**
     * {@value #ENUM}
     */
    public static final String ENUM = "enum";

    /**
     * {@value #EXTENDS}
     */
    public static final String EXTENDS = "extends";

    /**
     * {@value #FALSE}
     */
    public static final String FALSE = "false";

    /**
     * {@value #FINAL}
     */
    public static final String FINAL = "final";

    /**
     * {@value #FINALLY}
     */
    public static final String FINALLY = "finally";

    /**
     * {@value #FLOAT}
     */
    public static final String FLOAT = "float";

    /**
     * {@value #FOR}
     */
    public static final String FOR = "for";

    /**
     * {@value #GOTO}
     */
    public static final String GOTO = "goto";

    /**
     * {@value #IF}
     */
    public static final String IF = "if";

    /**
     * {@value #IMPLEMENTS}
     */
    public static final String IMPLEMENTS = "implements";

    /**
     * {@value #IMPORT}
     */
    public static final String IMPORT = "import";

    /**
     * {@value #INSTANCEOF}
     */
    public static final String INSTANCEOF = "instanceof";

    /**
     * {@value #INT}
     */
    public static final String INT = "int";

    /**
     * {@value #INTERFACE}
     */
    public static final String INTERFACE = "interface";

    /**
     * {@value #LONG}
     */
    public static final String LONG = "long";

    /**
     * {@value #NATIVE}
     */
    public static final String NATIVE = "native";

    /**
     * {@value #NEW}
     */
    public static final String NEW = "new";

    /**
     * {@value #NULL}
     */
    public static final String NULL = "null";

    /**
     * {@value #PACKAGE}
     */
    public static final String PACKAGE = "package";

    /**
     * {@value #PRIVATE}
     */
    public static final String PRIVATE = "private";

    /**
     * {@value #PROTECTED}
     */
    public static final String PROTECTED = "protected";

    /**
     * {@value #PUBLIC}
     */
    public static final String PUBLIC = "public";

    /**
     * {@value #RETURN}
     */
    public static final String RETURN = "return";

    /**
     * {@value #SHORT}
     */
    public static final String SHORT = "short";

    /**
     * {@value #STATIC}
     */
    public static final String STATIC = "static";

    /**
     * {@value #STRICTFP}
     */
    public static final String STRICTFP = "strictfp";

    /**
     * {@value #SUPER}
     */
    public static final String SUPER = "super";

    /**
     * {@value #SWITCH}
     */
    public static final String SWITCH = "switch";

    /**
     * {@value #SYNCHRONIZED}
     */
    public static final String SYNCHRONIZED = "synchronized";

    /**
     * {@value #THIS}
     */
    public static final String THIS = "this";

    /**
     * {@value #THROW}
     */
    public static final String THROW = "throw";

    /**
     * {@value #THROWS}
     */
    public static final String THROWS = "throws";

    /**
     * {@value #TRANSIENT}
     */
    public static final String TRANSIENT = "transient";

    /**
     * {@value #TRUE}
     */
    public static final String TRUE = "true";

    /**
     * {@value #TRY}
     */
    public static final String TRY = "try";

    /**
     * {@value #VOID}
     */
    public static final String VOID = "void";

    /**
     * {@value #VOLATILE}
     */
    public static final String VOLATILE = "volatile";

    /**
     * {@value #WHILE}
     */
    public static final String WHILE = "while";
}
/*
 * $Log: not supported by cvs2svn $
 */
