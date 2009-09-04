/*
 * $Id: Factory.java,v 1.6 2009-09-04 17:13:43 ball Exp $
 *
 * Copyright 2008, 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Factory base class.
 *
 * @param       <T>             The type of {@link Object} this
 *                              {@link Factory} will produce.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.6 $
 */
public class Factory<T> {
    private final Class<? extends T> type;
    private final FactoryMemberMap map;

    /**
     * Sole constructor.
     *
     * @param   type            The type ({@link Class} of {@link Object}
     *                          this {@link Factory} will produce.
     */
    public Factory(Class<? extends T> type) {
        this.type = type;

        map = new FactoryMemberMap(getType());
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
     * Method to get a factory {@link Member} ({@link Constructor} or static
     * {@link Method}) to manufacture or get an {@link Object}.
     *
     * @return  The factory {@link Member}.
     *
     * @throws  NoSuchMethodException
     *                          If the specified {@link Method} or
     *                          {@link Constructor} does not exist.
     */
    public Member getFactoryMember(Class<?>[] parameters)
            throws NoSuchMethodException {
        if (! map.containsKey(parameters)) {
            map.put(parameters, getType().getConstructor(parameters));
        }

        return map.get(parameters);
    }

    /**
     * Method to apply a factory {@link Member} ({@link Constructor} or
     * static {@link Method}) to manufacture or get an {@link Object}.
     *
     * @param   member          The {@link Member} ({@link Constructor} or
     *                          static {@link Method}) to invoke.
     * @param   arguments       The array of arguments to apply.
     *
     * @return  The {@link Object} instance.
     *
     * @throws  InstantiationException
     *                          If the underlying {@link Method} or
     *                          {@link Constructor} represents an abstract
     *                          {@link Class}.
     * @throws  IllegalAccessException
     *                          If the specified {@link Method} or
     *                          {@link Constructor} enforces Java language
     *                          access control and the underlying
     *                          {@link Method} or {@link Constructor} is
     *                          inaccessible.
     * @throws  InvocationTargetException
     *                          If the underlying {@link Method} or
     *                          {@link Constructor} fails for some reason.
     */
    public T apply(Member member, Object[] arguments)
            throws InstantiationException, IllegalAccessException,
                   InvocationTargetException {
        Object object = null;

        if (member instanceof Constructor) {
            object = ((Constructor) member).newInstance(arguments);
        } else if (member instanceof Method) {
            object = ((Method) member).invoke(null, arguments);
        } else if (member instanceof Field) {
            object = ((Field) member).get(null);
        } else {
            throw new IllegalArgumentException("member=" + member);
        }

        return getType().cast(object);
    }

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
     * @throws  NoSuchMethodException
     *                          If the specified {@link Method} or
     *                          {@link Constructor} does not exist.
     * @throws  InstantiationException
     *                          If the underlying {@link Method} or
     *                          {@link Constructor} represents an abstract
     *                          {@link Class}.
     * @throws  IllegalAccessException
     *                          If the specified {@link Method} or
     *                          {@link Constructor} enforces Java language
     *                          access control and the underlying
     *                          {@link Method} or {@link Constructor} is
     *                          inaccessible.
     * @throws  IllegalArgumentException
     *                          If the number of actual and formal parameters
     *                          differ; if an unwrapping conversion for
     *                          primitive arguments fails; or if, after
     *                          possible unwrapping, a parameter value cannot
     *                          be converted to the corresponding formal
     *                          parameter type by a method invocation
     *                          conversion; if this {@link Method} or
     *                          {@link Constructor} pertains to an
     *                          {@link Enum} type.
     * @throws  InvocationTargetException
     *                          If the underlying {@link Method} or
     *                          {@link Constructor} fails for some reason.
     */
    public T getInstance(Class<?>[] parameters, Object[] arguments)
            throws NoSuchMethodException,
                   InstantiationException, IllegalAccessException,
                   IllegalArgumentException, InvocationTargetException {
        return apply(getFactoryMember(parameters), arguments);
    }

    /**
     * Method to get an {@link Object} instance.  This method will first
     * attempt to find and invoke a static factory method.  If not factory
     * method is found, it will then attempt to construct a new instance.
     *
     * @param   arguments       The arguments to the {@link Object} static
     *                          factory method or constructor.
     *
     * @throws  NoSuchMethodException
     *                          If the specified {@link Method} or
     *                          {@link Constructor} does not exist.
     * @throws  InstantiationException
     *                          If the underlying {@link Method} or
     *                          {@link Constructor} represents an abstract
     *                          {@link Class}.
     * @throws  IllegalAccessException
     *                          If the specified {@link Method} or
     *                          {@link Constructor} enforces Java language
     *                          access control and the underlying
     *                          {@link Method} or {@link Constructor} is
     *                          inaccessible.
     * @throws  IllegalArgumentException
     *                          If the number of actual and formal parameters
     *                          differ; if an unwrapping conversion for
     *                          primitive arguments fails; or if, after
     *                          possible unwrapping, a parameter value cannot
     *                          be converted to the corresponding formal
     *                          parameter type by a method invocation
     *                          conversion; if this {@link Method} or
     *                          {@link Constructor} pertains to an
     *                          {@link Enum} type.
     * @throws  InvocationTargetException
     *                          If the underlying {@link Method} or
     *                          {@link Constructor} fails for some reason.
     */
    public T getInstance(Object... arguments)
            throws NoSuchMethodException,
                   InstantiationException, IllegalAccessException,
                   IllegalArgumentException, InvocationTargetException {
        return getInstance(typesOf(arguments), arguments);
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
     * @return  <code>true</code> if the length of the argument array is the
     *          same as the length of the parameter array and each parameter
     *          is assignable from its corresponding argument;
     *          <code>false</code> otherwise.
     */
    protected static boolean isApplicable(Class<?>[] parameters,
                                          Class<?>[] arguments) {
        boolean match = (parameters.length == arguments.length);

        for (int i = 0; match && i < arguments.length; i += 1) {
            match &= parameters[i].isAssignableFrom(arguments[i]);
        }

        return match;
    }

    /**
     * Convenience method to determine if a {@link Member} is public.
     *
     * @param   member          The {@link Member}.
     *
     * @return  <code>true</code> if the {@link Member} is public;
     *          <code>false</code> otherwise.
     */
    protected static boolean isPublic(Member member) {
        return Modifier.isPublic(member.getModifiers());
    }

    /**
     * Convenience method to determine if a {@link Member} is static.
     *
     * @param   member          The {@link Member}.
     *
     * @return  <code>true</code> if the {@link Member} is static;
     *          <code>false</code> otherwise.
     */
    protected static boolean isStatic(Member member) {
        return Modifier.isStatic(member.getModifiers());
    }

    private class FactoryMemberMap extends TreeMap<Class<?>[],Member> {
        private static final long serialVersionUID = 3396324212973830506L;

        public FactoryMemberMap(Class<? extends T> type) {
            super(new ArrayOrder<Class<?>>(ClassOrder.NAME));

            for (Constructor<?> constructor : type.getConstructors()) {
                if (isPublic(constructor)) {
                    put(constructor.getParameterTypes(), constructor);
                }
            }

            Set<String> set = new TreeSet<String>();

            set.add("compile");
            set.add("create");
            set.add("forName");
            set.add("get" + type.getSimpleName());
            set.add("getDefault");
            set.add("getDefaultInstance");
            set.add("getInstance");
            set.add("valueOf");

            for (Method method : type.getMethods()) {
                if (isPublic(method) && isStatic(method)) {
                    if (type.isAssignableFrom(method.getReturnType())) {
                        if (set.contains(method.getName())) {
                            put(method.getParameterTypes(), method);
                        }
                    }
                }
            }
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
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
