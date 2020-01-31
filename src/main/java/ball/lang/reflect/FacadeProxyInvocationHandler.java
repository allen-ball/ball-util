/*
 * $Id$
 *
 * Copyright 2019, 2020 Allen D. Ball.  All rights reserved.
 */
package ball.lang.reflect;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.IdentityHashMap;
import java.util.stream.Collectors;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static lombok.AccessLevel.PROTECTED;

/**
 * {@link InvocationHandler} abstract base class to "extend" concrete
 * implementation {@link Class}es by adding "facade" interfaces.  See
 * {@link #getProxyClassFor(Object)}.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@NoArgsConstructor(access = PROTECTED) @ToString
public abstract class FacadeProxyInvocationHandler
                      extends DefaultInvocationHandler {
    private final ProxyMap map = new ProxyMap();

    /**
     * Method to return an extended {@link Proxy} implementing "facade"
     * interfaces for {@code in} if {@link #getProxyClassFor(Object)}
     * returns non-{@code null}.
     *
     * @param   in              The {@link Object} to extend.
     *
     * @return  A {@link Proxy} if {@link #getProxyClassFor(Object)} returns
     *          non-{@code null}; {@code in} otherwise.
     */
    public Object enhance(Object in) {
        Object out = null;

        if (! hasFacade(in)) {
            Class<?> type = getProxyClassFor(in);

            if (type != null) {
                out = map.computeIfAbsent(in, k -> compute(type));
            }
        }

        return (out != null) ? out : in;
    }

    private <T> T compute(Class<T> type) {
        T proxy = null;

        try {
            proxy =
                type
                .getConstructor(InvocationHandler.class)
                .newInstance(this);
        } catch (RuntimeException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }

        return proxy;
    }

    /**
     * Method provided by subclasses to provide the {@link Proxy}
     * {@link Class} if the input {@link Object} should be extended.
     *
     * @param   object          The {@link Object} that may or may not be
     *                          extended.
     *
     * @return  A {@link Proxy} {@link Class} if the {@link Object} should
     *          be extended; {@code null} otherwise.
     */
    protected abstract Class<?> getProxyClassFor(Object object);

    private boolean hasFacade(Object object) {
        return reverseOf(object) != null;
    }

    private Object reverseOf(Object in) {
        Object out = null;

        if (in instanceof Proxy && Proxy.isProxyClass(in.getClass())) {
            InvocationHandler handler = Proxy.getInvocationHandler(in);

            if (handler instanceof FacadeProxyInvocationHandler) {
                out =
                    ((FacadeProxyInvocationHandler) handler)
                    .map.reverse().get(in);
            }
        }

        return out;
    }

    private Object reverseFor(Class<?> type, Object in) {
        Object out = null;

        if (out == null) {
            Object object = reverseOf(in);

            if (object != null && type.isAssignableFrom(object.getClass())) {
                out = object;
            }
        }

        if (out == null) {
            if (in instanceof Object[]) {
                if (type.isArray()) {
                    int length = ((Object[]) in).length;

                    out = Array.newInstance(type.getComponentType(), length);

                    for (int i = 0; i < length; i += 1) {
                        ((Object[]) out)[i] =
                            reverseFor(type.getComponentType(),
                                       ((Object[]) in)[i]);
                    }
                }
            }
        }

        return (out != null) ? out : in;
    }

    private Object[] reverseFor(Class<?>[] types, Object[] in) {
        Object[] out = null;

        if (in != null) {
            out = new Object[in.length];

            for (int i = 0; i < out.length; i += 1) {
                out[i] = reverseFor(types[i], in[i]);
            }
        }

        return (out != null) ? out : in;
    }

    @Override
    public Object invoke(Object proxy,
                         Method method, Object[] argv) throws Throwable {
        Object result = null;
        Class<?> declarer = method.getDeclaringClass();
        Object that = map.reverse.get(proxy);

        if (declarer.isAssignableFrom(Object.class)) {
            result = method.invoke(that, argv);
        } else {
            argv = reverseFor(method.getParameterTypes(), argv);

            if (declarer.isAssignableFrom(that.getClass())) {
                result = method.invoke(that, argv);
            } else {
                result = super.invoke(proxy, method, argv);
            }
        }

        return enhance(result);
    }

    @NoArgsConstructor
    private class ProxyMap extends IdentityHashMap<Object,Object> {
        private static final long serialVersionUID = 6708505296087349421L;

        private final IdentityHashMap<Object,Object> reverse =
            new IdentityHashMap<>();

        public IdentityHashMap<Object,Object> reverse() { return reverse; }

        @Override
        public Object put(Object key, Object value) {
            reverse().put(value, key);

            return super.put(key, value);
        }
    }
}
