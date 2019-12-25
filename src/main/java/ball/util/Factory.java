/*
 * $Id$
 *
 * Copyright 2008 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import java.beans.ConstructorProperties;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import static java.util.Comparator.comparing;
import static java.lang.reflect.Modifier.isStatic;
import static java.lang.reflect.Modifier.isPublic;

/**
 * {@link Factory} base class.
 *
 * @param       <T>             The type of {@link Object} this
 *                              {@link Factory} will produce.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public class Factory<T> extends TreeMap<Class<?>[],Member> {
    private static final long serialVersionUID = -5733222257965875050L;

    /** @serial */ private final Class<? extends T> type;
    /** @serial */ private final Object factory;

    /**
     * Sole public constructor.
     *
     * @param   type            The {@link Class} of {@link Object} this
     *                          {@link Factory} will produce.
     *
     * @throws  NullPointerException
     *                          If {@code type} is {@code null}.
     */
    @ConstructorProperties({ "type" })
    public Factory(Class<? extends T> type) { this(type, null); }

    /**
     * Construct a {@link Factory} by wrapping a factory instance.
     *
     * @param   type            The {@link Class} of {@link Object} this
     *                          {@link Factory} will produce.
     * @param   factory         An {@link Object} factory for this
     *                          {@code type} (may be {@code null}).
     *
     * @throws  NullPointerException
     *                          If {@code type} is {@code null}.
     */
    @ConstructorProperties({ "type", "factory" })
    protected Factory(Class<? extends T> type, Object factory) {
        super(comparing(Arrays::toString));

        this.type = type;
        this.factory = factory;

        CandidateSet set = new CandidateSet(type);

        if (factory != null) {
            Arrays.stream(factory.getClass().getMethods())
                .filter(t -> isPublic(t.getModifiers()))
                .filter(t -> type.isAssignableFrom(t.getReturnType()))
                .filter(t -> set.contains(t.getName()))
                .forEach(t -> putIfAbsent(t.getParameterTypes(), t));
        }

        Arrays.stream(type.getMethods())
            .filter(t -> (isPublic(t.getModifiers())
                          && isStatic(t.getModifiers())))
            .filter(t -> type.isAssignableFrom(t.getReturnType()))
            .filter(t -> (set.contains(t.getName())))
            .forEach(t -> putIfAbsent(t.getParameterTypes(), t));

        Arrays.stream(type.getConstructors())
            .filter(t -> isPublic(t.getModifiers()))
            .forEach(t -> putIfAbsent(t.getParameterTypes(), t));
    }

    /**
     * Method to get the type ({@link Class}) of objects produced by this
     * {@link Factory}.
     *
     * @return  The type of {@link Object} produced by this
     *          {@link Factory}.
     */
    public Class<? extends T> getType() { return type; }

    /**
     * Method to get the underlying factory {@link Object}.
     *
     * @return  The underlying factory {@link Object} or {@code null} if
     *          there is none.
     */
    public Object getFactory() { return factory; }

    /**
     * Method to get an {@link Object} instance.  This method will first
     * attempt to find and invoke a static factory method.  If no factory
     * method is found, it will then attempt to construct a new instance.
     *
     * @param   parameters      The parameter types to use to search for the
     *                          {@link Object} static factory method or
     *                          constructor.
     * @param   arguments       The arguments to the {@link Object} static
     *                          factory method or constructor.
     *
     * @return  The {@link Object} instance.
     *
     * @throws  IllegalAccessException
     *                          If the specified {@link Constructor} or
     *                          {@link Method} enforces Java language
     *                          access control and the underlying
     *                          {@link Constructor} or {@link Method} is
     *                          inaccessible.
     * @throws  IllegalArgumentException
     *                          If the number of actual and formal parameters
     *                          differ; if an unwrapping conversion for
     *                          primitive arguments fails; or if, after
     *                          possible unwrapping, a parameter value cannot
     *                          be converted to the corresponding formal
     *                          parameter type by a method invocation
     *                          conversion; if this {@link Constructor} or
     *                          {@link Method} pertains to an
     *                          {@link Enum} type.
     * @throws  InstantiationException
     *                          If the underlying {@link Constructor} or
     *                          {@link Method} represents an abstract
     *                          {@link Class}.
     * @throws  InvocationTargetException
     *                          If the underlying {@link Constructor} or
     *                          {@link Method} fails for some reason.
     * @throws  NoSuchMethodException
     *                          If the specified {@link Constructor} or
     *                          {@link Method} does not exist.
     */
    public T getInstance(Class<?>[] parameters, Object... arguments)
                                        throws IllegalAccessException,
                                               IllegalArgumentException,
                                               InstantiationException,
                                               InvocationTargetException,
                                               NoSuchMethodException {
        return apply(getFactoryMethod(parameters), arguments);
    }

    /**
     * Method to get an {@link Object} instance.  This method will first
     * attempt to find and invoke a static factory method.  If no factory
     * method is found, it will then attempt to construct a new instance.
     *
     * @param   arguments       The arguments to the {@link Object} static
     *                          factory method or constructor.
     *
     * @return  The {@link Object} instance.
     *
     * @throws  IllegalAccessException
     *                          If the specified {@link Constructor} or
     *                          {@link Method} enforces Java language
     *                          access control and the underlying
     *                          {@link Constructor} or {@link Method} is
     *                          inaccessible.
     * @throws  IllegalArgumentException
     *                          If the number of actual and formal parameters
     *                          differ; if an unwrapping conversion for
     *                          primitive arguments fails; or if, after
     *                          possible unwrapping, a parameter value cannot
     *                          be converted to the corresponding formal
     *                          parameter type by a method invocation
     *                          conversion; if this {@link Constructor} or
     *                          {@link Method} pertains to an
     *                          {@link Enum} type.
     * @throws  InstantiationException
     *                          If the underlying {@link Constructor} or
     *                          {@link Method} represents an abstract
     *                          {@link Class}.
     * @throws  InvocationTargetException
     *                          If the underlying {@link Constructor} or
     *                          {@link Method} fails for some reason.
     * @throws  NoSuchMethodException
     *                          If the specified {@link Constructor} or
     *                          {@link Method} does not exist.
     */
    public T getInstance(Object... arguments) throws IllegalAccessException,
                                                     IllegalArgumentException,
                                                     InstantiationException,
                                                     InvocationTargetException,
                                                     NoSuchMethodException {
        return getInstance(typesOf(arguments), arguments);
    }

    /**
     * Method to determine if there is a factory {@link Member} (factory
     * {@link Method}, static {@link Method}, or {@link Constructor}) to
     * manufacture or get an {@link Object}.
     *
     * @param   parameters      The {@link Constructor} or {@link Method}
     *                          parameter list.
     *
     * @return  {@code true} if there is such a {@link Member};
     *          {@code false} otherwise.
     */
    public boolean hasFactoryMethodFor(Class<?>... parameters) {
        boolean hasMember = false;

        try {
            getFactoryMethod(parameters);
            hasMember = (get(parameters) != null);
        } catch (NoSuchMethodException exception) {
            hasMember = false;
        }

        return hasMember;
    }

    /**
     * Method to get a factory {@link Member} (factory {@link Method},
     * static {@link Method}, or {@link Constructor}) to manufacture or get
     * an {@link Object}.
     *
     * @param   parameters      The {@link Constructor} or {@link Method}
     *                          parameter list.
     *
     * @return  The factory {@link Member}.
     *
     * @throws  NoSuchMethodException
     *                          If the specified {@link Constructor} or
     *                          {@link Method} does not exist.
     */
    public Member getFactoryMethod(Class<?>... parameters)
                                        throws NoSuchMethodException {
        if (! containsKey(parameters)) {
            put(parameters, getType().getConstructor(parameters));
        }

        return get(parameters);
    }

    /**
     * Method to apply a factory {@link Member} (factory {@link Method},
     * static {@link Method}, or {@link Constructor}) to manufacture or get
     * an {@link Object}.
     *
     * @param   member          The {@link Member} ({@link Constructor} or
     *                          static {@link Method}) to invoke.
     * @param   arguments       The array of arguments to apply.
     *
     * @return  The {@link Object} instance.
     *
     * @throws  IllegalAccessException
     *                          If the specified {@link Constructor} or
     *                          {@link Method} enforces Java language
     *                          access control and the underlying
     *                          {@link Constructor} or {@link Method} is
     *                          inaccessible.
     * @throws  InstantiationException
     *                          If the underlying {@link Constructor} or
     *                          {@link Method} represents an abstract
     *                          {@link Class}.
     * @throws  InvocationTargetException
     *                          If the underlying {@link Constructor} or
     *                          {@link Method} fails for some reason.
     */
    public T apply(Member member,
                   Object... arguments) throws IllegalAccessException,
                                               InstantiationException,
                                               InvocationTargetException {
        Object object = null;

        if (member instanceof Method) {
            object = ((Method) member).invoke(factory, arguments);
        } else if (member instanceof Constructor) {
            object = ((Constructor) member).newInstance(arguments);
        } else if (member instanceof Field) {
            object = ((Field) member).get(null);
        } else {
            throw new IllegalArgumentException("member=" + member);
        }

        return getType().cast(object);
    }

    @Override
    public Member get(Object key) {
        Member value = null;

        if (key instanceof Class<?>[]) {
            if (! super.containsKey(key)) {
                for (Map.Entry<Class<?>[],Member> entry : entrySet()) {
                    if (isApplicable(entry.getKey(), (Class<?>[]) key)) {
                        value = entry.getValue();
                        break;
                    }
                }

                super.put((Class<?>[]) key, value);
            }

            value = super.get(key);
        }

        return value;
    }

    /**
     * Convenience method to get the types of an argument array.
     *
     * @param   arguments       The argument array.
     *
     * @return  An array of types ({@link Class}s).
     */
    protected static Class<?>[] typesOf(Object... arguments) {
        Class<?>[] types = new Class<?>[arguments.length];

        for (int i = 0; i < types.length; i += 1) {
            types[i] = arguments[i].getClass();
        }

        return types;
    }

    /**
     * Method to determine if an argument of the specified types may be
     * applied to a method or constructor with the specified parameters.
     *
     * @see Class#isAssignableFrom(Class)
     *
     * @param   parameters      The parameter types.
     * @param   arguments       The argument types.
     *
     * @return  {@code true} if the length of the argument array is the same
     *          as the length of the parameter array and each parameter is
     *          assignable from its corresponding argument; {@code false}
     *          otherwise.
     */
    protected static boolean isApplicable(Class<?>[] parameters,
                                          Class<?>... arguments) {
        boolean match = (parameters.length == arguments.length);

        for (int i = 0; match && i < arguments.length; i += 1) {
            match &= parameters[i].isAssignableFrom(arguments[i]);
        }

        return match;
    }

    private class CandidateSet extends TreeSet<String> {
        private static final long serialVersionUID = -7927801377734740425L;

        public CandidateSet(Class<? extends T> type) {
            super(Arrays.asList("compile",
                                "create",
                                "decode",
                                "forName",
                                "getDefault",
                                "getDefaultInstance",
                                "getInstance",
                                "getObjectInstance",
                                "new" + type.getSimpleName(),
                                "newInstance",
                                "valueOf"));

            if (! (type.isAssignableFrom(Boolean.class)
                   || type.isAssignableFrom(Integer.class)
                   || type.isAssignableFrom(Long.class))) {
                add("get" + type.getSimpleName());
            }
        }
    }
}
