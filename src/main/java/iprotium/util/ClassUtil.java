/*
 * $Id: ClassUtil.java,v 1.2 2010-07-28 05:14:36 ball Exp $
 *
 * Copyright 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

/**
 * {@link Class} utility methods.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.2 $
 */
public abstract class ClassUtil {
    private ClassUtil() { }

    /**
     * @see Modifier#isAbstract(int)
     */
    public static boolean isAbstract(Class<?> type) {
        return Modifier.isAbstract(type.getModifiers());
    }

    /**
     * @see Modifier#isAbstract(int)
     */
    public static boolean isAbstract(Member member) {
        return Modifier.isAbstract(member.getModifiers());
    }

    /**
     * @see Modifier#isFinal(int)
     */
    public static boolean isFinal(Class<?> type) {
        return Modifier.isFinal(type.getModifiers());
    }

    /**
     * @see Modifier#isFinal(int)
     */
    public static boolean isFinal(Member member) {
        return Modifier.isFinal(member.getModifiers());
    }

    /**
     * @see Modifier#isPublic(int)
     */
    public static boolean isPublic(Class<?> type) {
        return Modifier.isPublic(type.getModifiers());
    }

    /**
     * @see Modifier#isPublic(int)
     */
    public static boolean isPublic(Member member) {
        return Modifier.isPublic(member.getModifiers());
    }

    /**
     * @see Modifier#isStatic(int)
     */
    public static boolean isStatic(Class<?> type) {
        return Modifier.isStatic(type.getModifiers());
    }

    /**
     * @see Modifier#isStatic(int)
     */
    public static boolean isStatic(Member member) {
        return Modifier.isStatic(member.getModifiers());
    }

    /**
     * @see Modifier#isNative(int)
     */
    public static boolean isNative(Class<?> type) {
        return Modifier.isNative(type.getModifiers());
    }

    /**
     * @see Modifier#isNative(int)
     */
    public static boolean isNative(Member member) {
        return Modifier.isNative(member.getModifiers());
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
