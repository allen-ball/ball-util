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
import lombok.NoArgsConstructor;
import lombok.ToString;

import static org.apache.commons.lang3.reflect.MethodUtils.invokeMethod;

/**
 * Default {@link InvocationHandler} implementation.
 * See {@link #invoke(Object,Method,Object[])}.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@NoArgsConstructor @ToString
public class DefaultInvocationHandler implements InvocationHandler {

    /**
     * Method to call
     * {@link Proxy#newProxyInstance(ClassLoader,Class[],InvocationHandler)}
     * with {@link.this} {@link InvocationHandler}.
     *
     * @param   interfaces      The interface {@link Class}es the
     *                          {@link Proxy} {@link Class} will implement.
     *
     * @return  The {@link Proxy}.
     */
    public Object newProxyInstance(Class<?>... interfaces) {
        return Proxy.newProxyInstance(getClass().getClassLoader(),
                                      interfaces, this);
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
}
