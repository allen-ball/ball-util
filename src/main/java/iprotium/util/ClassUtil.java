/*
 * $Id$
 *
 * Copyright 2010, 2011 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

/**
 * {@link Class} utility methods.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public abstract class ClassUtil {
    private ClassUtil() { }

    /**
     * See {@link Modifier#isAbstract(int)}.
     */
    public static boolean isAbstract(Class<?> type) {
        return Modifier.isAbstract(type.getModifiers());
    }

    /**
     * See {@link Modifier#isAbstract(int)}.
     */
    public static boolean isAbstract(Member member) {
        return Modifier.isAbstract(member.getModifiers());
    }

    /**
     * See {@link Modifier#isFinal(int)}.
     */
    public static boolean isFinal(Class<?> type) {
        return Modifier.isFinal(type.getModifiers());
    }

    /**
     * See {@link Modifier#isFinal(int)}.
     */
    public static boolean isFinal(Member member) {
        return Modifier.isFinal(member.getModifiers());
    }

    /**
     * See {@link Modifier#isPublic(int)}.
     */
    public static boolean isPublic(Class<?> type) {
        return Modifier.isPublic(type.getModifiers());
    }

    /**
     * See {@link Modifier#isPublic(int)}.
     */
    public static boolean isPublic(Member member) {
        return Modifier.isPublic(member.getModifiers());
    }

    /**
     * See {@link Modifier#isStatic(int)}.
     */
    public static boolean isStatic(Class<?> type) {
        return Modifier.isStatic(type.getModifiers());
    }

    /**
     * See {@link Modifier#isStatic(int)}.
     */
    public static boolean isStatic(Member member) {
        return Modifier.isStatic(member.getModifiers());
    }

    /**
     * See {@link Modifier#isNative(int)}.
     */
    public static boolean isNative(Class<?> type) {
        return Modifier.isNative(type.getModifiers());
    }

    /**
     * See {@link Modifier#isNative(int)}.
     */
    public static boolean isNative(Member member) {
        return Modifier.isNative(member.getModifiers());
    }
}
