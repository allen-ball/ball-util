/*
 * $Id$
 *
 * Copyright 2010 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Manifest;

/**
 * {@link Class} utility methods.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class ClassUtil {
    private static final String _CLASS = ".class";

    private ClassUtil() { }

    /**
     * Static method to get a {@link Class} for a {@link Type}.
     *
     * @param   type            The {@link Type}.
     *
     * @return  The corresponding {@link Class}.
     */
    public static Class<?> getClass(Type type) {
        Class<?> cls = null;

        if (type instanceof Class) {
            cls = (Class) type;
        } else if (type instanceof ParameterizedType) {
            cls = getClass(((ParameterizedType) type).getRawType());
        } else if (type instanceof GenericArrayType) {
            cls =
                getClass(((GenericArrayType) type).getGenericComponentType());

            if (cls != null ) {
                cls = Array.newInstance(cls, 0).getClass();
            }
        }

        return cls;
    }

    /**
     * Static method to get the type arguments ({@link Class}es) for a
     * sub-{@link Class} specified by the type parameters of a
     * super-{@link Class}.
     *
     * @param   superclass      The super-{@link Class}.
     * @param   subclass        The sub-{@link Class}.
     *
     * @param   <T>             The super-{@link Class} type.
     *
     * @return  The {@link List} of resolved types.
     */
    public static <T> List<Class<?>> getTypeArguments(Class<T> superclass,
                                                      Class<? extends T> subclass) {
        ArrayList<Class<?>> list = new ArrayList<Class<?>>();
        Map<Type,Type> map = getResolvedTypeMap(superclass, subclass);
System.out.println(map);
        Type type = superclass;
        Type[] arguments =
            (type instanceof Class)
                ? ((Class) type).getTypeParameters()
                : ((ParameterizedType) type).getActualTypeArguments();
System.out.println(java.util.Arrays.asList(arguments));
        for (Type argument: arguments) {
            while (map.containsKey(argument)) {
                argument = map.get(argument);
            }

            list.add(getClass(argument));
        }

        return list;
    }

    private static <T> Map<Type,Type> getResolvedTypeMap(Class<T> superclass,
                                                         Class<? extends T> subclass) {
        HashMap<Type,Type> map = new HashMap<Type,Type>();
        Type type = subclass;

        while (! getClass(type).equals(superclass)) {
            if (type instanceof Class) {
                type = ((Class) type).getGenericSuperclass();
            } else {
                ParameterizedType parameterized = (ParameterizedType) type;
                Class<?> raw = (Class) parameterized.getRawType();
                TypeVariable<?>[] parameters = raw.getTypeParameters();
                Type[] arguments = parameterized.getActualTypeArguments();

                for (int i = 0; i < arguments.length; i += 1) {
                    map.put(parameters[i], arguments[i]);
                }

                if (! raw.equals(superclass)) {
                    type = raw.getGenericSuperclass();
                }
            }
        }

        return map;
    }

    /**
     * Method to get {@link Manifest} for a {@link Class}.
     *
     * @param   type            The {@link Class}.
     *
     * @return  The {@link Manifest} if the {@code MANIFEST.MF} file is
     *          located and parsed; {@code null} otherwise.
     */
    public static Manifest getManifestFor(Class<?> type) {
        Manifest manifest = null;
        URL url = getManifestURLFor(type);

        try (InputStream in = url.openStream()) {
            manifest = new Manifest(in);
        } catch (IOException exception) {
        }

        return manifest;
    }

    /**
     * Method to locate the {@code MAINFEST.MF} for a {@link Class}.
     *
     * @param   type            The {@link Class}.
     *
     * @return  The {@link URL} if the {@code MANIFEST.MF} file is located;
     *          {@code null} otherwise.
     */
    public static URL getManifestURLFor(Class<?> type) {
        URL url = null;
        String resource = type.getSimpleName() + _CLASS;
        String path = type.getResource(resource).toString();

        path =
            path.substring(0,
                           path.length()
                           - (type.getCanonicalName().length()
                              + _CLASS.length()))
            + "META-INF/MANIFEST.MF";

        try {
            url = new URL(path);
        } catch (MalformedURLException exception) {
        }

        return url;
    }

    /**
     * See {@link Modifier#isAbstract(int)}.
     *
     * @param   type            The {@link Class} to test.
     *
     * @return  {@code true} if the argument is {@code abstract};
     *          {@code false} otherwise.
     */
    public static boolean isAbstract(Class<?> type) {
        return Modifier.isAbstract(type.getModifiers());
    }

    /**
     * See {@link Modifier#isAbstract(int)}.
     *
     * @param   member          The {@link Member} to test.
     *
     * @return  {@code true} if the argument is {@code abstract};
     *          {@code false} otherwise.
     */
    public static boolean isAbstract(Member member) {
        return Modifier.isAbstract(member.getModifiers());
    }

    /**
     * See {@link Modifier#isFinal(int)}.
     *
     * @param   type            The {@link Class} to test.
     *
     * @return  {@code true} if the argument is {@code final};
     *          {@code false} otherwise.
     */
    public static boolean isFinal(Class<?> type) {
        return Modifier.isFinal(type.getModifiers());
    }

    /**
     * See {@link Modifier#isFinal(int)}.
     *
     * @param   member          The {@link Member} to test.
     *
     * @return  {@code true} if the argument is {@code final};
     *          {@code false} otherwise.
     */
    public static boolean isFinal(Member member) {
        return Modifier.isFinal(member.getModifiers());
    }

    /**
     * See {@link Modifier#isInterface(int)}.
     *
     * @param   type            The {@link Class} to test.
     *
     * @return  {@code true} if the argument is an {@code interface};
     *          {@code false} otherwise.
     */
    public static boolean isInterface(Class<?> type) {
        return Modifier.isInterface(type.getModifiers());
    }

    /**
     * See {@link Modifier#isInterface(int)}.
     *
     * @param   member          The {@link Member} to test.
     *
     * @return  {@code true} if the argument is an {@code interface};
     *          {@code false} otherwise.
     */
    public static boolean isInterface(Member member) {
        return Modifier.isInterface(member.getModifiers());
    }

    /**
     * See {@link Modifier#isNative(int)}.
     *
     * @param   type            The {@link Class} to test.
     *
     * @return  {@code true} if the argument is {@code native};
     *          {@code false} otherwise.
     */
    public static boolean isNative(Class<?> type) {
        return Modifier.isNative(type.getModifiers());
    }

    /**
     * See {@link Modifier#isNative(int)}.
     *
     * @param   member          The {@link Member} to test.
     *
     * @return  {@code true} if the argument is {@code native};
     *          {@code false} otherwise.
     */
    public static boolean isNative(Member member) {
        return Modifier.isNative(member.getModifiers());
    }

    /**
     * See {@link Modifier#isPrivate(int)}.
     *
     * @param   type            The {@link Class} to test.
     *
     * @return  {@code true} if the argument is {@code private};
     *          {@code false} otherwise.
     */
    public static boolean isPrivate(Class<?> type) {
        return Modifier.isPrivate(type.getModifiers());
    }

    /**
     * See {@link Modifier#isPrivate(int)}.
     *
     * @param   member          The {@link Member} to test.
     *
     * @return  {@code true} if the argument is {@code private};
     *          {@code false} otherwise.
     */
    public static boolean isPrivate(Member member) {
        return Modifier.isPrivate(member.getModifiers());
    }

    /**
     * See {@link Modifier#isProtected(int)}.
     *
     * @param   type            The {@link Class} to test.
     *
     * @return  {@code true} if the argument is {@code protected};
     *          {@code false} otherwise.
     */
    public static boolean isProtected(Class<?> type) {
        return Modifier.isProtected(type.getModifiers());
    }

    /**
     * See {@link Modifier#isProtected(int)}.
     *
     * @param   member          The {@link Member} to test.
     *
     * @return  {@code true} if the argument is {@code protected};
     *          {@code false} otherwise.
     */
    public static boolean isProtected(Member member) {
        return Modifier.isProtected(member.getModifiers());
    }

    /**
     * See {@link Modifier#isPublic(int)}.
     *
     * @param   type            The {@link Class} to test.
     *
     * @return  {@code true} if the argument is {@code public};
     *          {@code false} otherwise.
     */
    public static boolean isPublic(Class<?> type) {
        return Modifier.isPublic(type.getModifiers());
    }

    /**
     * See {@link Modifier#isPublic(int)}.
     *
     * @param   member          The {@link Member} to test.
     *
     * @return  {@code true} if the argument is {@code public};
     *          {@code false} otherwise.
     */
    public static boolean isPublic(Member member) {
        return Modifier.isPublic(member.getModifiers());
    }

    /**
     * See {@link Modifier#isStatic(int)}.
     *
     * @param   type            The {@link Class} to test.
     *
     * @return  {@code true} if the argument is {@code static};
     *          {@code false} otherwise.
     */
    public static boolean isStatic(Class<?> type) {
        return Modifier.isStatic(type.getModifiers());
    }

    /**
     * See {@link Modifier#isStatic(int)}.
     *
     * @param   member          The {@link Member} to test.
     *
     * @return  {@code true} if the argument is {@code static};
     *          {@code false} otherwise.
     */
    public static boolean isStatic(Member member) {
        return Modifier.isStatic(member.getModifiers());
    }

    /**
     * See {@link Modifier#isStrict(int)}.
     *
     * @param   type            The {@link Class} to test.
     *
     * @return  {@code true} if the argument is {@code strict};
     *          {@code false} otherwise.
     */
    public static boolean isStrict(Class<?> type) {
        return Modifier.isStrict(type.getModifiers());
    }

    /**
     * See {@link Modifier#isStrict(int)}.
     *
     * @param   member          The {@link Member} to test.
     *
     * @return  {@code true} if the argument is {@code strict};
     *          {@code false} otherwise.
     */
    public static boolean isStrict(Member member) {
        return Modifier.isStrict(member.getModifiers());
    }

    /**
     * See {@link Modifier#isSynchronized(int)}.
     *
     * @param   type            The {@link Class} to test.
     *
     * @return  {@code true} if the argument is {@code synchronized};
     *          {@code false} otherwise.
     */
    public static boolean isSynchronized(Class<?> type) {
        return Modifier.isSynchronized(type.getModifiers());
    }

    /**
     * See {@link Modifier#isSynchronized(int)}.
     *
     * @param   member          The {@link Member} to test.
     *
     * @return  {@code true} if the argument is {@code synchronized};
     *          {@code false} otherwise.
     */
    public static boolean isSynchronized(Member member) {
        return Modifier.isSynchronized(member.getModifiers());
    }

    /**
     * See {@link Modifier#isTransient(int)}.
     *
     * @param   type            The {@link Class} to test.
     *
     * @return  {@code true} if the argument is {@code transient};
     *          {@code false} otherwise.
     */
    public static boolean isTransient(Class<?> type) {
        return Modifier.isTransient(type.getModifiers());
    }

    /**
     * See {@link Modifier#isTransient(int)}.
     *
     * @param   member          The {@link Member} to test.
     *
     * @return  {@code true} if the argument is {@code transient};
     *          {@code false} otherwise.
     */
    public static boolean isTransient(Member member) {
        return Modifier.isTransient(member.getModifiers());
    }

    /**
     * See {@link Modifier#isVolatile(int)}.
     *
     * @param   type            The {@link Class} to test.
     *
     * @return  {@code true} if the argument is {@code volaile};
     *          {@code false} otherwise.
     */
    public static boolean isVolatile(Class<?> type) {
        return Modifier.isVolatile(type.getModifiers());
    }

    /**
     * See {@link Modifier#isVolatile(int)}.
     *
     * @param   member          The {@link Member} to test.
     *
     * @return  {@code true} if the argument is {@code volaile};
     *          {@code false} otherwise.
     */
    public static boolean isVolatile(Member member) {
        return Modifier.isVolatile(member.getModifiers());
    }
}
