/*
 * $Id$
 *
 * Copyright 2019 Allen D. Ball.  All rights reserved.
 */
package ball.lang.reflect;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static org.apache.commons.lang3.ClassUtils.getAllInterfaces;
import static org.apache.commons.lang3.reflect.MethodUtils.invokeMethod;

/**
 * Default {@link InvocationHandler} implementation.
 * See {@link #invoke(Object,Method,Object[])}.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@NoArgsConstructor @ToString
public class DefaultInvocationHandler implements InvocationHandler {
    private final HashMap<Class<?>,List<Class<?>>> cache = new HashMap<>();

    /**
     * See {@link Proxy#getProxyClass(ClassLoader,Class[])}.
     *
     * @param   interfaces      The interface {@link Class}es the
     *                          {@link Proxy} {@link Class} will implement.
     *
     * @return  The {@link Proxy}.
     */
    public Class<?> getProxyClass(Class<?>... interfaces) throws IllegalArgumentException {
        return Proxy.getProxyClass(getClass().getClassLoader(), interfaces);
    }

    /**
     * See
     * {@link Proxy#newProxyInstance(ClassLoader,Class[],InvocationHandler)}.
     * Default implementation invokes {@link #getProxyClass(Class...)}.
     *
     * @param   interfaces      The interface {@link Class}es the
     *                          {@link Proxy} {@link Class} will implement.
     *
     * @return  The {@link Proxy}.
     */
    public Object newProxyInstance(Class<?>... interfaces) throws IllegalArgumentException {
        Object proxy = null;

        try {
            proxy =
                getProxyClass(interfaces)
                .getConstructor(InvocationHandler.class)
                .newInstance(this);
        } catch (IllegalArgumentException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }

        return proxy;
    }

    /**
     * {@inheritDoc}
     *
     * If the {@link Method#isDefault() method.isDefault()}, that
     * {@link Method} will be invoked directly.  If the {@link Method} is
     * declared in {@link Object}, it is applied to {@link.this}
     * {@link InvocationHandler}.  Otherwise, the call will be dispatched to
     * a declared {@link Method} on {@link.this} {@link InvocationHandler}
     * with the same name and compatible parameter types (forcing access if
     * necessary).
     *
     * @throws  Exception       If no compatible {@link Method} is found or
     *                          the {@link Method} cannot be invoked.
     */
    @Override
    public Object invoke(Object proxy,
                         Method method, Object[] argv) throws Throwable {
        Object result = null;
        Class<?> declarer = method.getDeclaringClass();

        if (method.isDefault()) {
            Constructor<MethodHandles.Lookup> constructor =
                MethodHandles.Lookup.class
                .getDeclaredConstructor(Class.class);

            constructor.setAccessible(true);

            result =
                constructor.newInstance(declarer)
                .in(declarer)
                .unreflectSpecial(method, declarer)
                .bindTo(proxy)
                .invokeWithArguments(argv);
        } else if (declarer.equals(Object.class)) {
            result = method.invoke(this, argv);
        } else {
            result =
                invokeMethod(this, true,
                             method.getName(),
                             argv, method.getParameterTypes());
        }

        return result;
    }

    /**
     * Method available to subclass implementations to get the implemented
     * interfaces of the argument {@link Class types}.  The default
     * implementation caches the results.
     *
     * @param   type            The {@link Class} to analyze.
     * @param   types           Additional {@link Class}es to analyze.
     *
     * @return  The {@link List} of interface {@link Class}es.
     */
    protected List<Class<?>> getImplementedInterfacesOf(Class<?> type,
                                                        Class<?>... types) {
        Set<Class<?>> set =
            Stream.concat(Stream.of(type), Arrays.stream(types))
            .filter(Objects::nonNull)
            .flatMap(t -> cache.computeIfAbsent(t, k -> compute(k)).stream())
            .collect(Collectors.toCollection(LinkedHashSet::new));

        return new ArrayList<>(set);
    }

    private List<Class<?>> compute(Class<?> type) {
        Set<Class<?>> set =
            Stream.concat(Stream.of(type), getAllInterfaces(type).stream())
            .filter(Class::isInterface)
            .collect(Collectors.toCollection(LinkedHashSet::new));

        return new ArrayList<>(set);
    }
}
